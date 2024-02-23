package com.shoppingcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByEmail(String email);
	Optional<User> findByUsername(String username);
	  List<User> findByisEmailValidatedFalse();
}
