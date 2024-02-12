package com.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{

}
