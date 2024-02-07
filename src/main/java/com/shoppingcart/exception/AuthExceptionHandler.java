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
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "User Not found with given Id");
	}
}
