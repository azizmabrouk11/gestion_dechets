package com.nourproject.backend.controllers;

import com.nourproject.backend.dtos.Response;
import com.nourproject.backend.services.UserSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Controller
 * 
 * Provides administrative endpoints for system management.
 * Includes user synchronization with Keycloak.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserSyncService userSyncService;

    /**
     * Manually trigger user synchronization from Keycloak
     * 
     * @return ResponseEntity with sync result
     */
    @PostMapping("/sync-users")
    public ResponseEntity<Response> syncUsers() {
        log.info("POST /api/admin/sync-users - Manual user sync triggered");
        
        try {
            userSyncService.manualSync();
            
            return ResponseEntity.ok(Response.builder()
                    .status(200)
                    .message("User synchronization completed successfully")
                    .build());
        } catch (Exception e) {
            log.error("Error during manual user sync", e);
            return ResponseEntity.status(500).body(Response.builder()
                    .status(500)
                    .message("User synchronization failed: " + e.getMessage())
                    .build());
        }
    }
}
