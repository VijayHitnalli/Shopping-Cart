package com.shoppingcart.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shoppingcart.enums.AddressType;
import com.shoppingcart.enums.Priority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "addresses")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int addressId;
	private String streetAddress;
	private String streetAdressAdditional;
	private String city;
	private String country;
	private String state;
	private int pincode;
	private AddressType addressType;
	
	@OneToMany(mappedBy = "address")
	private List<Contact> contacts;
	
	@OneToOne
	private Store store;
}
