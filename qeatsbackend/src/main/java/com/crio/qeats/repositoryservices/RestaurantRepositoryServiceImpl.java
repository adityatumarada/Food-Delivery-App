/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.utils.GeoUtils;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static com.crio.qeats.globals.GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS;
import static com.crio.qeats.globals.GlobalConstants.getJedisPool;

@Service
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {



  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
    LocalTime openingTime = LocalTime.parse(res.getOpensAt());
    LocalTime closingTime = LocalTime.parse(res.getClosesAt());

    return time.isAfter(openingTime) && time.isBefore(closingTime);
  }

  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objectives:
  // 1. Implement findAllRestaurantsCloseby.
  // 2. Remember to keep the precision of GeoHash in mind while using it as a key.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {

    // TODO: CRIO_TASK_MODULE_REDIS
    // We want to use cache to speed things up. Write methods that perform the same functionality,
    // but using the cache if it is present and reachable.
    // Remember, you must ensure that if cache is not present, the queries are directed at the
    // database instead.
    List<Restaurant> restaurantList = new ArrayList<>();
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);

    //if cache exists
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
      //if cache is empty

      for (RestaurantEntity restaurant : mongoTemplate.findAll(RestaurantEntity.class)) {
        if (isRestaurantCloseByAndOpen(restaurant,currentTime,latitude,longitude,servingRadiusInKms)) {
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


      try {
        String listJsonString = new ObjectMapper().writeValueAsString(restaurantList);
        jedis.setex(geoHash.toBase32(), REDIS_ENTRY_EXPIRY_IN_SECONDS, listJsonString);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }

    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose names have an exact or partial match with the search query.
  @Override
  public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();
    if(searchString==null||searchString.isEmpty())
    {
      return restaurantList;
    }

    Query query = new Query();
    query.addCriteria(Criteria.where("item").regex("/" + searchString + "/", "i"));
    restaurantList.addAll(mongoTemplate.find(query, Restaurant.class));


    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose attributes (cuisines) intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();
    if(searchString==null)
    {
      return restaurantList;
    }

     return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose names form a complete or partial match
  // with the search query.
  @Override
  public List<Restaurant> findRestaurantsByItemName(
      Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();
    if(searchString==null)
    {
      return restaurantList;
    }

    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants which serve food items whose attributes intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
      String searchString, LocalTime currentTime, Double servingRadiusInKms) {
    List<Restaurant> restaurantList = new ArrayList<>();
    if(searchString==null||searchString.isEmpty())
    {
      return restaurantList;
    }

    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
      LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return GeoUtils.findDistanceInKm(latitude, longitude,
          restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
          < servingRadiusInKms;
    }

    return false;
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
