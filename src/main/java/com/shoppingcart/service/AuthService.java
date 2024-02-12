package com.shoppingcart.service;

import org.springframework.http.ResponseEntity;

import com.shoppingcart.requestdto.AuthRequest;
import com.shoppingcart.requestdto.OtpModel;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.AuthResponse;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel);
	
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest, HttpServletResponse response);

}
