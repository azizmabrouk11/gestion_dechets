package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
import com.nourproject.backend.dtos.vehicule.VehiculeUpdateDto;
import com.nourproject.backend.entities.Vehicule;
import com.nourproject.backend.exceptions.GlobalException;
import com.nourproject.backend.exceptions.NotFoundException;
import com.nourproject.backend.mappers.VehiculeMapper;
import com.nourproject.backend.repositories.VehiculeRepository;
import com.nourproject.backend.services.interfaces.VehiculeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements VehiculeService {

    private final VehiculeMapper vehiculeMapper;
    private final VehiculeRepository vehiculeRepository;

    @Override
    public Response findAll() {
        List<VehiculeDto> list = vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::vehiculeToVehiculeDto).toList();

        return Response.builder()
                .status(200)
                .message("List of vehicules retrieved successfully")
                .vehicules(list)
                .build();
    }

    @Override
    public Response findById(String id) {
        VehiculeDto vehiculeDto = vehiculeRepository.findById(id)
                .map(vehiculeMapper::vehiculeToVehiculeDto)
                .orElseThrow(() -> new NotFoundException("Vehicule with ID " + id + " not found"));
        
        return Response.builder()
                .status(200)
                .message("Vehicule retrieved successfully")
                .vehicule(vehiculeDto)
                .build();
    }

    @Override
    public Response findByMatricul(String matricul) {
        VehiculeDto vehiculeDto = vehiculeRepository.findByMatricul(matricul)
                .map(vehiculeMapper::vehiculeToVehiculeDto)
                .orElseThrow(() -> new NotFoundException("Vehicule with matricul " + matricul + " not found"));
        
        return Response.builder()
                .status(200)
                .message("Vehicule retrieved successfully")
                .vehicule(vehiculeDto)
                .build();
    }

    @Override
    public Response save(VehiculeDto vehiculeDto) {
        Vehicule vehicule = vehiculeMapper.vehiculeDtoToVehicule(vehiculeDto);
        Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        VehiculeDto savedDto = vehiculeMapper.vehiculeToVehiculeDto(savedVehicule);
        
        return Response.builder()
                .status(201)
                .message("Vehicule created successfully")
                .vehicule(savedDto)
                .build();
    }

    @Override
    public Response updateById(String id, VehiculeUpdateDto vehiculeUpdateDto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicule with ID " + id + " not found"));
        
        vehiculeMapper.updateVehiculeFromDto(vehiculeUpdateDto, vehicule);
        Vehicule updatedVehicule = vehiculeRepository.save(vehicule);
        VehiculeDto updatedDto = vehiculeMapper.vehiculeToVehiculeDto(updatedVehicule);
        
        return Response.builder()
                .status(200)
                .message("Vehicule updated successfully")
                .vehicule(updatedDto)
                .build();
    }

    @Override
    @Transactional
    public Response deleteById(String id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehicule with ID " + id + " not found"));
        
        VehiculeDto deletedDto = vehiculeMapper.vehiculeToVehiculeDto(vehicule);
        vehiculeRepository.delete(vehicule);
        
        return Response.builder()
                .status(200)
                .message("Vehicule deleted successfully")
                .vehicule(deletedDto)
                .build();
    }

    @Override
    public Vehicule getByMatricul(String matricul) {
        return vehiculeRepository.findByMatricul(matricul)
                .orElseThrow(() -> new NotFoundException("Vehicule with matricul " + matricul + " not found"));
    }
}
