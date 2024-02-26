package com.shoppingcart.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Contact;
import com.shoppingcart.enums.Priority;
import com.shoppingcart.exception.InvalidPriorityException;
import com.shoppingcart.repository.AddressRepository;
import com.shoppingcart.repository.ContactRepository;
import com.shoppingcart.requestdto.ContactRequest;
import com.shoppingcart.responsedto.ContactResponse;
import com.shoppingcart.service.ContactService;
import com.shoppingcart.utility.ResponseStructure;
@Service
public class ContactServiceImpl implements ContactService{

	
	private AddressRepository addressRepository;
	
	private ContactRepository contactRepository;
	
	private ResponseStructure<ContactResponse> responseStructure;
	
	private ResponseStructure<List<ContactResponse>> listResponseStructure;
	
	
	
	private ContactResponse mapToContactReponse(Contact contact)
	{
		return ContactResponse.builder()
				.contactId(contact.getContactId())
				.name(contact.getName())
				.contactNumber(contact.getContactNumber())
				.priority(contact.getPriority().toString())
				.build();
	}

	private Contact mapToContactRequest(ContactRequest contactRequest)
	{
		try {
			Priority priority = Priority.valueOf(contactRequest.getPriority().toUpperCase());

			return Contact.builder()
					.name(contactRequest.getName())
					.contactNumber(contactRequest.getContactNumber())
					.priority(priority)
					.build();
		}
		catch(IllegalArgumentException | NullPointerException ex)
		{
			throw new InvalidPriorityException("The priority can be Primary or Secondary only");
		}
	}
	
	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> addContactToAddress(ContactRequest contactRequest, int addressId) {
	    
		Optional<Address> optional = addressRepository.findById(addressId);
		
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("Given id not present");
		}
		
		Address address = optional.get();
		
	    Contact contact = mapToContactRequest(contactRequest);
	    
	    Contact savedContact = contactRepository.save(contact);
	    
	    address.getContacts().add(savedContact);
	   
	    Address updatedAddress = addressRepository.save(address);
	    
	    ContactResponse contactResponse = mapToContactReponse(savedContact);
	    
	    responseStructure.setData(contactResponse);
	    responseStructure.setMessage("Contact added Successful");
	    responseStructure.setStatus(HttpStatus.CREATED.value());
	    
	    return new ResponseEntity<ResponseStructure<ContactResponse>>(responseStructure,HttpStatus.CREATED);	    
	}
	

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(ContactRequest contactRequest,
			int contactId) {
		
		   Optional<Contact> optional = contactRepository.findById(contactId);
	        if (!optional.isPresent()) {
	            throw new IllegalArgumentException("Contact with the given id not found");
	        }
	        
	        Contact existingContact = optional.get();

	        existingContact.setName(contactRequest.getName());
	        existingContact.setContactNumber(contactRequest.getContactNumber());
	        
	        Contact updatedContact = contactRepository.save(existingContact);

	        ContactResponse contactResponse = mapToContactReponse(updatedContact);

	        responseStructure.setData(contactResponse);
	        responseStructure.setMessage("Contact updated successfully");
	        responseStructure.setStatus(HttpStatus.OK.value());

		    return new ResponseEntity<ResponseStructure<ContactResponse>>(responseStructure,HttpStatus.OK);
	    }
	

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> findContactById(int contactId) {
		 Optional<Contact> optionalContact = contactRepository.findById(contactId);
	        
	      
	        if (!optionalContact.isPresent()) {
	            throw new IllegalArgumentException("Contact with the given id not found");
	        }
	            ContactResponse contactResponse = mapToContactReponse(optionalContact.get());
	            
	            responseStructure.setData(contactResponse);
	            responseStructure.setMessage("Contact found successfully");
	            responseStructure.setStatus(HttpStatus.FOUND.value());
	            
			    return new ResponseEntity<ResponseStructure<ContactResponse>>(responseStructure,HttpStatus.FOUND);

	        }
	
	

	@Override
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> findContactsByAddress(int addressId) {
		  Optional<Address> optionalAddress = addressRepository.findById(addressId);

	       
	        if (!optionalAddress.isPresent()) {
	            throw new IllegalArgumentException("Contact with the given id not found");
	        	
	        }
	        
	            Address address = optionalAddress.get();
	            List<Contact> contacts = address.getContacts();

	            List<ContactResponse> contactResponses = new ArrayList<ContactResponse>();

	            for (Contact contact : contacts) {
	                contactResponses.add(mapToContactReponse(contact));
	            }

	            listResponseStructure.setData(contactResponses);
	            listResponseStructure.setMessage("Contacts found for the address");
	            listResponseStructure.setStatus(HttpStatus.OK.value());

	            return new ResponseEntity<ResponseStructure<List<ContactResponse>>>(listResponseStructure,HttpStatus.FOUND);
	        } 
	}

	


