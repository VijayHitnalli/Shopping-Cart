package com.shoppingcart.requestdto;

import com.shoppingcart.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {

	private String email;
	private String password;
	private UserRole userRole;
}
