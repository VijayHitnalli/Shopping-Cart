package com.shoppingcart.requestdto;

import com.shoppingcart.enums.AvailabilityStatus;
import com.shoppingcart.responsedto.ProductResponse;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
	
	private String productName;
	private String productDescription;
	private int productPrice;
	private int productQuantity;
	
	@Enumerated(EnumType.STRING)
	private AvailabilityStatus availabilityStatus;
}
