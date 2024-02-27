package com.shoppingcart.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.shoppingcart.utility.ResponseStructure;

public interface ImageService {

	ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image);

	ResponseEntity<byte[]> getImageById(String imageId);

}
