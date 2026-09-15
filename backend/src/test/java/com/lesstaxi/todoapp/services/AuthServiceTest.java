package com.lesstaxi.todoapp.services;

import com.lesstaxi.todoapp.models.ERole;
import com.lesstaxi.todoapp.models.User;
import com.lesstaxi.todoapp.payload.request.LoginRequest;
import com.lesstaxi.todoapp.payload.request.SignupRequest;
import com.lesstaxi.todoapp.payload.response.JwtResponse;
import com.lesstaxi.todoapp.repositories.UserRepository;
import com.lesstaxi.todoapp.security.jwt.JwtUtils;
import com.lesstaxi.todoapp.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    @Test
    void testAuthenticateUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl("1", "testuser", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockedJwtToken");

        JwtResponse response = authService.authenticateUser(request);

        assertNotNull(response);
        assertEquals("mockedJwtToken", response.getAccessToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void testRegisterUserSuccess() {
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        assertDoesNotThrow(() -> authService.registerUser(request));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserUsernameExists() {
        SignupRequest request = new SignupRequest();
        request.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.registerUser(request));
        assertTrue(exception.getMessage().contains("Username is already taken"));
        verify(userRepository, never()).save(any(User.class));
    }
}
