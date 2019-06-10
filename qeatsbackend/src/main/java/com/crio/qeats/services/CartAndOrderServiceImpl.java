package com.crio.qeats.services;

import com.crio.qeats.repositoryservices.CartRepositoryService;
import com.crio.qeats.repositoryservices.OrderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartAndOrderServiceImpl implements CartAndOrderService {

  @Autowired
  private CartRepositoryService cartRepositoryService;

  @Autowired
  private OrderRepositoryService orderRepositoryService;

  @Autowired
  private MenuService menuService;

}
