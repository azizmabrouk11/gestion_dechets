package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Notification.NotificationDto;
import com.nourproject.backend.dtos.Notification.NotificationUpdateDto;
import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.services.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Response> getAllNotifications() {
        return ResponseEntity.ok(notificationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getNotificationById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Response> getNotificationsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.findByUserId(userId));
    }

    @GetMapping("/container/{containerId}")
    public ResponseEntity<Response> getNotificationsByContainerId(@PathVariable String containerId) {
        return ResponseEntity.ok(notificationService.findByContainerId(containerId));
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<Response> getNotificationsByIncidentId(@PathVariable String incidentId) {
        return ResponseEntity.ok(notificationService.findByIncidentId(incidentId));
    }

    @PostMapping
    public ResponseEntity<Response> createNotification(@RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.save(notificationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateNotification(
            @PathVariable String id,
            @RequestBody NotificationUpdateDto notificationUpdateDto) {
        return ResponseEntity.ok(notificationService.updateById(id, notificationUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteNotification(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.deleteById(id));
    }
}
