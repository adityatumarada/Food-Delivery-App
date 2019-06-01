/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
@Log4j2
public class QEatsApplication {

  public static void main(String[] args) {
    // You don't need to run main for serialization module.
  }

  /**
   * Fetches a ModelMapper instance.
   *
   * @return ModelMapper
   */
  @Bean // Want a new obj every time
  @Scope("prototype")
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
