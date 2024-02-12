package com.shoppingcart.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingcart.entity.AccessToken;
import com.shoppingcart.entity.User;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{
	Optional<AccessToken> findByToken(String at);
	
	List<AccessToken> findAllByExpirationBefore(LocalDateTime time);

	Optional<AccessToken> findByTokenAndIsBlocked(String token, boolean isBlocked);
	
	Optional<AccessToken> findAllByUserAndIsBlocked(User user,boolean isBlocked);
		
	List<AccessToken> findAllByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String token);

	
}
