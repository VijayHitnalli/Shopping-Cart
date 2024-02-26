package com.shoppingcart.exception;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler{
	
	
	private ResponseEntity<Object> structure(HttpStatus status,String messege,Object rootCause){
		return new ResponseEntity<Object> (Map.of(
				"status",status.value(),
				"messege",messege,
				"rootCause",rootCause
				),status);
	}
	
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<ObjectError> allErrors = ex.getAllErrors();
		Map<String, String> errors=new HashMap<String,String>();
		allErrors.forEach(error->{
			FieldError fieldError =(FieldError) error;
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		});
		return structure(HttpStatus.BAD_REQUEST,"Failed To save the data...!", errors);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundById(UserNotFoundException exception){
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Username or password incorrect");
	}
	@ExceptionHandler(UserAlreadyExistByEmailException.class)
	public ResponseEntity<Object> handleUserAlreadyExistByEmail(UserAlreadyExistByEmailException exception){
		return structure(HttpStatus.NOT_ACCEPTABLE, exception.getMessage(), "User with given Email Id is Alreday Exist");
	}
	@ExceptionHandler(InvalidOTPException.class)
	public ResponseEntity<Object> handleInvalidOTP(InvalidOTPException exception){
		return structure(HttpStatus.NOT_ACCEPTABLE, exception.getMessage(), "Your OTP is expired generate again");
	}
	@ExceptionHandler(UserNotLoggedInException.class)
	public ResponseEntity<Object> handleUserNotLoggedIn(UserNotLoggedInException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "You are not logged in");
	}
	@ExceptionHandler(UserAlreadyLoggedInException.class)
	public ResponseEntity<Object> handleUserAlreadyLoggedIn(UserAlreadyLoggedInException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "User already logged in");
	}
	
	
	@ExceptionHandler(InvalidAddressTypeException.class)
	public ResponseEntity<Object> handleInvalidAddressType(InvalidAddressTypeException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Given type of Address is not valid");
	}
	@ExceptionHandler(StoreNotFoundException.class)
	public ResponseEntity<Object> handleStoreNotFound(StoreNotFoundException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Given store Id not found in the database");
	}
	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<Object> handleAddressNotFound(AddressNotFoundException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Given Address Id not found in the database");
	}
	@ExceptionHandler(InvalidPriorityException.class)
	public ResponseEntity<Object> handleInvalidPriority(InvalidPriorityException exception){
		return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Invalid Priority");
	}
}
