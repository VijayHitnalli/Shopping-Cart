package com.shoppingcart.exception;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class InvalidPriorityException extends RuntimeException {
private String message;
}
