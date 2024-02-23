package com.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{

}
