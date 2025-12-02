package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.dtos.container.ContainerDto;
import com.nourproject.backend.dtos.container.ContainerUpdateDto;
import com.nourproject.backend.enums.ContainerStatus;
import com.nourproject.backend.services.interfaces.ContainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerService containerService;

    @GetMapping
    public ResponseEntity<Response> getAllContainers() {
        return ResponseEntity.ok(containerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getContainerById(@PathVariable String id) {
        return ResponseEntity.ok(containerService.findById(id));
    }

    @GetMapping("/pickuppoint/{pickUpPointId}")
    public ResponseEntity<Response> getContainersByPickUpPoint(@PathVariable String pickUpPointId) {
        return ResponseEntity.ok(containerService.findByPickUpPointId(pickUpPointId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Response> getContainersByStatus(@PathVariable ContainerStatus status) {
        return ResponseEntity.ok(containerService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Response> createContainer(@Valid @RequestBody ContainerDto containerDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(containerService.save(containerDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateContainer(
            @PathVariable String id,
            @Valid @RequestBody ContainerUpdateDto containerUpdateDto) {
        return ResponseEntity.ok(containerService.updateById(id, containerUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteContainer(@PathVariable String id) {
        return ResponseEntity.ok(containerService.deleteById(id));
    }
}
