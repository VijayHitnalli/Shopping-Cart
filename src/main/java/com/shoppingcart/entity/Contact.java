package com.shoppingcart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shoppingcart.enums.Priority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
@Table(name = "contacts")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contactId;
	private String name;
	private long contactNumber;
	private Priority priority;
	
	@JsonIgnore
	@ManyToOne
	private Address address;
}
