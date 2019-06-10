/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import static com.crio.qeats.utils.GeoUtils.findDistanceInKm;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.repositories.RestaurantRepository;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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

  // TODO: CRIO_TASK_MODULE_NOSQL - Implement findAllRestaurantsCloseby.
  // Check RestaurantRepositoryService.java file for the interface contract.
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    return findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
  }

  private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
                                                         LocalTime currentTime,
                                                         Double servingRadiusInKms) {
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


}
