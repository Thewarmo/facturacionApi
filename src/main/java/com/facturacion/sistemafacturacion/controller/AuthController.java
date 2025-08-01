package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.AuthRequest;
import com.facturacion.sistemafacturacion.dto.AuthResponse;
import com.facturacion.sistemafacturacion.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "authorization methods")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    @Operation(summary = "Hacer la autorización")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refrescar access token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthResponse request) {
        String refreshToken = request.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);

        if (username == null || jwtService.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(403).build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }
}
