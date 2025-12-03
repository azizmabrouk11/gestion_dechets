package com.nourproject.backend.services.impl;

import com.nourproject.backend.dtos.Notification.NotificationDto;
import com.nourproject.backend.dtos.Notification.NotificationUpdateDto;
import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.entities.Notification;
import com.nourproject.backend.mappers.NotificationMapper;
import com.nourproject.backend.repositories.NotificationRepository;
import com.nourproject.backend.services.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Async
    public void sendEmail(NotificationDto notificationDto) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(notificationDto.getRecipient());
            mail.setSubject(notificationDto.getSubject());
            mail.setText(notificationDto.getBody());
            mailSender.send(mail);
            log.info("Email sent successfully to: {}", notificationDto.getRecipient());
            notificationRepository.save(notificationMapper.toEntity(notificationDto));
        } catch (Exception e) {
            log.error("Failed to send email to: {}", notificationDto.getRecipient(), e);
        }
    }

    @Override
    public Response findAll() {
        List<Notification> notifications = notificationRepository.findAll();
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully")
                .notifications(notificationDtos)
                .build();
    }

    @Override
    public Response findById(String id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return Response.builder()
                .status(200)
                .message("Notification retrieved successfully")
                .notification(notificationMapper.toDto(notification))
                .build();
    }

    @Override
    public Response findByUserId(String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully for user: " + userId)
                .notifications(notificationDtos)
                .build();
    }

    @Override
    public Response findByContainerId(String containerId) {
        List<Notification> notifications = notificationRepository.findByContainerId(containerId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully for container: " + containerId)
                .notifications(notificationDtos)
                .build();
    }

    @Override
    public Response findByIncidentId(String incidentId) {
        List<Notification> notifications = notificationRepository.findByIncidentId(incidentId);
        List<NotificationDto> notificationDtos = notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
        return Response.builder()
                .status(200)
                .message("Notifications retrieved successfully for incident: " + incidentId)
                .notifications(notificationDtos)
                .build();
    }

    @Override
    public Response save(NotificationDto notificationDto) {
        Notification notification = notificationMapper.toEntity(notificationDto);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send email asynchronously if recipient is provided
        if (notificationDto.getRecipient() != null && !notificationDto.getRecipient().isEmpty()) {
            sendEmail(notificationDto);
        }
        
        return Response.builder()
                .status(201)
                .message("Notification created successfully")
                .notification(notificationMapper.toDto(savedNotification))
                .build();
    }

    @Override
    public Response updateById(String id, NotificationUpdateDto notificationUpdateDto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        notificationMapper.updateNotificationFromDto(notificationUpdateDto, notification);
        Notification updatedNotification = notificationRepository.save(notification);
        
        return Response.builder()
                .status(200)
                .message("Notification updated successfully")
                .notification(notificationMapper.toDto(updatedNotification))
                .build();
    }

    @Override
    public Response deleteById(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
        return Response.builder()
                .status(200)
                .message("Notification deleted successfully")
                .build();
    }
}
