package com.shoppingcart.service;

import org.springframework.http.ResponseEntity;

import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.utility.ResponseStructure;

public interface AuthService {

	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest);

}
