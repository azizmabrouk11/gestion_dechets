package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.entities.PickUpPoint;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.PickUpPointMapper;
import com.nourproject.backend.repositories.PickUpPointRepository;
import com.nourproject.backend.services.interfaces.PickUpPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PickUpPointServiceImpl implements PickUpPointService {

    private final PickUpPointMapper pickUpPointMapper;
    private final PickUpPointRepository pickUpPointRepository;

    @Override
    public Response findAll() {
        List<PickUpPointDto> list = pickUpPointRepository.findAll().stream()
                .map(pickUpPointMapper::pickUpPointToPickUpPointDto).toList();

        return Response.builder()
                .status(200)
                .message("List of pickup points retrieved successfully")
                .pickUpPoints(list)
                .build();
    }

    @Override
    public Response findById(String id) {
        PickUpPointDto pickUpPointDto = pickUpPointRepository.findById(id)
                .map(pickUpPointMapper::pickUpPointToPickUpPointDto)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));
        
        return Response.builder()
                .status(200)
                .message("PickUp Point retrieved successfully")
                .pickUpPoint(pickUpPointDto)
                .build();
    }

    @Override
    public Response findByLocation(String location) {
        PickUpPointDto pickUpPointDto = pickUpPointRepository.findByLocation(location)
                .map(pickUpPointMapper::pickUpPointToPickUpPointDto)
                .orElseThrow(() -> new NotFoundException("PickUp Point with location " + location + " not found"));
        
        return Response.builder()
                .status(200)
                .message("PickUp Point retrieved successfully")
                .pickUpPoint(pickUpPointDto)
                .build();
    }

    @Override
    public Response save(PickUpPointDto pickUpPointDto) {
        PickUpPoint pickUpPoint = pickUpPointMapper.pickUpPointDtoToPickUpPoint(pickUpPointDto);
        PickUpPoint savedPickUpPoint = pickUpPointRepository.save(pickUpPoint);
        PickUpPointDto savedDto = pickUpPointMapper.pickUpPointToPickUpPointDto(savedPickUpPoint);
        
        return Response.builder()
                .status(201)
                .message("PickUp Point created successfully")
                .pickUpPoint(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, PickUpPointUpdateDto pickUpPointUpdateDto) {
        PickUpPoint pickUpPoint = pickUpPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));
        
        pickUpPointMapper.updatePickUpPointFromDto(pickUpPointUpdateDto, pickUpPoint);
        PickUpPoint updatedPickUpPoint = pickUpPointRepository.save(pickUpPoint);
        PickUpPointDto updatedDto = pickUpPointMapper.pickUpPointToPickUpPointDto(updatedPickUpPoint);
        
        return Response.builder()
                .status(200)
                .message("PickUp Point updated successfully")
                .pickUpPoint(updatedDto)
                .build();
    }

    @Override
    @Transactional
    public Response deleteById(String id) {
        PickUpPoint pickUpPoint = pickUpPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));
        
        PickUpPointDto deletedDto = pickUpPointMapper.pickUpPointToPickUpPointDto(pickUpPoint);
        pickUpPointRepository.delete(pickUpPoint);
        
        return Response.builder()
                .status(200)
                .message("PickUp Point deleted successfully")
                .pickUpPoint(deletedDto)
                .build();
    }

    @Override
    public PickUpPoint getByLocation(String location) {
        return pickUpPointRepository.findByLocation(location)
                .orElseThrow(() -> new NotFoundException("PickUp Point with location " + location + " not found"));
    }
}
