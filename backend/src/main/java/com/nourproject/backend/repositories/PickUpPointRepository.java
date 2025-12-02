package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.PickUpPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PickUpPointRepository extends MongoRepository<PickUpPoint, String> {
    Optional<PickUpPoint> findByLocation(String location);
}
