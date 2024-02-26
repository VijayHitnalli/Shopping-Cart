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

import com.shoppingcart.requestdto.AddressRequest;
import com.shoppingcart.responsedto.AddressResponse;
import com.shoppingcart.service.AddressService;
import com.shoppingcart.service.AuthService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@EnableMethodSecurity
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")
public class AddressController {

	private AddressService addressService;

	@PostMapping("/stores/{storeId}/addresses")
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToStore(@RequestBody AddressRequest addressRequest, @PathVariable int storeId)
	{
		return addressService.addAddressToStore(addressRequest, storeId);
	}
	
	@PostMapping("/customers/{customerId}/addresses")
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToCustomer(@RequestBody AddressRequest addressRequest, @PathVariable int customerId)
	{
		return addressService.addAddressToCustomer(addressRequest, customerId);
	}
	
	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(@RequestBody AddressRequest addressRequest, @PathVariable int addressId)
	{
		return addressService.updateAddress(addressRequest, addressId);
	}
	
	@GetMapping("/addresses/{addressId}")
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(@PathVariable int addressId)
	{
		return addressService.findAddressById(addressId);
	}
	
	@GetMapping("/stores/{storeId}/addresses")
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(@PathVariable int storeId)
	{
		return addressService.findAddressByStore(storeId);
	}
}
