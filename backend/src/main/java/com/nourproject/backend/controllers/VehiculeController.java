package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.vehicule.VehiculeDto;
import com.nourproject.backend.dtos.vehicule.VehiculeUpdateDto;
import com.nourproject.backend.services.interfaces.VehiculeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;

    @GetMapping
    public ResponseEntity<Response> getAllVehicules() {
        return ResponseEntity.ok(vehiculeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getVehiculeById(@PathVariable String id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    @GetMapping("/matricul/{matricul}")
    public ResponseEntity<Response> getVehiculeByMatricul(@PathVariable String matricul) {
        return ResponseEntity.ok(vehiculeService.findByMatricul(matricul));
    }

    @PostMapping
    public ResponseEntity<Response> createVehicule(@Valid @RequestBody VehiculeDto vehiculeDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.save(vehiculeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateVehicule(
            @PathVariable String id,
            @Valid @RequestBody VehiculeUpdateDto vehiculeUpdateDto) {
        return ResponseEntity.ok(vehiculeService.updateById(id, vehiculeUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteVehicule(@PathVariable String id) {
        return ResponseEntity.ok(vehiculeService.deleteById(id));
    }
}
