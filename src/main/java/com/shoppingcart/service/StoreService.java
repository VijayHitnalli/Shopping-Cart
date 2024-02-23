package com.shoppingcart.service;

import org.springframework.http.ResponseEntity;

import com.shoppingcart.requestdto.StoreRequest;
import com.shoppingcart.responsedto.StoreResponse;
import com.shoppingcart.utility.ResponseStructure;

public interface StoreService {

	ResponseEntity<ResponseStructure<StoreResponse>> addStore(StoreRequest storeRequest);

	

	ResponseEntity<ResponseStructure<StoreResponse>> updateStore(int storeId, StoreRequest storeRequest);



	ResponseEntity<ResponseStructure<StoreResponse>> findStoreById(int storeId);



	ResponseEntity<ResponseStructure<StoreResponse>> findStoreBySeller(int sellerId);



}
