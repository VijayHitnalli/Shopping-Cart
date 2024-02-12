package com.shoppingcart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingcart.requestdto.AuthRequest;
import com.shoppingcart.requestdto.OtpModel;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.AuthResponse;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {
	
	private AuthService authService;
	

	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest){
		return authService.registerUser(userRequest);
	}
	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTO(@RequestBody OtpModel otpModel){
		return authService.verifyOTP(otpModel);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,HttpServletResponse response){
		return authService.login(authRequest,response);
	}
}
