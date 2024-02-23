package com.shoppingcart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingcart.requestdto.StoreRequest;
import com.shoppingcart.responsedto.StoreResponse;
import com.shoppingcart.service.StoreService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@EnableMethodSecurity
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")
public class StoreController {
	
	private StoreService storeService;
	
	@PostMapping("/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> addStore(@RequestBody StoreRequest storeRequest ){
		return storeService.addStore(storeRequest);
	}
	@PutMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(@PathVariable int storeId ,@RequestBody StoreRequest storeRequest ){
		return storeService.updateStore(storeId,storeRequest);
	}
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreById(@PathVariable int storeId){
		return storeService.findStoreById(storeId);
	}
	
	@GetMapping("/sellers/{sellerId}/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreBySeller(@PathVariable int sellerId){
		return storeService.findStoreBySeller(sellerId);
	}
	
}
