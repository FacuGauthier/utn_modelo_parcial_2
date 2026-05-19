package com.gfacu.modeloParcial.controllers;

import com.gfacu.modeloParcial.models.Libro;
import com.gfacu.modeloParcial.services.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<Libro> listarLibros() {
        return libroService.listarLibros();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscarPorId(@PathVariable Long id) {
        Libro libro = libroService.buscarPorId(id);

        return ResponseEntity.ok(libro);
    }

    @PostMapping
    public ResponseEntity<Libro> crearLibro(@RequestBody Libro libro) {
        Libro libroNew = libroService.crearLibro(libro);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(libroNew);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> modificarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        Libro libroUpdate = libroService.modificarLibro(id,libro);

        return ResponseEntity.ok(libroUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.borrarLibro(id);
        return ResponseEntity.noContent().build();
    }
}
