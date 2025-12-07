package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Container;
import com.nourproject.backend.enums.ContainerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContainerRepository extends MongoRepository<Container, String> {
    @Query("{ 'pickUpPointId': ?0 }")
    List<Container> findByPickUpPointId(String pickUpPointId);
    
    List<Container> findByContainerStatus(ContainerStatus containerStatus);
}
