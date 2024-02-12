package com.shoppingcart.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppingcart.cache.CacheStore;
import com.shoppingcart.entity.AccessToken;
import com.shoppingcart.entity.Customer;
import com.shoppingcart.entity.RefreshToken;
import com.shoppingcart.entity.Seller;
import com.shoppingcart.entity.User;
import com.shoppingcart.exception.InvalidOTPException;
import com.shoppingcart.exception.UserAlreadyExistByEmailException;

import com.shoppingcart.exception.UserNotLoggedInException;
import com.shoppingcart.repository.AccessTokenRepository;
import com.shoppingcart.repository.CustomerRepository;
import com.shoppingcart.repository.RefreshTokenRepository;
import com.shoppingcart.repository.SellerRepository;
import com.shoppingcart.repository.UserRepository;
import com.shoppingcart.requestdto.AuthRequest;
import com.shoppingcart.requestdto.OtpModel;
import com.shoppingcart.requestdto.UserRequest;
import com.shoppingcart.responsedto.AuthResponse;
import com.shoppingcart.responsedto.UserResponse;
import com.shoppingcart.security.JwtService;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.CookieManager;
import com.shoppingcart.utility.MessageStructure;
import com.shoppingcart.utility.ResponseStructure;
import com.shoppingcart.utility.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	private CustomerRepository customerRepository;

	private SellerRepository sellerRepository;

	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private ResponseStructure<UserResponse> responseStructure;

	private ResponseStructure<AuthResponse> authResponseStructure;

	private SimpleResponseStructure simpleResponseStructure;

	private CacheStore<String> otpCacheStore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;

	private AuthenticationManager authenticationManager;

	private CookieManager cookieManager;

	private JwtService jwtService;

	private AccessTokenRepository accessTokenRepository;

	private RefreshTokenRepository refreshTokenRepository;

	@Value("${myapp.access.expiry}")
	private int accessExpiryInseconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInseconds;

	public AuthServiceImpl(CustomerRepository customerRepository, SellerRepository sellerRepository,
			UserRepository userRepository, PasswordEncoder passwordEncoder,
			ResponseStructure<UserResponse> responseStructure, CacheStore<String> otpCacheStore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookieManager, JwtService jwtService, ResponseStructure<AuthResponse> authResponseStructure,
			AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository,
			SimpleResponseStructure simpleResponseStructure) {
		super();
		this.customerRepository = customerRepository;
		this.sellerRepository = sellerRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.responseStructure = responseStructure;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTokenRepository = accessTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.authResponseStructure = authResponseStructure;
		this.simpleResponseStructure = simpleResponseStructure;
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
		String otp = otpCacheStore.get(otpModel.getEmail());
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
		responseStructure.setMessage("Registration Successfull...!");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,
			HttpServletResponse response) {
		String username = authRequest.getEmail().split("@")[0];
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
				authRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(token);
		if (!authentication.isAuthenticated())
			throw new UsernameNotFoundException("Failed to Authenticate the user");
		else {
			// generating the cookies and authResponse and returning to the client
			return userRepository.findByUsername(username).map(user -> {
				grantAccess(response, user);
				return ResponseEntity.ok(authResponseStructure.setStatus(HttpStatus.OK.value())
						.setData(AuthResponse.builder().userId(user.getUserId()).username(user.getUsername())
								.role(user.getUserRole().name()).isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInseconds))
								.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInseconds)).build())
						.setMessage("LogIn Successful...!"));
			}).get();
		}
	}

	private void grantAccess(HttpServletResponse response, User user) {
		// generating access and refresh token
		String generateAccessToken = jwtService.generateAccessToken(user.getUsername());
		String generateRefreshToken = jwtService.generateRefreshToken(user.getUsername());

		// Adding access and refresh tokens cookies ton the response
		response.addCookie(cookieManager.configure(new Cookie("at", generateAccessToken), accessExpiryInseconds));
		response.addCookie(cookieManager.configure(new Cookie("rt", generateRefreshToken), refreshExpiryInseconds));

		// saving the access and refresh cookie into the database
		accessTokenRepository.save(AccessToken.builder().token(generateAccessToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInseconds)).user(user).build());
		refreshTokenRepository.save(RefreshToken.builder().token(generateRefreshToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInseconds)).user(user).build());
	}

	// -------------------------------------------------------------------------------------------------------
	private <T extends User> T mapToUserRequest(UserRequest userRequest) {
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
		user.setUsername(user.getEmail().split("@")[0]);
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

	private UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).username(user.getUsername()).email(user.getEmail())
				.userRole(user.getUserRole()).isEmailValidated(user.isEmailValidated()).isDeleted(user.isDeleted())
				.build();

	}

	private void sendOtpToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail()).subject("Complete Your Registration Process...!")
				.sentDate(new Date())
				.text("hey " + user.getUsername() + " Good to see you interested in flipkart,"
						+ " Complete your registation using OTP <br>" + "<h1>" + otp + "</h1><br>" + "<br><br>"
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

	private void registrtionSuccessful(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail()).subject("Thank You for Registering...!")
				.sentDate(new Date())
				.text("hey " + user.getUsername() + " Good to see you interested in flipkart,"
						+ "Congratulation your Registration Successful...!" + " with best regards <br>"
						+ " ShoppingCart")
				.build());
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

	// ACCEPTING HttpRequest and HttpRespoone(Older way to LOGOUT)

//	@Override
//	public ResponseEntity<ResponseStructure<AuthResponse>> logout(HttpServletRequest request,
//			HttpServletResponse response) {
//		String at = null;
//		String rt=null;
//		Cookie[] cookies = request.getCookies();
//		if (cookies != null) {
//			for (Cookie cookie : cookies) {
//				if(cookie.getName().equals("at")) at=cookie.getValue();
//				if(cookie.getName().equals("rt")) rt=cookie.getValue();
//			}
//			accessTokenRepository.findByToken(at).ifPresent(accessToken->{
//				accessToken.setBlocked(true);
//				accessTokenRepository.save(accessToken);
//			});
//			refreshTokenRepository.findByToken(rt).ifPresent(refreshToken->{
//				refreshToken.setBlocked(true);
//				refreshTokenRepository.save(refreshToken);
//			});
//				response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
//				response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));
//		}
//		authResponseStructure.setStatus(HttpStatus.OK.value());
//		authResponseStructure.setMessage("LOGOUT SUCCESSFULL...");
//		return new ResponseEntity<ResponseStructure<AuthResponse>>(authResponseStructure, HttpStatus.OK);
//	}

	// ACCEPTING @CookieValue(New)
	@Override
	public ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,
			HttpServletResponse response) {
		accessTokenRepository.findByToken(accessToken).ifPresent(token -> {
			token.setBlocked(true);
			accessTokenRepository.save(token);
		});
		refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
			token.setBlocked(true);
			refreshTokenRepository.save(token);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));

		simpleResponseStructure.setMessage("Logout Successfully...!");
		simpleResponseStructure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}

	public void deleteExpiredTokens() {
		LocalDateTime currentTime = LocalDateTime.now();
		List<AccessToken> accessTokens = accessTokenRepository.findAllByExpirationBefore(currentTime);
		List<RefreshToken> refreshToken = refreshTokenRepository.findAllByExpirationBefore(currentTime);
		accessTokenRepository.deleteAll();
		refreshTokenRepository.deleteAll();

	}

	public ResponseEntity<SimpleResponseStructure> revokeOtherDevices(String accessToken, String refreshToken, HttpServletResponse response) {
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    if (username != null) {
	        userRepository.findByUsername(username).ifPresent(user -> {
	            blockAccessTokens(accessTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));
	            blockRefreshTokens(refreshTokenRepository.findAllByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
	        });
	    } else {
	    	simpleResponseStructure.setMessage("Failed to authorize");
			simpleResponseStructure.setStatus(HttpStatus.UNAUTHORIZED.value());
	        return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure,HttpStatus.UNAUTHORIZED);
	    }
	    simpleResponseStructure.setMessage("Revoked Successfully from other devices...!");
		simpleResponseStructure.setStatus(HttpStatus.OK.value());
	    return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}
	
	private void blockAccessTokens(List<AccessToken> accessTokens) {
		accessTokens.forEach(at -> {
			at.setBlocked(true);
			accessTokenRepository.save(at);
		});
	}

	private void blockRefreshTokens(List<RefreshToken> refreshTokens) {
		refreshTokens.forEach(rt -> {
			rt.setBlocked(true);
			refreshTokenRepository.save(rt);
		});
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAllDevices(String accessToken, String refreshToken,
			HttpServletResponse response) {
		String username=SecurityContextHolder.getContext().getAuthentication().getName();
		if(username != null) {
			userRepository.findByUsername(username).ifPresent(user->{
				blockAllAccessTokens(accessTokenRepository.findAllByUserAndIsBlocked(user, false));
				blockAllRefreshTokens(refreshTokenRepository.findAllByUserAndIsBlocked(user, false));
			});
		}else {
			simpleResponseStructure.setMessage("Failed to authorize");
			simpleResponseStructure.setStatus(HttpStatus.UNAUTHORIZED.value());
	        return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure,HttpStatus.UNAUTHORIZED);
		}
		  simpleResponseStructure.setMessage("Revoked Successfully from all devices...!");
			simpleResponseStructure.setStatus(HttpStatus.OK.value());
		    return new ResponseEntity<SimpleResponseStructure>(simpleResponseStructure, HttpStatus.OK);
	}
	
	private void blockAllAccessTokens(List<AccessToken> accessTokens) {
		accessTokens.forEach(at->{
			at.setBlocked(true);
			accessTokenRepository.save(at);
		});
	}
	private void blockAllRefreshTokens(List<RefreshToken> refreshToken) {
		refreshToken.forEach(rt->{
			rt.setBlocked(true);
			refreshTokenRepository.save(rt);
		});
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> refreshTokens(String accessToken, String refreshToken,
	        HttpServletResponse response) {
	    
	    Optional<AccessToken> optionalAccessToken = accessTokenRepository.findByToken(accessToken);
	    if (optionalAccessToken.isEmpty()) {
	        throw new UserNotLoggedInException("Invalid access token. Please log in again.");
	    }
	    blockAccessTokens(accessToken);

	    Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(refreshToken);
	    if (optionalRefreshToken.isEmpty() || optionalRefreshToken.get().isBlocked()) {
	        throw new UserNotLoggedInException("Invalid refresh token. Please log in again.");
	    }
	    AuthResponse authResponse = generateNewTokens(optionalRefreshToken.get().getUser());
	    blockRefreshTokens(refreshToken);

	    simpleResponseStructure.setStatus(HttpStatus.OK.value());
	    simpleResponseStructure.setMessage("Tokens refreshed successfully.");
	    return ResponseEntity.ok(simpleResponseStructure);
	}
	
	private AuthResponse generateNewTokens(User user) {
	    String newAccessToken = jwtService.generateAccessToken(user.getUsername());
	    String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());
	    return new AuthResponse(user.getUserId(), user.getUsername(), user.getUserRole().name(), true,
                LocalDateTime.now().plusSeconds(accessExpiryInseconds),
                LocalDateTime.now().plusSeconds(refreshExpiryInseconds));
	}

	private void blockAccessTokens(String accessTokens) {
		Optional<AccessToken> token = accessTokenRepository.findByToken(accessTokens);
		 token.ifPresent(at -> {
		        at.setBlocked(true);
		        accessTokenRepository.save(at);
		    });
	}

	private void blockRefreshTokens(String refreshTokens) {
		Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshTokens);
		token.ifPresent(rt -> {
	        rt.setBlocked(true);
	        refreshTokenRepository.save(rt);
	    });
	}

	
	
}
