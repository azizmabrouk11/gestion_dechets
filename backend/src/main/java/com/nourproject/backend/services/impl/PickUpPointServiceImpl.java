package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.entities.Container;
import com.nourproject.backend.entities.PickUpPoint;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.PickUpPointMapper;
import com.nourproject.backend.repositories.ContainerRepository;
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
    private final ContainerRepository containerRepository;

    @Override
    public Response findAll() {
        List<PickUpPoint> pickUpPoints = pickUpPointRepository.findAll();
        
        // Populate containers for each pickup point
        pickUpPoints.forEach(pickUpPoint -> {
            List<Container> containers = containerRepository.findByPickUpPointId(pickUpPoint.get_id());
            pickUpPoint.setContainers(containers);
        });
        
        List<PickUpPointDto> list = pickUpPoints.stream()
                .map(pickUpPointMapper::pickUpPointToPickUpPointDto).toList();

        return Response.builder()
                .status(200)
                .message("List of pickup points retrieved successfully")
                .pickuppoints(list)
                .build();
    }
    @Override
    public Response findAllFull() {
        List<PickUpPoint> pickUpPoints = pickUpPointRepository.findAll();
        
        // Filter pickup points with containers >= 80% full
        List<PickUpPointDto> list = pickUpPoints.stream()
                .filter(p -> p.getContainers() != null && p.getContainers().stream()
                        .anyMatch(c -> ((double) c.getFillLevel() / c.getCapacity()) * 100 >= 80)
                )
                .map(pickUpPointMapper::pickUpPointToPickUpPointDto)
                .toList();

        return Response.builder()
                .status(200)
                .message("List of pickup points retrieved successfully")
                .pickuppoints(list)
                .build();
    }



    @Override
    public Response findById(String id) {
        PickUpPoint pickUpPoint = pickUpPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));
        
        // Populate containers from the database
        List<Container> containers = containerRepository.findByPickUpPointId(pickUpPoint.get_id());
        pickUpPoint.setContainers(containers);
        
        PickUpPointDto pickUpPointDto = pickUpPointMapper.pickUpPointToPickUpPointDto(pickUpPoint);

        return Response.builder()
                .status(200)
                .message("PickUp Point retrieved successfully")
                .pickuppoint(pickUpPointDto)
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
                .pickuppoint(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, PickUpPointUpdateDto pickUpPointUpdateDto) {
        PickUpPoint pickUpPoint = pickUpPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));

        pickUpPointMapper.updatePickUpPointFromDto(pickUpPointUpdateDto, pickUpPoint);
        
        // Fetch and set the containers based on pickUpPointId
        List<Container> containers = containerRepository.findByPickUpPointId(id);
        pickUpPoint.setContainers(containers);
        
        PickUpPoint updatedPickUpPoint = pickUpPointRepository.save(pickUpPoint);
        
        PickUpPointDto updatedDto = pickUpPointMapper.pickUpPointToPickUpPointDto(updatedPickUpPoint);

        return Response.builder()
                .status(200)
                .message("PickUp Point updated successfully")
                .pickuppoint(updatedDto)
                .build();
    }

    @Override
    public Response syncContainers(String id) {
        PickUpPoint pickUpPoint = pickUpPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("PickUp Point with ID " + id + " not found"));
        
        // Fetch all containers assigned to this pickup point
        List<Container> containers = containerRepository.findByPickUpPointId(id);
        pickUpPoint.setContainers(containers);
        
        // Save the pickup point with updated container references
        PickUpPoint updatedPickUpPoint = pickUpPointRepository.save(pickUpPoint);
        PickUpPointDto updatedDto = pickUpPointMapper.pickUpPointToPickUpPointDto(updatedPickUpPoint);
        
        return Response.builder()
                .status(200)
                .message("Containers synced successfully")
                .pickuppoint(updatedDto)
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
                .pickuppoint(deletedDto)
                .build();
    }
}
