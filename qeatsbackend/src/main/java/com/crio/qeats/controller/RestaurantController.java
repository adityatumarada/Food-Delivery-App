/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.controller;

import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.services.RestaurantService;

import java.time.LocalTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Log4j2
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)
public class RestaurantController {

  public static final String RESTAURANT_API_ENDPOINT = "/qeats/v1";
  public static final String RESTAURANTS_API = "/restaurants";

  @Autowired
  private RestaurantService restaurantService;

  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement the GetRestaurants API as per spec.
  // Get the list of open restaurants near the specified latitude/longitude.
  // API URI: /qeats/v1/restaurants?latitude=21.93&longitude=23.0
  // Method: GET
  // Query Params: latitude, longitude
  // Success Output:
  // 1. If there are open restaurants that are near the specified latitude/longitude,
  //    return them as a list.
  // 2. If there are no open restaurants in the specified range, return an empty array.
  // HTTP Code: 200
  // {
  //  "restaurants": [
  //    {
  //      "restaurantId": "10",
  //      "name": "A2B",
  //      "city": "Hsr Layout",
  //      "imageUrl": "www.google.com",
  //      "latitude": 20.027,
  //      "longitude": 30.0,
  //      "opensAt": "18:00",
  //      "closesAt": "23:00",
  //      "attributes": [
  //        "Tamil",
  //        "South Indian"
  //      ]
  //    }
  //  ]
  // }
  //
  // Error Response:
  // HTTP Code: 4xx, if client side error.
  //          : 5xx, if server side error.
  // Eg:
  // curl -X GET "http://localhost:8081/qeats/v1/restaurants?latitude=28.4900591&longitude=77.536386"
  @GetMapping(RESTAURANTS_API)
  public ResponseEntity<GetRestaurantsResponse> getRestaurants(
          @Valid
                  GetRestaurantsRequest getRestaurantsRequest) {
    log.info("getRestaurants called with {}", getRestaurantsRequest);

    GetRestaurantsResponse getRestaurantsResponse;

    @NotNull
    double lt = getRestaurantsRequest.getLatitude().doubleValue();
    @NotNull
    double lg = getRestaurantsRequest.getLongitude().doubleValue();

    if (lt > 90
            || lt < 0
            || lg > 180
            || lg < 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (getRestaurantsRequest.getLatitude() == null
            || getRestaurantsRequest.getLongitude() == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    getRestaurantsResponse = restaurantService
            .findAllRestaurantsCloseBy(getRestaurantsRequest, LocalTime.now());
    log.info("getRestaurants returned {}", getRestaurantsResponse);

    return ResponseEntity.ok().body(getRestaurantsResponse);
  }
}
