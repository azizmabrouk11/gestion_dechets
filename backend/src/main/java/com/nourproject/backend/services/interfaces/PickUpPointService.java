package com.nourproject.backend.services.interfaces;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.entities.PickUpPoint;

public interface PickUpPointService {

    Response findAll();

    Response findById(String id);

    Response findByLocation(String location);

    Response save(PickUpPointDto pickUpPointDto);

    Response updateById(String id, PickUpPointUpdateDto pickUpPointUpdateDto);

    Response deleteById(String id);

    PickUpPoint getByLocation(String location);
}
