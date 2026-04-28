package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.LoginRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.AuthResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.UserResponseDTO;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.User;
import com.SupplementDistributor.SupplementDistributor.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final IUserRepository userRepository;
   private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());


        return AuthResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .user(Mapper.toDTO(user))
                .build();
    }

}
