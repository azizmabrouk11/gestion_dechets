package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.PickUpPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PickUpPointRepository extends MongoRepository<PickUpPoint, String> {
    // Query methods can be added here if needed
    // For location queries, use custom queries with @Query annotation
}
