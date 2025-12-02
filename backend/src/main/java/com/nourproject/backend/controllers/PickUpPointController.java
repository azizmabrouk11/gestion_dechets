package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointDto;
import com.nourproject.backend.dtos.pickuppoint.PickUpPointUpdateDto;
import com.nourproject.backend.services.interfaces.PickUpPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pickuppoints")
@RequiredArgsConstructor
public class PickUpPointController {

    private final PickUpPointService pickUpPointService;

    @GetMapping
    public ResponseEntity<Response> getAllPickUpPoints() {
        return ResponseEntity.ok(pickUpPointService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getPickUpPointById(@PathVariable String id) {
        return ResponseEntity.ok(pickUpPointService.findById(id));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<Response> getPickUpPointByLocation(@PathVariable String location) {
        return ResponseEntity.ok(pickUpPointService.findByLocation(location));
    }

    @PostMapping
    public ResponseEntity<Response> createPickUpPoint(@Valid @RequestBody PickUpPointDto pickUpPointDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pickUpPointService.save(pickUpPointDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updatePickUpPoint(
            @PathVariable String id,
            @Valid @RequestBody PickUpPointUpdateDto pickUpPointUpdateDto) {
        return ResponseEntity.ok(pickUpPointService.updateById(id, pickUpPointUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deletePickUpPoint(@PathVariable String id) {
        return ResponseEntity.ok(pickUpPointService.deleteById(id));
    }
}
