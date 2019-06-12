/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import static com.crio.qeats.globals.GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS;
import static com.crio.qeats.globals.GlobalConstants.getJedisPool;
import static com.crio.qeats.utils.GeoUtils.findDistanceInKm;

import ch.hsr.geohash.GeoHash;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Primary
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // TODO: CRIO_TASK_MODULE_REDIS - Add cache support.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    if (GlobalConstants.isCacheAvailable()) {
      return findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime,
        servingRadiusInKms);
    } else {
      return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
    }
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
      LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();

    for (RestaurantEntity restaurant : restaurantRepository.findAll()) {
      if (findDistanceInKm(latitude, longitude,
          restaurant.getLatitude(), restaurant.getLongitude()) <= 3
          && isOpenNow(currentTime, restaurant)) {
        restaurant.setLatitude(latitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
        restaurant.setLongitude(longitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));

        Restaurant temp = new Restaurant();
        temp.setAttributes(restaurant.getAttributes());
        temp.setId(restaurant.getId());
        temp.setRestaurantId(restaurant.getRestaurantId());
        temp.setName(restaurant.getName());
        temp.setCity(restaurant.getCity());
        temp.setImageUrl(restaurant.getImageUrl());
        temp.setLatitude(restaurant.getLatitude());
        temp.setLongitude(restaurant.getLongitude());
        temp.setOpensAt(restaurant.getOpensAt());
        temp.setClosesAt(restaurant.getClosesAt());

        restaurantList.add(temp);
      }
    }

    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_REDIS - Implement caching.

  /**
   * Implement caching for restaurants closeby.
   * Whenever the entry is not there in the cache, you will have to populate it from DB.
   * If the entry is already available in the cache, then return it from cache to save DB lookup.
   * The cache entries should expire in GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS.
   * Make sure you use something like a GeoHash with a slightly lower precision,
   * so that for lat/long that are slightly close, the function returns the same set of restaurants.
   */
  private List<Restaurant> findAllRestaurantsCloseByFromCache(
      Double latitude, Double longitude, LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);

    if (jedis.exists(geoHash.toBase32())) {
      String strlist = jedis.get(geoHash.toBase32());
      ObjectMapper mapper = new ObjectMapper();
      List<Restaurant> restaurantList1 = new ArrayList<>();
      try {
        restaurantList1 = Arrays.asList(mapper.readValue(strlist, Restaurant[].class));
      } catch (IOException e) {
        e.printStackTrace();
      }

      for (int i = 0; i < restaurantList1.size(); i++) {
        if (isOpenNow(currentTime, change(restaurantList1.get(i)))) {
          restaurantList.add(restaurantList1.get(i));
        }
      }
      String listJsonString = " ";
      try {
        listJsonString = new ObjectMapper().writeValueAsString(restaurantList);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      jedis.setex(geoHash.toBase32(), REDIS_ENTRY_EXPIRY_IN_SECONDS, listJsonString);

    } else {
      restaurantList = findAllRestaurantsCloseFromDb(latitude,
        longitude, currentTime, servingRadiusInKms);
      try {
        String listJsonString = new ObjectMapper().writeValueAsString(restaurantList);
        jedis.setex(geoHash.toBase32(), REDIS_ENTRY_EXPIRY_IN_SECONDS, listJsonString);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }

    return restaurantList;
  }

  private RestaurantEntity change(Restaurant restaurant) {
    RestaurantEntity temp = new RestaurantEntity();
    temp.setAttributes(restaurant.getAttributes());
    temp.setId(restaurant.getId());
    temp.setRestaurantId(restaurant.getRestaurantId());
    temp.setName(restaurant.getName());
    temp.setCity(restaurant.getCity());
    temp.setImageUrl(restaurant.getImageUrl());
    temp.setLatitude(restaurant.getLatitude());
    temp.setLongitude(restaurant.getLongitude());
    temp.setOpensAt(restaurant.getOpensAt());
    temp.setClosesAt(restaurant.getClosesAt());

    return temp;
  }
}
