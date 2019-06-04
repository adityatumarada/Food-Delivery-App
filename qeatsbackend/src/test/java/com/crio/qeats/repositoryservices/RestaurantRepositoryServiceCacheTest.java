package com.crio.qeats.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.globals.GlobalConstants;
import com.crio.qeats.repositories.RestaurantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.inject.Provider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(classes = {QEatsApplication.class})
class RestaurantRepositoryServiceCacheTest {

  private static final String FIXTURES = "fixtures/exchanges";

  @Autowired
  private RestaurantRepositoryService restaurantRepositoryService;
  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @MockBean
  private RestaurantRepository mockRestaurantRepository;

  @AfterEach
  void teardown() {
    GlobalConstants.destroyCache();
  }


  @Test
  void restaurantsCloseByFromWarmCache(@Autowired MongoTemplate mongoTemplate) throws IOException {
    assertNotNull(mongoTemplate);
    assertNotNull(restaurantRepositoryService);

    when(mockRestaurantRepository.findAll()).thenReturn(listOfRestaurants());

