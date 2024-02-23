package com.shoppingcart.serviceimpl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Customer;
import com.shoppingcart.entity.Store;
import com.shoppingcart.enums.AddressType;
import com.shoppingcart.exception.AddressNotFoundException;
import com.shoppingcart.exception.InvalidAddressTypeException;
import com.shoppingcart.exception.StoreNotFoundException;
import com.shoppingcart.repository.AddressRepository;
import com.shoppingcart.repository.CustomerRepository;
import com.shoppingcart.repository.StoreRepository;
import com.shoppingcart.requestdto.AddressRequest;
import com.shoppingcart.responsedto.AddressResponse;
import com.shoppingcart.service.AddressService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService{

	private AddressRepository addressRepository;

	private StoreRepository storeRepository;
	
	private CustomerRepository customerRepository;
	
	private ResponseStructure<AddressResponse> responseStructure;

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToStore(AddressRequest addressRequest, int storeId) {
	    Optional<Store> optional = storeRepository.findById(storeId);
	    ResponseStructure<AddressResponse> responseStructure = new ResponseStructure<>(); // Initializing response structure

	    if (!optional.isPresent()) {
	        throw new IllegalArgumentException("Given Id not found");
	    }

	    Store store = optional.get();

	    if (store.getAddress() != null) {
	        responseStructure.setMessage("Store already has an address");
	        responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	        return new ResponseEntity<ResponseStructure<AddressResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
	    }

	    Address address = mapToAddress(addressRequest);
	    addressRepository.save(address);
	    store.setAddress(address);
	    storeRepository.save(store);
	    AddressResponse mappedAddressResponse = mapToAddressResponse(address);
	    responseStructure.setData(mappedAddressResponse);
	    responseStructure.setMessage("Address added to store successfully");
	    responseStructure.setStatus(HttpStatus.CREATED.value());

	    return new ResponseEntity<ResponseStructure<AddressResponse>>(responseStructure, HttpStatus.CREATED);
	}
	
	
	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToCustomer(AddressRequest addressRequest, int customerId) {
	    Optional<Customer> optional = customerRepository.findById(customerId);
	    ResponseStructure<AddressResponse> responseStructure = new ResponseStructure<>(); // Initializing response structure

	    if (!optional.isPresent()) {
	        throw new IllegalArgumentException("Customer with given ID not found");
	    }

	    Customer customer = optional.get();

	    if (customer.getAddresses() != null) {
	        responseStructure.setMessage("Customer already has an address");
	        responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	        return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
	    }

	    Address address = mapToAddress(addressRequest);
	    addressRepository.save(address); 
	    customer.getAddresses().add(address);
	    
	    customerRepository.save(customer);
	    AddressResponse mappedAddressResponse = mapToAddressResponse(address);
	    responseStructure.setData(mappedAddressResponse);
	    responseStructure.setMessage("Address added to customer successfully");
	    responseStructure.setStatus(HttpStatus.CREATED.value());

	    return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
	}



	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressById(int addressId)
	{
		Optional<Address> optional = addressRepository.findById(addressId);
		
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("Given user id not found");
		}
		
		Address address = optional.get();
		AddressResponse mapToAddressResponse = mapToAddressResponse(address);
		responseStructure.setData(mapToAddressResponse);
		responseStructure.setMessage("Found Successfully...!");
		responseStructure.setStatus(HttpStatus.FOUND.value());
		
		return new ResponseEntity<ResponseStructure<AddressResponse>>(responseStructure,HttpStatus.FOUND);

	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> findAddressByStore(int storeId) {
	    Optional<Store> optionalStore = storeRepository.findById(storeId);

	    if (optionalStore.isPresent()) {
	        Store store = optionalStore.get();
	        Address address = store.getAddress();
	        if (address != null) {
	            AddressResponse addressResponse = mapToAddressResponse(address);
	            ResponseStructure<AddressResponse> responseStructure = new ResponseStructure<>();
	            responseStructure.setData(addressResponse);
	            responseStructure.setMessage("Address details found successfully");
	            responseStructure.setStatus(HttpStatus.FOUND.value());
	            return new ResponseEntity<>(responseStructure, HttpStatus.FOUND);
	        } else {
	            throw new AddressNotFoundException("Address not found for the given store Id");
	        }
	    } else {
	        throw new StoreNotFoundException("Store with the given Id not found, please provide a valid store Id");
	    }
	}


	//Mapper Methods
	private AddressResponse mapToAddressResponse(Address address)
	{
		return AddressResponse.builder()
				.addressId(address.getAddressId())
				.streetAddress(address.getStreetAddress())
				.streetAddressAdditional(address.getStreetAddressAdditional())
				.city(address.getCity())
				.state(address.getState())
				.country(address.getCountry())
				.pincode(address.getPincode())
				.addressType(address.getAddressType().toString())
				.build();
	}

	private Address mapToAddress(AddressRequest addressRequest) 
	{
		try {
			AddressType addressType = AddressType.valueOf(addressRequest.getAddressType().toUpperCase());

			return Address.builder()
					.streetAddress(addressRequest.getStreetAddress())
					.streetAddressAdditional(addressRequest.getStreetAddressAdditional())
					.city(addressRequest.getCity())
					.state(addressRequest.getState())
					.country(addressRequest.getCountry())
					.pincode(addressRequest.getPincode())
					.addressType(addressType)
					.build();
		}
		catch(IllegalArgumentException | NullPointerException ex)
		{
			throw new InvalidAddressTypeException("The Address Type can be Residential or Official only");
		}
	}


	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(AddressRequest addressRequest, int addressId) {
	    Optional<Address> optionalAddress = addressRepository.findById(addressId);

	    if (optionalAddress.isPresent()) {
	        Address address = optionalAddress.get();
	        Address updatedAddress = mapToAddress(addressRequest);
	        updatedAddress.setAddressId(address.getAddressId());
	        updatedAddress = addressRepository.save(updatedAddress);

	        AddressResponse addressResponse = mapToAddressResponse(updatedAddress);
	        ResponseStructure<AddressResponse> responseStructure = new ResponseStructure<>();
	        responseStructure.setData(addressResponse);
	        responseStructure.setMessage("Address details updated successfully");
	        responseStructure.setStatus(HttpStatus.CREATED.value());

	        return new ResponseEntity<ResponseStructure<AddressResponse>>(responseStructure, HttpStatus.CREATED);
	    } else {
	        throw new AddressNotFoundException("Address with the given Id not found, please provide a valid address Id");
	    }
	}


}
