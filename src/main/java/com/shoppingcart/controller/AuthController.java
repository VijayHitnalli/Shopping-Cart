package com.shoppingcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.ResponseStructure;

@RestController
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest){
		return authService.registerUser(userRequest);
	}
	
}
