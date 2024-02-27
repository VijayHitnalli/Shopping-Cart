package com.shoppingcart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppingcart.entity.Image;

public interface ImageRepository extends MongoRepository<Image, String>{

}
