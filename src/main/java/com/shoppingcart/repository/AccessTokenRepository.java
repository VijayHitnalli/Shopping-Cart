package com.shoppingcart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{
	Optional<AccessToken> findByToken(String at);
}
