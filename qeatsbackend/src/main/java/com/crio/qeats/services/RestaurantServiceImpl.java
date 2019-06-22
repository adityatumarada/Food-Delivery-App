/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService {

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;


  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
    Double servingRadiusInKms = -1.0;

    LocalTime timeone1 = LocalTime.of(7, 59, 59);
    LocalTime timeone2 = LocalTime.of(10, 00, 01);
    LocalTime timetwo1 = LocalTime.of(12, 59, 59);
    LocalTime timetwo2 = LocalTime.of(14, 00, 01);
    LocalTime timethree1 = LocalTime.of(18, 59, 59);
    LocalTime timethree2 = LocalTime.of(21, 00, 01);

    if (currentTime.isBefore(timeone2) && currentTime.isAfter(timeone1)) {
      servingRadiusInKms = 3.0;
    } else if (currentTime.isBefore(timetwo2) && currentTime.isAfter(timetwo1)) {
      servingRadiusInKms = 3.0;
    } else if (currentTime.isBefore(timethree2) && currentTime.isAfter(timethree1)) {
      servingRadiusInKms = 3.0;
    } else {
      servingRadiusInKms = 5.0;
    }

    List<Restaurant> restaurantsCloseBy = restaurantRepositoryService.findAllRestaurantsCloseBy(
        getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(),
        currentTime, servingRadiusInKms);

    return new GetRestaurantsResponse(restaurantsCloseBy);
  }


  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Implement findRestaurantsBySearchQuery. The request object has the search string.
  // We have to combine results from multiple sources:
  // 1. Restaurants by name (exact and inexact)
  // 2. Restaurants by cuisines (also called attributes)
  // 3. Restaurants by food items it serves
  // 4. Restaurants by food item attributes (spicy, sweet, etc)
  // Remember, a restaurant must be present only once in the resulting list.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findRestaurantsBySearchQuery(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {

    Double servingRadiusInKms = -1.0;

    LocalTime timeone1 = LocalTime.of(7, 59, 59);
    LocalTime timeone2 = LocalTime.of(10, 00, 01);
    LocalTime timetwo1 = LocalTime.of(12, 59, 59);
    LocalTime timetwo2 = LocalTime.of(14, 00, 01);
    LocalTime timethree1 = LocalTime.of(18, 59, 59);
    LocalTime timethree2 = LocalTime.of(21, 00, 01);

    if (currentTime.isBefore(timeone2) && currentTime.isAfter(timeone1)) {
      servingRadiusInKms = 3.0;
    } else if (currentTime.isBefore(timetwo2) && currentTime.isAfter(timetwo1)) {
      servingRadiusInKms = 3.0;
    } else if (currentTime.isBefore(timethree2) && currentTime.isAfter(timethree1)) {
      servingRadiusInKms = 3.0;
    } else {
      servingRadiusInKms = 5.0;
    }
    List<Restaurant> restaurantsCloseBy = new ArrayList<>();

    if (getRestaurantsRequest.getSearchFor() != null
        && !getRestaurantsRequest.getSearchFor().isEmpty()) {
      restaurantsCloseBy = restaurantRepositoryService.findRestaurantsByName(
        getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(),
        getRestaurantsRequest.getSearchFor(),
        currentTime, servingRadiusInKms);
      restaurantsCloseBy.addAll(restaurantRepositoryService.findRestaurantsByAttributes(
          getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(),
          getRestaurantsRequest.getSearchFor(),
          currentTime, servingRadiusInKms));
      restaurantsCloseBy.addAll(restaurantRepositoryService.findRestaurantsByItemName(
          getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(),
          getRestaurantsRequest.getSearchFor(),
          currentTime, servingRadiusInKms));
      restaurantsCloseBy.addAll(restaurantRepositoryService.findRestaurantsByItemAttributes(
          getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(),
          getRestaurantsRequest.getSearchFor(),
          currentTime, servingRadiusInKms));
      return new GetRestaurantsResponse(restaurantsCloseBy);
    }
    return new GetRestaurantsResponse(restaurantsCloseBy);

  }

  // TODO: CRIO_TASK_MODULE_MULTITHREADING: Implement multi-threaded version of RestaurantSearch.
  // Implement variant of findRestaurantsBySearchQuery which is at least 1.5x time faster than
  // findRestaurantsBySearchQuery.
  //  @Override
  //  public GetRestaurantsResponse findRestaurantsBySearchQueryMt(
  //      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
  //
  //    return null;
  //  }
}
