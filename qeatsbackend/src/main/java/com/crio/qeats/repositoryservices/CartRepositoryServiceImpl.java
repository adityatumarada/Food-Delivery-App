/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositoryservices;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.exceptions.CartNotFoundException;
import com.crio.qeats.models.CartEntity;
import com.crio.qeats.repositories.CartRepository;

import java.util.List;
import java.util.Optional;
import javax.inject.Provider;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.stereotype.Service;


@Service
public class CartRepositoryServiceImpl implements CartRepositoryService {

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;
  
  @Override
  public String createCart(Cart cart) {
    ModelMapper modelMapper = modelMapperProvider.get();
    CartEntity ct = modelMapper.map(cart, CartEntity.class);
    cartRepository.insert(ct);
    cartRepository.save(ct);
    return ct.getId();
  }

  @Override
  public Optional<Cart> findCartByUserId(String userId) {
    List<CartEntity> cartEntities = cartRepository.findAll();
    for (CartEntity cartEntity : cartEntities) {
      if (cartEntity.getUserId().equals(userId)) {
        Cart cart = findCartByCartId(cartEntity.getId());
        return Optional.of(cart);
      }
    }
    return Optional.empty();
  }

  @Override
  public Cart findCartByCartId(String cartId) throws CartNotFoundException {

    if (cartRepository.findById(cartId).isPresent()) {
      CartEntity ct = cartRepository.findById(cartId).get();
      ModelMapper modelMapper = modelMapperProvider.get();
      return modelMapper.map(ct, Cart.class);
    }

    throw new CartNotFoundException();
  }

  @Override
  public Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<CartEntity> cartEntity = cartRepository.findById(cartId);

    if (!cartEntity.isPresent()) {
      throw new CartNotFoundException();

    } else {
      CartEntity cartEnt = cartEntity.get();
      cartEnt.addItem(item);
      cartEnt.setRestaurantId(restaurantId);
      if (cartEnt.getItems().isEmpty()) {
        cartEnt.setRestaurantId("");
      }
      cartRepository.save(cartEnt);
      Cart cart = modelMapper.map(cartEnt, Cart.class);
      return cart;
    }
  }

  @Override
  public Cart removeItem(Item item,
      String cartId, String restaurantId) throws CartNotFoundException {
    ModelMapper modelMapper = modelMapperProvider.get();
    Optional<CartEntity> cartEntity = cartRepository.findById(cartId);

    if (!cartEntity.isPresent()) {
      throw new CartNotFoundException();

    } else {
      CartEntity cartEnt = cartEntity.get();
      cartEnt.removeItem(item);
      //cartEnt.setRestaurantId(restaurantId)
      if (cartEnt.getItems().isEmpty()) {
        cartEnt.setRestaurantId("");
      }
      cartRepository.save(cartEnt);
      Cart cart = modelMapper.map(cartEnt, Cart.class);
      return cart;
    }
  }
}