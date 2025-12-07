package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.PickUpPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PickUpPointRepository extends MongoRepository<PickUpPoint, String> {
    // Custom query to find pickup points by _id field using string conversion
    @Query(value = "{ '_id': { '$in': ?0 } }")
    List<PickUpPoint> findByIdIn(List<String> ids);
}
