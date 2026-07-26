package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.LoginRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.AuthResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.RoleName;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.model.User;
import com.SupplementDistributor.SupplementDistributor.repository.IUserRepository;
import com.SupplementDistributor.SupplementDistributor.security.JwtService;
import com.SupplementDistributor.SupplementDistributor.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequestDTO loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@supplements.com")
                .password("$2a$10$hashedpassword")
                .phone("1234567890")
                .role(RoleName.ADMIN)
                .active(true)
                .build();

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("admin@supplements.com");
        loginRequest.setPassword("admin123");
    }

    // ─────────────────────────────────────────
    // login
    // ─────────────────────────────────────────

    @Test
    void login_withValidCredentials_shouldReturnTokenAndUser() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("admin@supplements.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("admin@supplements.com", "ADMIN"))  // ← corregido
                .thenReturn("mocked.jwt.token");

        // Act
        AuthResponseDTO result = authService.login(loginRequest);

        // Assert
        assertThat(result.getToken()).isEqualTo("mocked.jwt.token");
        assertThat(result.getType()).isEqualTo("Bearer");
        assertThat(result.getUser().getEmail()).isEqualTo("admin@supplements.com");
        assertThat(result.getUser().getRole()).isEqualTo(RoleName.ADMIN);
    }

    @Test
    void login_shouldNotExposePassword() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString()))  // ← corregido
                .thenReturn("mocked.jwt.token");

        // Act
        AuthResponseDTO result = authService.login(loginRequest);

        // Assert
        assertThat(result.getUser().getClass().getDeclaredFields())
                .noneMatch(f -> f.getName().equals("password"));
    }

    @Test
    void login_withInvalidCredentials_shouldThrowBadCredentialsException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        // Verifica que nunca llegó a buscar el usuario en la BD
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_whenUserNotFoundAfterAuth_shouldThrowResourceNotFoundException() {
        // Arrange — autenticación exitosa pero usuario no existe en BD
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("admin@supplements.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_shouldCallAuthenticationManagerWithCorrectCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString()))  // ← corregido
                .thenReturn("token");

        // Act
        authService.login(loginRequest);

        // Assert
        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals("admin@supplements.com") &&
                                auth.getCredentials().equals("admin123")
                )
        );
    }

    // ─────────────────────────────────────────
    // logout
    // ─────────────────────────────────────────

    @Test
    void logout_withActiveToken_shouldBlacklistToken() {
        // Arrange
        String token = "valid.jwt.token";
        when(jwtService.getRemainingExpiration(token)).thenReturn(3600000L); // 1 hora restante

        // Act
        authService.logout(token);

        // Assert — verifica que se guardó en la blacklist con el TTL correcto
        verify(tokenBlacklistService).blacklistToken(token, 3600000L);
    }

    @Test
    void logout_withExpiredToken_shouldNotBlacklistToken() {
        // Arrange
        String token = "expired.jwt.token";
        when(jwtService.getRemainingExpiration(token)).thenReturn(-1000L); // ya expiró

        // Act
        authService.logout(token);

        // Assert — si el token ya expiró no tiene sentido guardarlo en Redis
        verify(tokenBlacklistService, never()).blacklistToken(any(), anyLong());
    }

    @Test
    void logout_withTokenAboutToExpire_shouldStillBlacklist() {
        // Arrange
        String token = "almost.expired.token";
        when(jwtService.getRemainingExpiration(token)).thenReturn(1000L); // 1 segundo restante

        // Act
        authService.logout(token);

        // Assert — si tiene aunque sea 1ms restante, hay que blacklistearlo
        verify(tokenBlacklistService).blacklistToken(token, 1000L);
    }
}