package com.shoppingcart.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{
	
	private CustomerRepository customerRepository;
	
	private SellerRepository sellerRepository;

	private UserRepository userRepository;
	
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
				user.setPassword(userRequest.getPassword());
				user.setUserName(user.getEmail().split("@")[0]);
				user.setUserRole(userRequest.getUserRole());
		 return (T) user;
	}
	
	private User saveUser(User user) {
		switch (user.getUserRole()) {
		case CUSTOMER->{
			user=customerRepository.save((Customer)user);
		}
		case SELLER->{
			user=sellerRepository.save((Seller)user);
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
		User user = mapToUserRequest(userRequest);
		user=saveUser(user);
		
		UserResponse response = mapToUserResponse(user);
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("USER REGISTERED");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure,HttpStatus.CREATED);
	}

}



