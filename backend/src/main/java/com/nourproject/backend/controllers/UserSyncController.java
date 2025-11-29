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
 * User Sync Controller
 * 
 * Provides endpoint for manually triggering user synchronization
 * between Keycloak and the database.
 * 
 * @author Senior Developer
 * @version 1.0
 * @since 2025-11-28
 */
@RestController
@RequestMapping("/api/admin/sync")
@RequiredArgsConstructor
@Slf4j
public class UserSyncController {
    
    private final UserSyncService userSyncService;
    
    /**
     * Manually trigger user synchronization from Keycloak
     * This is useful when you need to sync users without restarting the application
     * 
     * @return ResponseEntity with sync status
     */
    @PostMapping("/users")
    public ResponseEntity<Response> syncUsers() {
        log.info("POST /api/admin/sync/users - Manual user sync triggered");
        
        try {
            userSyncService.manualSync();
            
            Response response = Response.builder()
                    .status(200)
                    .message("User synchronization completed successfully")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error during manual user sync", e);
            
            Response response = Response.builder()
                    .status(500)
                    .message("User synchronization failed: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
