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
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;

import com.crio.qeats.models.MenuEntity;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.ItemRepository;
import com.crio.qeats.repositories.MenuRepository;
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
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @Autowired
  private MenuRepository menuRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private ItemRepository itemRepository;

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

    if (GlobalConstants.isCacheAvailable()) {
      return findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime,
        servingRadiusInKms);
    } else {
      return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
    }

  }

  private List<Restaurant> findAllRestaurantsCloseByFromCache(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    List<Restaurant> restaurantList = new ArrayList<>();
    JedisPool jedisPool = getJedisPool();
    Jedis jedis = jedisPool.getResource();
    GeoHash geoHash = GeoHash.withCharacterPrecision(latitude, longitude, 7);

    if (jedis.exists(geoHash.toBase32())) {
      String strlist = jedis.get(geoHash.toBase32());
      ObjectMapper mapper = new ObjectMapper();
      try {
        restaurantList = Arrays.asList(mapper.readValue(strlist, Restaurant[].class));
        String listJsonString = new ObjectMapper().writeValueAsString(restaurantList);
        jedis.setex(geoHash.toBase32(), REDIS_ENTRY_EXPIRY_IN_SECONDS, listJsonString);
      } catch (IOException e) {
        e.printStackTrace();
      }
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

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    List<Restaurant> restaurantList = new ArrayList<>();

    for (RestaurantEntity restaurant : restaurantRepository.findAll()) {
      if (findDistanceInKm(latitude, longitude,
          restaurant.getLatitude(), restaurant.getLongitude()) <= 3
          && isOpenNow(currentTime, restaurant)) {
        restaurant.setLatitude(latitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
        restaurant.setLongitude(longitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));

        restaurantList.add(changeto(restaurant));
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
    if (searchString == null || searchString.isEmpty()) {
      return restaurantList;
    }

    List<Restaurant> allrest = findAllRestaurantsCloseBy(latitude,
        longitude, currentTime, servingRadiusInKms);

    for (int i = 0; i < allrest.size(); i++) {
      if (allrest.get(i).getName().equals(searchString)) {
        restaurantList.add(allrest.get(i));
      }
    }

    for (int i = 0; i < allrest.size(); i++) {
      if (allrest.get(i).getName().contains(searchString)) {
        if (!restaurantList.contains(allrest.get(i))) {
          restaurantList.add(allrest.get(i));
        }
      }
    }

    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Objective:
  // Find restaurants whose attributes (cuisines) intersect with the search query.
  @Override
  public List<Restaurant> findRestaurantsByAttributes(
      Double latitude, Double longitude, String searchString,
      LocalTime currentTime, Double servingRadiusInKms) {

    List<Restaurant> restaurantList = new ArrayList<>();
    if (searchString == null) {
      return restaurantList;
    }

    List<Restaurant> allrest = findAllRestaurantsCloseBy(latitude,
        longitude, currentTime, servingRadiusInKms);

    for (int i = 0; i < allrest.size(); i++) {
      if (allrest.get(i).getAttributes().contains(searchString)) {
        restaurantList.add(allrest.get(i));
      }
    }

    for (int i = 0; i < allrest.size(); i++) {
      List<String> att = allrest.get(i).getAttributes();
      for (int j = 0; j < att.size(); j++) {
        if (att.get(j).contains(searchString)) {
          if (!restaurantList.contains(allrest.get(i))) {
            restaurantList.add(allrest.get(i));
            break;
          }
        }
      }
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
    if (searchString == null) {
      return restaurantList;
    }

    List<Restaurant> closerest = findAllRestaurantsCloseBy(latitude,
        longitude, currentTime, servingRadiusInKms);

    List<RestaurantEntity> allrest = restaurantRepository.findAll();


    List<MenuEntity> menus = menuRepository.findAll();
    //exact match
    for (int i = 0; i < menus.size(); i++) {
      List<Item> items = menus.get(i).getItems();
      for (int j = 0; j < items.size(); j++) {
        if (items.get(j).getName().equals(searchString)) {
          RestaurantEntity temp = new RestaurantEntity();
          for (int k = 0; k < allrest.size(); k++) {
            if (allrest.get(k).getRestaurantId().equals(menus.get(i).getRestaurantId())) {
              temp = allrest.get(j);
              break;
            }
          }

          if (closerest.contains(changeto(temp)) && !allrest.contains(temp)) {
            restaurantList.add(changeto(temp));
          }

        }
      }
    }
    //partial match
    for (int i = 0; i < menus.size(); i++) {
      List<Item> items = menus.get(i).getItems();
      for (int j = 0; j < items.size(); j++) {
        if (items.get(j).getName().contains(searchString)) {
          RestaurantEntity temp = new RestaurantEntity();
          for (int k = 0; k < allrest.size(); k++) {
            if (allrest.get(k).getRestaurantId().equals(menus.get(i).getRestaurantId())) {
              temp = allrest.get(j);
              break;
            }
          }

          if (closerest.contains(changeto(temp)) && !allrest.contains(temp)) {
            restaurantList.add(changeto(temp));
          }

        }
      }

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
    if (searchString == null || searchString.isEmpty()) {
      return restaurantList;
    }

    List<Restaurant> closerest = findAllRestaurantsCloseBy(latitude,
        longitude, currentTime, servingRadiusInKms);

    List<RestaurantEntity> allrest = restaurantRepository.findAll();


    List<MenuEntity> menus = menuRepository.findAll();
    //exact match
    for (int i = 0; i < menus.size(); i++) {
      List<Item> items = menus.get(i).getItems();
      for (int j = 0; j < items.size(); j++) {
        if (items.get(j).getAttributes().contains(searchString)) {
          RestaurantEntity temp = new RestaurantEntity();
          for (int k = 0; k < allrest.size(); k++) {
            if (allrest.get(k).getRestaurantId().equals(menus.get(i).getRestaurantId())) {
              temp = allrest.get(j);
              break;
            }
          }

          if (closerest.contains(changeto(temp)) && !allrest.contains(temp)) {
            restaurantList.add(changeto(temp));
          }

        }
      }
    }
    //partial match
    for (int i = 0; i < menus.size(); i++) {
      List<Item> items = menus.get(i).getItems();
      for (int j = 0; j < items.size(); j++) {
        List<String> atts = items.get(j).getAttributes();
        for (int l = 0; l < atts.size(); l++) {
          if (atts.get(j).contains(searchString)) {
            RestaurantEntity temp = new RestaurantEntity();
            for (int k = 0; k < allrest.size(); k++) {
              if (allrest.get(k).getRestaurantId().equals(menus.get(i).getRestaurantId())) {
                temp = allrest.get(j);
                break;
              }
            }

            if (closerest.contains(changeto(temp)) && !allrest.contains(temp)) {
              restaurantList.add(changeto(temp));
            }

          }
        }
      }

    }


    return restaurantList;
  }

  // TODO: CRIO_TASK_MODULE_NOSQL
  // Objective:
  // 1. Check if a restaurant is nearby and open. If so, it is a candidate to be returned.
  // NOTE: How far exactly is "nearby"?

  /**
   * Utility method to check if a restaurant is within the serving radius at a given time.
   *
   * @return boolean True if restaurant falls within serving radius and is open, false otherwise
   */
  private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
      LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
    if (isOpenNow(currentTime, restaurantEntity)) {
      return findDistanceInKm(latitude, longitude,
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

  private Restaurant changeto(RestaurantEntity restaurant) {
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

    return temp;
  }


}
