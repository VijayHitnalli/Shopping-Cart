package com.shoppingcart.responsedto;

import com.shoppingcart.enums.AvailabilityStatus;

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
public class ProductResponse {
	private int productId;
	private String productName;
	private String productDescription;
	private int productPrice;
	private int productQuantity;
	private float averageRating;
	private int totalOrders;
	
	@Enumerated(EnumType.STRING)
	private AvailabilityStatus availabilityStatus;
}
