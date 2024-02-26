package com.shoppingcart.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.shoppingcart.requestdto.ContactRequest;
import com.shoppingcart.responsedto.ContactResponse;
import com.shoppingcart.utility.ResponseStructure;

public interface ContactService {


	ResponseEntity<ResponseStructure<ContactResponse>> updateContact(ContactRequest contactRequest, int contactId);

	ResponseEntity<ResponseStructure<ContactResponse>> findContactById(int contactId);

	ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactsByAddress(int addressId);

	ResponseEntity<ResponseStructure<ContactResponse>> addContactToAddress(ContactRequest contactRequest,
			int addressId);

}
