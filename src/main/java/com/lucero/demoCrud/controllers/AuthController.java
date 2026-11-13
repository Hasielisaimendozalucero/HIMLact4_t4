package com.lucero.demoCrud.controllers;

import com.lucero.demoCrud.dto.AuthRequestDTO;
import com.lucero.demoCrud.dto.AuthResponseDTO;
import com.lucero.demoCrud.models.Usuario;
import com.lucero.demoCrud.repository.UsuarioRepository;
import com.lucero.demoCrud.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre de usuario ya está registrado"));
        }

        Usuario nuevoUsuario = new Usuario(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        usuarioRepository.save(nuevoUsuario);

        String token = jwtUtil.generarToken(nuevoUsuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponseDTO(token, nuevoUsuario.getUsername(), "Usuario registrado exitosamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), usuarioOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }

        String token = jwtUtil.generarToken(usuarioOpt.get().getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token, usuarioOpt.get().getUsername(), "Login exitoso"));
    }
}
