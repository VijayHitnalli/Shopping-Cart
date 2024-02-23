package com.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer>{

}
