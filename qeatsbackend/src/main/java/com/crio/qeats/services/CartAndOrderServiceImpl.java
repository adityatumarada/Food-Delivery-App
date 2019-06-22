package com.crio.qeats.services;

import com.crio.qeats.dto.Cart;
import com.crio.qeats.dto.Item;
import com.crio.qeats.dto.Order;
import com.crio.qeats.exceptions.EmptyCartException;
import com.crio.qeats.exceptions.ItemNotFromSameRestaurantException;
import com.crio.qeats.exchanges.CartModifiedResponse;
import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;

import java.util.NoSuchElementException;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CartAndOrderServiceImpl implements CartAndOrderService {

  @Autowired
  private CartRepositoryService cartRepositoryService;

  @Autowired
  private OrderRepositoryService orderRepositoryService;

  @Autowired
  private MenuService menuService;

  @Override
  public Cart findOrCreateCart(String userId) {

    Optional<Cart> ct = cartRepositoryService.findCartByUserId(userId);
    if (ct.isPresent()) {
      return ct.get();
    } else {
      Cart c = new Cart();
      try {
        c.setUserId(userId);
        c.setRestaurantId("");
        cartRepositoryService.createCart(c);
        return cartRepositoryService.findCartByCartId("");
      } catch (NoSuchElementException e) {
        return c;
      }
    }
  }

  @Override
  public CartModifiedResponse addItemToCart(String itemId, String cartId, String restaurantId)
      throws ItemNotFromSameRestaurantException {
    CartModifiedResponse response = new CartModifiedResponse();
    Cart cart = cartRepositoryService.findCartByCartId(cartId);
    Item item = menuService.findItem(itemId, restaurantId);
    if (!cart.getRestaurantId().isEmpty() && !cart.getRestaurantId().equals(restaurantId)) {
      return new CartModifiedResponse(cart,
        new ItemNotFromSameRestaurantException().getErrorType());
    }
    cart = cartRepositoryService.addItem(item, cartId, restaurantId);
    response.setCart(cart);
    response.setCartResponseType(0);
    return response;
  }

  @Override
  public CartModifiedResponse removeItemFromCart(String itemId,
      String cartId, String restaurantId) {
    Cart cart = cartRepositoryService.removeItem(menuService.findItem(itemId,
        restaurantId),cartId,restaurantId);
    return new CartModifiedResponse(cart);
  }

  @Override
  public Order postOrder(String cartId) throws EmptyCartException {
    if (cartRepositoryService.findCartByCartId(cartId) != null) {
      return orderRepositoryService.placeOrder(cartRepositoryService.findCartByCartId(cartId));
    }

    throw new EmptyCartException("EmptyCartException");
  }
}
