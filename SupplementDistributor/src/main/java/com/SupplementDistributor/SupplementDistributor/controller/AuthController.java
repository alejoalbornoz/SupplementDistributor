package com.SupplementDistributor.SupplementDistributor.controller;


import com.SupplementDistributor.SupplementDistributor.dto.request.LoginRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.AuthResponseDTO;
import com.SupplementDistributor.SupplementDistributor.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Saca el "Bearer "
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
