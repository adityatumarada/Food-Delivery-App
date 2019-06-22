/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Order;
import com.crio.qeats.models.OrderEntity;
import com.crio.qeats.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Provider;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRepositoryServiceImpl implements OrderRepositoryService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Override
  public Order placeOrder(Cart cart) {
    OrderEntity order = new OrderEntity();
    order.setRestaurantId(cart.getRestaurantId());
    order.setUserId(cart.getUserId());
    order.setItems(cart.getItems());
    order.setTotal(cart.getTotal());
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    order.setPlacedTime(dtf.format(now));

    orderRepository.insert(order);

    ModelMapper modelMapper = modelMapperProvider.get();
    Order temp = modelMapper.map(order, Order.class);
    return temp;
  }
}