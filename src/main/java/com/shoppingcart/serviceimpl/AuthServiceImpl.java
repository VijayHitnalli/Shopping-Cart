package com.shoppingcart.serviceimpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.eclipse.angus.mail.handlers.message_rfc822;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.Encoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppingcart.cache.CacheStore;
import com.shoppingcart.entity.Customer;
import com.shoppingcart.entity.Seller;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.InvalidOTPException;
import com.shoppingcart.exception.UserAlreadyExistByEmailException;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.repository.CustomerRepository;
import com.shoppingcart.repository.SellerRepository;
import com.shoppingcart.repository.UserRepository;
import com.shoppingcart.requestdto.OtpModel;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.MessageStructure;
import com.shoppingcart.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

	private CustomerRepository customerRepository;

	private SellerRepository sellerRepository;

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private ResponseStructure<UserResponse> responseStructure;

	private CacheStore<String> otpCacheStore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;

	public <T extends User> T mapToUserRequest(UserRequest userRequest) {
		User user = null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER:
			user = new Customer();
			break;
		case SELLER:
			user = new Seller();
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

		User user = null;
		switch (userRequest.getUserRole()) {
		case CUSTOMER -> {
			user = customerRepository.save(mapToUserRequest(userRequest));
		}
		case SELLER -> {
			user = sellerRepository.save(mapToUserRequest(userRequest));
		}
		default -> throw new RuntimeException("User Role Should be SELLER/CUSTOMER");
		}
		return user;
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName()).email(user.getEmail())
				.userRole(user.getUserRole()).isEmailValidated(user.isEmailValidated()).isDeleted(user.isDeleted())
				.build();

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) {
		if (userRepository.existsByEmail(userRequest.getEmail()))
			throw new UserAlreadyExistByEmailException("Given Email already exist in the database");

		String OTP = otpGeneration();
		User user = mapToUserRequest(userRequest);

		userCacheStore.add(userRequest.getEmail(), user);
		otpCacheStore.add(userRequest.getEmail(), OTP);
		try {
			sendOtpToMail(user, OTP);
		} catch (Exception e) {
			log.error("The Email Address doesn't exist");
		}
		

		return new ResponseEntity<ResponseStructure<UserResponse>>(
				responseStructure.setStatus(HttpStatus.ACCEPTED.value())
						.setMessage("Please verify through OTP sent to your email ").setData(mapToUserResponse(user)),
				HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel) {
		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getOtp());
		if (otp == null)
			throw new InvalidOTPException("OTP Expired");
		if (user == null)
			throw new RuntimeException("Registration session expire");
		if (!otp.equals(otpModel.getOtp()))
			throw new InvalidOTPException("Invalid OTP");
		user.setEmailValidated(true);
		userRepository.save(user);
		try {
			registrtionSuccessful(user, otp);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		UserResponse response = mapToUserResponse(user);
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("Registration Successfil...!");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
	}

	private void sendOtpToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Complete Your Registration Process...!")
				.sentDate(new Date())
				.text("hey " + user.getUserName() + " Good to see you interested in flipkart,"
						+ " Complete your registation using OTP <br>" + "<h1>" + otp + "</h1><br>" + "<br><br>"
						+ " with best regards <br>" + " ShoppingCart")
				.build());
	}
	
	private void registrtionSuccessful(User user,String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Thank You for Registering...!")
				.sentDate(new Date())
				.text("hey " + user.getUserName() + " Good to see you interested in flipkart,"
						+ "Congratulation your Registration Successful...!"
						+ " with best regards <br>" + " ShoppingCart")
				.build());
	}

	@Async
	private void sendMail(MessageStructure messageStructure) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(messageStructure.getTo());
		helper.setSubject(messageStructure.getSubject());
		helper.setSentDate(messageStructure.getSentDate());
		helper.setText(messageStructure.getText(), true);
		javaMailSender.send(mimeMessage);
	}
	private String otpGeneration() {
		return String.valueOf(new Random().nextInt(100000, 999999));
	}

	

	// ********REMOVING NON-VERIFIED USER***********///
	public void removeNonVerifiedUser() {
		List<User> list = userRepository.findByisEmailValidatedFalse();
		if (!list.isEmpty()) {
			for (User user : list) {
				userRepository.delete(user);
			}
		}
	}

}
