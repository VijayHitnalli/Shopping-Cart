package com.shoppingcart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sellers")
@Setter
@Getter
public class Seller extends User{

	@OneToOne
	private Store store;
}
