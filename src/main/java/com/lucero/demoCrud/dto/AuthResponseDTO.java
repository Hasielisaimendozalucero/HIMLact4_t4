package com.lucero.demoCrud.dto;

public class AuthResponseDTO {
    private String token;
    private String username;
    private String mensaje;

    public AuthResponseDTO(String token, String username, String mensaje) {
        this.token = token;
        this.username = username;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}