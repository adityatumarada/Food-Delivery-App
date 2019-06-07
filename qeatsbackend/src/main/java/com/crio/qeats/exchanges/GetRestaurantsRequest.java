/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement GetRestaurantsRequest.
// Complete the class such that it is able to deserialize the incoming query params from
// REST API clients.
// For instance, if a REST client calls API /qeats/v1/restaurants?latitude=21.93&longitude=23.0,
// this class should be able to deserialize lat/long from that.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRestaurantsRequest {
  // Remember, Lombok does a lot of the work here.
  // The http url parameters should have matching names.
  // Spring takes care of the wiring.

  @NotNull
  Double latitude;

  @NotNull
  Double longitude;

}