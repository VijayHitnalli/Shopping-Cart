package com.shoppingcart.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.Encoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppingcart.entity.Customer;
import com.shoppingcart.entity.Seller;
import com.shoppingcart.entity.User;
import com.shoppingcart.repository.CustomerRepository;
import com.shoppingcart.repository.SellerRepository;
import com.shoppingcart.repository.UserRepository;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
	
	private CustomerRepository customerRepository;
	
	private SellerRepository sellerRepository;

	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	private ResponseStructure<UserResponse> responseStructure;
	
	

	public <T extends User> T mapToUserRequest(UserRequest userRequest) {
		User user=null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER:user=new Customer();	
			break;
		case SELLER:user=new Seller();
			break;
		default:
			break;
		}
				user.setEmail(userRequest.getEmail());
				user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
				user.setUserName(user.getEmail().split("@")[0]);
				user.setUserRole(userRequest.getUserRole());
		 return (T) user;
	}
	
	private User saveUser(UserRequest userRequest) {

		User user=null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER->{
			user= customerRepository.save(mapToUserRequest(userRequest));
		}
		case SELLER->{
			user=sellerRepository.save(mapToUserRequest(userRequest));
		}
		default-> throw new RuntimeException("User Role Should be SELLER/CUSTOMER");
		}
		return user;
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.isEmailValidated(user.isEmailValidated())
				.isDeleted(user.isDeleted())
				.build();
		
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) {
	User user=userRepository.findByUserName(userRequest.getEmail().split("@")[0]).map(u->{
		if(u.isEmailValidated()) {
			throw new RuntimeException("User Already exist eith the specified Id");
		}
		else {
			//Send an email otp 
		}
		return u;
	}).orElse(saveUser(userRequest));
	return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure.setStatus(HttpStatus.ACCEPTED.value())
			.setMessage("Please verify throw otp sent on your eamil")
			.setData(mapToUserResponse(user)),HttpStatus.ACCEPTED);
	}
	
	public void removeNonVerifiedUser() {
		List<User> list = userRepository.findByisEmailValidatedFalse();
		if(!list.isEmpty()) {
			for(User user:list) {
				userRepository.delete(user);
			}
		}
		
	}
	
	
}



