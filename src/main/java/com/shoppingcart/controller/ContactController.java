package com.shoppingcart.controller;

import java.util.List;

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

import com.shoppingcart.requestdto.ContactRequest;
import com.shoppingcart.responsedto.ContactResponse;
import com.shoppingcart.service.AddressService;
import com.shoppingcart.service.ContactService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@EnableMethodSecurity
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")
public class ContactController {

	private ContactService contactService;
	
	   @PostMapping("/contacts")
	    public ResponseEntity<ResponseStructure<ContactResponse>> addContactToAddress(@RequestBody ContactRequest contactRequest, @PathVariable int addressId) {
	        return contactService.addContactToAddress(contactRequest, addressId);
	    }
	
	@PutMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(@RequestBody ContactRequest contactRequest, @PathVariable int contactId)
	{
		return contactService.updateContact(contactRequest, contactId);
	}
	
	@GetMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> findContactById(@PathVariable int contactId)
	{
		return contactService.findContactById(contactId);
	}
	
	@GetMapping("/addresses/{addressId}/contacts")
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactsByAddress(@PathVariable int addressId)
	{
		return contactService.findContactsByAddress(addressId);
	}
}
