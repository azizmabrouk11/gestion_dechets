package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
import com.nourproject.backend.dtos.vehicule.VehiculeUpdateDto;
import com.nourproject.backend.entities.Vehicule;

public interface VehiculeService {

    Response findAll();

    Response findById(String id);

    Response findByMatricul(String matricul);

    Response save(VehiculeDto vehiculeDto);

    Response updateById(String id, VehiculeUpdateDto vehiculeUpdateDto);

    Response deleteById(String id);

    Vehicule getByMatricul(String matricul);
}
