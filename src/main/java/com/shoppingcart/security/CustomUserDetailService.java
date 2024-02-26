package com.shoppingcart.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService{
	
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username).map(user-> new CustomUserDetails(user))
				.orElseThrow(()->new UserNotFoundException("Failde To Verify the User"));
	}

}
