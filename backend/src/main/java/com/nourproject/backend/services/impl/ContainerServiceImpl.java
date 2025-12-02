package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.container.ContainerUpdateDto;
import com.nourproject.backend.entities.Container;
import com.nourproject.backend.enums.ContainerStatus;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.ContainerMapper;
import com.nourproject.backend.repositories.ContainerRepository;
import com.nourproject.backend.services.interfaces.ContainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContainerServiceImpl implements ContainerService {

    private final ContainerMapper containerMapper;
    private final ContainerRepository containerRepository;

    @Override
    public Response findAll() {
        List<ContainerDto> list = containerRepository.findAll().stream()
                .map(containerMapper::containerToContainerDto).toList();

        return Response.builder()
                .status(200)
                .message("List of containers retrieved successfully")
                .containers(list)
                .build();
    }

    @Override
    public Response findById(String id) {
        ContainerDto containerDto = containerRepository.findById(id)
                .map(containerMapper::containerToContainerDto)
                .orElseThrow(() -> new NotFoundException("Container with ID " + id + " not found"));
        
        return Response.builder()
                .status(200)
                .message("Container retrieved successfully")
                .container(containerDto)
                .build();
    }

    @Override
    public Response findByPickUpPointId(String pickUpPointId) {
        List<ContainerDto> list = containerRepository.findByPickUpPointId(pickUpPointId).stream()
                .map(containerMapper::containerToContainerDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Containers for pickup point retrieved successfully")
                .containers(list)
                .build();
    }

    @Override
    public Response findByStatus(ContainerStatus containerStatus) {
        List<ContainerDto> list = containerRepository.findByContainerStatus(containerStatus).stream()
                .map(containerMapper::containerToContainerDto).toList();
        
        return Response.builder()
                .status(200)
                .message("Containers with status " + containerStatus + " retrieved successfully")
                .containers(list)
                .build();
    }

    @Override
    public Response save(ContainerDto containerDto) {
        Container container = containerMapper.containerDtoToContainer(containerDto);
        Container savedContainer = containerRepository.save(container);
        ContainerDto savedDto = containerMapper.containerToContainerDto(savedContainer);
        
        return Response.builder()
                .status(201)
                .message("Container created successfully")
                .container(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, ContainerUpdateDto containerUpdateDto) {
        Container container = containerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Container with ID " + id + " not found"));
        
        containerMapper.updateContainerFromDto(containerUpdateDto, container);
        Container updatedContainer = containerRepository.save(container);
        ContainerDto updatedDto = containerMapper.containerToContainerDto(updatedContainer);
        
        return Response.builder()
                .status(200)
                .message("Container updated successfully")
                .container(updatedDto)
                .build();
    }

    @Override
    @Transactional
    public Response deleteById(String id) {
        Container container = containerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Container with ID " + id + " not found"));
        
        ContainerDto deletedDto = containerMapper.containerToContainerDto(container);
        containerRepository.delete(container);
        
        return Response.builder()
                .status(200)
                .message("Container deleted successfully")
                .container(deletedDto)
                .build();
    }
}
