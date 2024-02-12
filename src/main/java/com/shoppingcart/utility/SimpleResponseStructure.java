package com.shoppingcart.utility;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class SimpleResponseStructure<T> {
	private int status;
	private String message;
}
