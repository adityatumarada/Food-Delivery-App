package com.crio.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: CRIO_TASK_MODULE_SERIALIZATION - Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com",
//  "latitude": 20.027,
//  "longitude": 30.0,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }
// CRIO_SOLUTION_START_MODULE_SERIALIZATION
// CRIO_NUDGE_START_MODULE_SERIALIZATION
// 1. First let the user code with Getters/Setters
// 2. Either when they are stuck or post module completion, we can introduce lombok.
// Good chance that they will struggle when they miss out NoArgsConstructor.
// CRIO_NUDGE_END_MODULE_SERIALIZATION
@Data
@AllArgsConstructor
@NoArgsConstructor
// CRIO_SOLUTION_END_MODULE_SERIALIZATION
public class Restaurant {

  // CRIO_SOLUTION_START_MODULE_SERIALIZATION
  @NotNull @JsonIgnore
  private String id;

  @NotNull
  private String restaurantId;

  @NotNull
  private String name;

  @NotNull
  private String city;

  @NotNull
  private String imageUrl;

  @NotNull
  private Double latitude;

  @NotNull
  private Double longitude;

  @NotNull
  private String opensAt;

  @NotNull
  private String closesAt;

  @NotNull
  private List<String> attributes = new ArrayList<>();
}