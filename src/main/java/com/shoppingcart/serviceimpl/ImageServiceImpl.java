package com.shoppingcart.serviceimpl;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import com.shoppingcart.entity.Image;
import com.shoppingcart.entity.StoreImage;
import com.shoppingcart.enums.ImageType;
import com.shoppingcart.exception.ImageNotFoundException;
import com.shoppingcart.exception.StoreNotFoundException;
import com.shoppingcart.repository.ImageRepository;
import com.shoppingcart.repository.StoreRepository;
import com.shoppingcart.service.ImageService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService{

	private StoreRepository storeRepository;
	
	private ResponseStructure<String> responseStructure;
	
	private ImageRepository imageRepository;
	
	
	
	@Override
	public ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image) {
		return storeRepository.findById(storeId).map(store->{
			StoreImage storeImage=new StoreImage();
			storeImage.setStoreId(storeId);
			storeImage.setImageType(ImageType.LOGO);
			storeImage.setContentType(image.getContentType());
			try {
				storeImage.setImageBytes(image.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			imageRepository.save(storeImage);
			responseStructure.setMessage("Image added Successfully");
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setData("/api/v1/images/"+storeImage.getImageID());
			
			return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.CREATED);
		}).orElseThrow(()-> new StoreNotFoundException("Given id not present in the Database"));
	}


	 public ResponseEntity<byte[]> getImageById(String imageId) {
	        Image image = imageRepository.findById(imageId)
	                .orElseThrow(() -> new ImageNotFoundException("Given image id not found in the database"));
	        
	        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getContentType()))
	        		.contentLength(image.getImageBytes().length)
	        		.body(image.getImageBytes());
	        		
	    }

}
