/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.models.RestaurantEntity;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

// TODO: CRIO_TASK_MODULE_NOSQL - Pass all the RestaurantRepositoryService test cases.
// Make modifications to the tests if necessary.
@SpringBootTest(classes = {QEatsApplication.class})
public class RestaurantRepositoryServiceTest {

  private static final String FIXTURES = "fixtures/exchanges";
  List<RestaurantEntity> allRestaurants = new ArrayList<>();
  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;
  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @BeforeEach
  public void setup() throws IOException {
    allRestaurants = listOfRestaurants();

    for (RestaurantEntity restaurantEntity : allRestaurants) {
      mongoTemplate.save(restaurantEntity, "restaurants");
    }
  }

  @AfterEach
  public void teardown() {
    mongoTemplate.dropCollection("restaurants");
    GlobalConstants.destroyCache();
  }

  @Test
  public void restaurantsCloseByAndOpenNow(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(18, 01), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(2, allRestaurantsCloseBy.size());
    assertEquals("11", allRestaurantsCloseBy.get(0).getRestaurantId());
    assertEquals("12", allRestaurantsCloseBy.get(1).getRestaurantId());
  }

  @Test
  public void noRestaurantsNearBy(@Autowired MongoTemplate mongoTemplate) {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
        .findAllRestaurantsCloseBy(20.9, 30.0, LocalTime.of(18, 00), 3.0);

    ModelMapper modelMapper = modelMapperProvider.get();
    assertEquals(0, allRestaurantsCloseBy.size());
  }


  private List<RestaurantEntity> listOfRestaurants() throws IOException {
    String fixture =
        FixtureHelpers.fixture(FIXTURES + "/initial_data_set_restaurants.json");

    return objectMapper.readValue(fixture, new TypeReference<List<RestaurantEntity>>() {
    });
  }
}
