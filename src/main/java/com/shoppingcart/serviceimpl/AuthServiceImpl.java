package com.shoppingcart.serviceimpl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppingcart.entity.User;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.ResponseStructure;

@Service
public class AuthServiceImpl implements AuthService{
	
	
	public <T extends User> T mapToUserRequest(UserRequest userRequest) {
		return (T) User.builder()
				.email(userRequest.getEmail())
				.password(userRequest.getPassword())
				.userRole(userRequest.getUserRole())
				.build();
	}
	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.isEmailVerified(user.isEmailValidated())
				.isDeleted(user.isDeleted())
				.build();
	}
	
	

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) {
		
		return null;
	}

}
