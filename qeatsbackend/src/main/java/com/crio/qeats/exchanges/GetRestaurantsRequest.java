/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.exchanges;

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

    private Double latitude;
    private Double longitude;

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}