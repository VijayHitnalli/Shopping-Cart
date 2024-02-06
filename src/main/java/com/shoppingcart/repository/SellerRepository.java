package com.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer>{

}
