package com.shoppingcart.entity;

import org.springframework.stereotype.Service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreImage extends Image{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int storeId;
}
