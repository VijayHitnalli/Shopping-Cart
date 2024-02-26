package com.shoppingcart.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Setter
@Getter
public class Customer extends User{
	
	@OneToMany(mappedBy = "customer")
	private List<Address> addresses;
}
