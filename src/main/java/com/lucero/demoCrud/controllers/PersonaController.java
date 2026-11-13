package com.lucero.demoCrud.controllers;

import com.lucero.demoCrud.dto.PersonaRequestDTO;
import com.lucero.demoCrud.dto.PersonaResponseDTO;
import com.lucero.demoCrud.models.CiudadOrigen;
import com.lucero.demoCrud.models.Persona;
import com.lucero.demoCrud.repository.CiudadOrigenRepository;
import com.lucero.demoCrud.repository.PersonaRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaRepository personaRepository;
    private final CiudadOrigenRepository ciudadOrigenRepository;

    public PersonaController(PersonaRepository personaRepository, CiudadOrigenRepository ciudadOrigenRepository) {
        this.personaRepository = personaRepository;
        this.ciudadOrigenRepository = ciudadOrigenRepository;
    }

    // Método auxiliar para convertir Entity a DTO de respuesta
    private PersonaResponseDTO convertirADTO(Persona p) {
        String nombreCiudad = (p.getCiudadOrigen() != null)
                ? p.getCiudadOrigen().getNombre() + " (" + p.getCiudadOrigen().getEstado() + ")"
                : "Sin ciudad asignada";
        return new PersonaResponseDTO(p.getId(), p.getNombre(), p.getApellido(), nombreCiudad);
    }

    // a) GET para listar con paginación: /api/personas?page=0&size=5
    @GetMapping
    public ResponseEntity<Page<PersonaResponseDTO>> listarPersonas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PersonaResponseDTO> pagina = personaRepository.findAll(pageable).map(this::convertirADTO);
        return ResponseEntity.ok(pagina);
    }

    // a) GET para ver un registro individual por ID: /api/personas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Persona con ID " + id + " no encontrada"));
        }
        return ResponseEntity.ok(convertirADTO(personaOpt.get()));
    }

    // b) POST para crear una nueva Persona
    @PostMapping
    public ResponseEntity<?> crearPersona(@Valid @RequestBody PersonaRequestDTO dto) {
        Optional<CiudadOrigen> ciudadOpt = ciudadOrigenRepository.findById(dto.getCiudadOrigenId());
        if (ciudadOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La CiudadOrigen con ID " + dto.getCiudadOrigenId() + " no existe"));
        }

        Persona nueva = new Persona();
        nueva.setNombre(dto.getNombre());
        nueva.setApellido(dto.getApellido());
        nueva.setCiudadOrigen(ciudadOpt.get());

        Persona guardada = personaRepository.save(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(guardada));
    }

    // c) PUT para actualizar una Persona existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable Long id, @Valid @RequestBody PersonaRequestDTO dto) {
        Optional<Persona> personaOpt = personaRepository.findById(id);
        if (personaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Persona con ID " + id + " no encontrada"));
        }

        Optional<CiudadOrigen> ciudadOpt = ciudadOrigenRepository.findById(dto.getCiudadOrigenId());
        if (ciudadOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La CiudadOrigen con ID " + dto.getCiudadOrigenId() + " no existe"));
        }

        Persona existente = personaOpt.get();
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setCiudadOrigen(ciudadOpt.get());

        Persona actualizada = personaRepository.save(existente);
        return ResponseEntity.ok(convertirADTO(actualizada));
    }

    // d) DELETE para eliminar una Persona
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable Long id) {
        if (!personaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Persona con ID " + id + " no encontrada"));
        }
        personaRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Persona eliminada correctamente"));
    }
}