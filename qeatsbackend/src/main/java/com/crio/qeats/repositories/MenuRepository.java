/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.MenuEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuRepository extends MongoRepository<MenuEntity, String> {

}
