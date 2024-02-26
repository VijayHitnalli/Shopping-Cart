package com.shoppingcart.responsedto;

import com.shoppingcart.requestdto.AuthRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
	private int storeId;
	private String storeName;
	private String about;
	
}
