package com.lucero.demoCrud.dto;

public class PersonaResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCiudad;

    public PersonaResponseDTO() {}

    public PersonaResponseDTO(Long id, String nombre, String apellido, String nombreCiudad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreCiudad = nombreCiudad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }
}