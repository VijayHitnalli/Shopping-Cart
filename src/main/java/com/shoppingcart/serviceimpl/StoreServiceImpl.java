package com.shoppingcart.serviceimpl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppingcart.entity.Seller;
import com.shoppingcart.entity.Store;
import com.shoppingcart.repository.SellerRepository;
import com.shoppingcart.repository.StoreRepository;
import com.shoppingcart.requestdto.StoreRequest;
import com.shoppingcart.responsedto.StoreResponse;
import com.shoppingcart.service.StoreService;
import com.shoppingcart.utility.ResponseStructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
public class StoreServiceImpl implements StoreService{
	
	private StoreRepository storeRepository;
	
	private SellerRepository sellerRepository;
	
	public StoreServiceImpl(StoreRepository storeRepository, SellerRepository sellerRepository,
			ResponseStructure<StoreResponse> responseStructure) {
		super();
		this.storeRepository = storeRepository;
		this.sellerRepository = sellerRepository;
		this.responseStructure = responseStructure;
	}

	private ResponseStructure<StoreResponse> responseStructure;
	
	
	private StoreResponse mapToStoreResponse(Store store)
	{
		return StoreResponse.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.about(store.getAbout())
				.build();
	}

	private Store mapToStoreRequest(StoreRequest storeRequest) 
	{
		return Store.builder()
				.storeName(storeRequest.getStoreName())
				.about(storeRequest.getAbout())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> addStore(StoreRequest storeRequest) {
		Store store = storeRepository.save(mapToStoreRequest(storeRequest));
		StoreResponse mapToStoreResponse = mapToStoreResponse(store);
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("Store Created Successfully...!");
		responseStructure.setData(mapToStoreResponse);
		return new ResponseEntity<ResponseStructure<StoreResponse>>(responseStructure,HttpStatus.CREATED);
		
	}



	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(int storeId, StoreRequest storeRequest) {
		Optional<Store> optional = storeRepository.findById(storeId);
		
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("No store for given id");
		}
		
		Store store = optional.get();
		store.setStoreName(storeRequest.getStoreName());
		store.setAbout(storeRequest.getAbout());
		
		store = storeRepository.save(store);
		StoreResponse mapToStoreResponse = mapToStoreResponse(store);
		
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("Updated Successfully...!");
		responseStructure.setData(mapToStoreResponse);
		return new ResponseEntity<ResponseStructure<StoreResponse>>(responseStructure,HttpStatus.OK);
		
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreById(int storeId) {
		Optional<Store> optional = storeRepository.findById(storeId);
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("No store for given id");
		}
		Store store = optional.get();
		StoreResponse mapToStoreResponse = mapToStoreResponse(store);
		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setData(mapToStoreResponse);
		responseStructure.setMessage("Data fetched Successfully...!");
		return new ResponseEntity<ResponseStructure<StoreResponse>>(responseStructure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> findStoreBySeller(int sellerId) {
		Optional<Seller> optional = sellerRepository.findById(sellerId);
		if(!optional.isPresent()) {
			throw new IllegalArgumentException("No store for given id");
		}
		
		Seller seller = optional.get();
		Store store = seller.getStore();
		
		StoreResponse mapToStoreResponse = mapToStoreResponse(store);
		responseStructure.setData(mapToStoreResponse);
		responseStructure.setMessage("Store fetched successfully...!");
		responseStructure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<StoreResponse>>(responseStructure,HttpStatus.OK);
		
	}
	

}
