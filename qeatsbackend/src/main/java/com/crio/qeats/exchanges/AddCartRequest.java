package com.crio.qeats.exchanges;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequest {
  @NotNull
  String cartId;

  @NotNull
  String restaurantId;

  @NotNull
  String itemId;
}
