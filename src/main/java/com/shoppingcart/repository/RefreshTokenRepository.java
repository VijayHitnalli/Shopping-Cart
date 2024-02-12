package com.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

}
