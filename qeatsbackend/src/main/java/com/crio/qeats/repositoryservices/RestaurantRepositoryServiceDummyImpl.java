package com.crio.qeats.repositoryservices;

import static com.crio.qeats.utils.GeoUtils.findDistanceInKm;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

@Service
public abstract class RestaurantRepositoryServiceDummyImpl implements RestaurantRepositoryService {
  private static final String FIXTURES = "fixtures/exchanges";
  private ObjectMapper objectMapper = new ObjectMapper();

  private List<Restaurant> loadRestaurantsDuringNormalHours() throws IOException {
    String fixture =
            FixtureHelpers.fixture(FIXTURES + "/normal_hours_list_of_restaurants.json");

    return objectMapper.readValue(fixture, new TypeReference<List<Restaurant>>() {
    });
  }

  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Use this dummy implementation.
  // This function returns a list of restaurants in any lat/long of your choice randomly.
  // It will load some dummy restaurants and change their latitude/longitude near
  // the lat/long you pass. In the next module, once you start using mongodb, you will not use
  // it anymore.
  @Override
  public List<Restaurant> findAllRestaurantsCloseBy(Double latitude,
      Double longitude, LocalTime currentTime, Double servingRadiusInKms) {
    List<Restaurant> restaurantList = new ArrayList<>();
    try {
      restaurantList = loadRestaurantsDuringNormalHours();
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (Restaurant restaurant : restaurantList) {
      if (findDistanceInKm(latitude, longitude,
              restaurant.getLatitude(), restaurant.getLongitude()) <= 3) {
        restaurant.setLatitude(latitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
        restaurant.setLongitude(longitude + ThreadLocalRandom.current().nextDouble(0.000001, 0.2));
      }
    }
    return restaurantList;
  }
}