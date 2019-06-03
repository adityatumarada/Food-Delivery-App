package com.crio.qeats.dto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
//  "attributes": [https://www.youtube.com/watch?v=QDFI19lj4OM
//    "Tamil",
//    "South Indian"
//  ]
// }
public class Restaurant {

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

