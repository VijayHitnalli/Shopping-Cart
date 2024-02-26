package com.shoppingcart.service;

import org.springframework.http.ResponseEntity;

import com.shoppingcart.requestdto.AddressRequest;
import com.shoppingcart.responsedto.AddressResponse;
import com.shoppingcart.utility.ResponseStructure;

public interface AddressService {

	
	ResponseEntity<ResponseStructure<AddressResponse>> addAddressToStore(AddressRequest addressRequest, int storeId);

	ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(AddressRequest addressRequest, int addressId);

	ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(int addressId);

	ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(int storeId);

	ResponseEntity<ResponseStructure<AddressResponse>> addAddressToCustomer(AddressRequest addressRequest,
			int customerId);
}
