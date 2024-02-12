package com.shoppingcart.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.RefreshToken;
import com.shoppingcart.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	Optional<RefreshToken> findByToken(String rt);
	
	List<RefreshToken> findAllByExpirationBefore(LocalDateTime time);
	
	Optional<RefreshToken> findAllByUserAndIsBlocked(User user,boolean isBlocked);
	
	List<RefreshToken> findAllByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String token);

	
}
