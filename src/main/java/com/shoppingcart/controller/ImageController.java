package com.shoppingcart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shoppingcart.service.ImageService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
@EnableMethodSecurity
@CrossOrigin(allowCredentials = "true",origins = "http://localhost:5173/")
public class ImageController {
	
	private ImageService imageService;
	
	@PostMapping("/stores/{storeId}/images")
	public ResponseEntity<ResponseStructure<String>> addStoreImage(@PathVariable int storeId, MultipartFile image){
		return imageService.addStoreImage(storeId,image);
	}
	
	@GetMapping("/images/{imageId}")
	public ResponseEntity<byte[]> getImageById(@PathVariable String imageId){
		return imageService.getImageById(imageId);
	}

}
