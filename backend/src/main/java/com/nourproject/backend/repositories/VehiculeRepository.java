package com.nourproject.backend.repositories;

import com.nourproject.backend.entities.Vehicule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VehiculeRepository extends MongoRepository<Vehicule, String> {
    Optional<Vehicule> findByMatricul(String matricul);
}
