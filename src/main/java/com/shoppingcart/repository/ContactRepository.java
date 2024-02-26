package com.shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	List<Contact> findByAddress(Address address);
}
