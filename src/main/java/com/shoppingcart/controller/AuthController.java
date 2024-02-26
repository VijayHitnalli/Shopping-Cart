package com.shoppingcart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.shoppingcart.utility.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@EnableMethodSecurity
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")
public class AuthController {

	private AuthService authService;

	
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest) {
		return authService.registerUser(userRequest);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpModel otpModel) {
		return authService.verifyOTP(otpModel);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,
			HttpServletResponse response,@CookieValue(name = "at",required = false) String accessToken,
			@CookieValue(name = "rt",required = false) String refreshToken) {
		return authService.login(authRequest, response,accessToken,refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<SimpleResponseStructure> logout(@CookieValue(name = "at") String accessToken,
			@CookieValue(name = "rt") String refreshToken, HttpServletResponse response) {
		return authService.logout(accessToken, refreshToken, response);
	}
	@PostMapping("/revoke-other")
	public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(@CookieValue(name = "at") String accessToken,@CookieValue(name = "rt") String refreshToken, HttpServletResponse response) {
		return authService.revokeOtherDevices(accessToken, refreshToken, response);
	}
	
	@PostMapping("/revoke-all")
	public ResponseEntity<SimpleResponseStructure> revokeAllDevices(@CookieValue(name = "at") String accessToken,@CookieValue(name = "rt") String refreshToken, HttpServletResponse response) {
		return authService.revokeAllDevices(accessToken, refreshToken, response);
	}
	@PostMapping("/refresh-token")
	public ResponseEntity<ResponseStructure<AuthResponse>> refreshLoginAndTokenRotation(@CookieValue(name = "at",required = false) String accessToken,@CookieValue(name = "rt",required = false) String refreshToken, HttpServletResponse response){
		return authService.refreshLoginAndTokenRotation(accessToken, refreshToken, response);
	}

}
