/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.exchanges;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement GetRestaurantsResponse.
// Complete the class such that it produces the following JSON during serialization.
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
//    },
//    {
//      "restaurantId": "11",
//      "name": "Shanti Sagar",
//      "city": "Btm Layout",
//      "imageUrl": "www.google.com",
//      "latitude": 20.0269,
//      "longitude": 30.00,
//      "opensAt": "18:00",
//      "closesAt": "23:00",
//      "attributes": [
//        "Udupi",
//        "South Indian"
//      ]
//    }
//  ]
// }
@Data
@NoArgsConstructor
public class GetRestaurantsResponse {
  private String restaurantId;
  private String name;
  private String city;
  private String imageUrl;
  private double latitude;
  private double longitude;
  private String opensAt;
  private String closesAt;
  private List<String> attributes;


  public String getRestaurantId() {
    return restaurantId;
  }

  public String getName() {
    return name;
  }

  public String getCity() {
    return city;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public String getClosesAt() {
    return closesAt;
  }

  public String getOpensAt() {
    return opensAt;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setClosesAt(String closesAt) {
    this.closesAt = closesAt;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setOpensAt(String opensAt) {
    this.opensAt = opensAt;
  }

  public void setAttributes(List<String> attributes) {
    this.attributes = attributes;
  }

  public List<String> getAttributes() {
    return attributes;
  }

}
