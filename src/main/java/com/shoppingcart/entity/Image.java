package com.shoppingcart.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import com.shoppingcart.enums.ImageType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "image")
@Table(name = "images")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Image {

	@org.springframework.data.annotation.Id
	private String imageID;
	@Enumerated(EnumType.STRING)
	private ImageType imageType;
	private byte[] imageBytes;
	private String contentType;

}
