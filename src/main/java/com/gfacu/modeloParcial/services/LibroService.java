package com.gfacu.modeloParcial.services;

import com.gfacu.modeloParcial.models.Libro;
import com.gfacu.modeloParcial.repositories.LibroRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {
    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public Libro crearLibro(Libro libro) {
        validarLibro(libro);

        try{
            libro.setId(null);
            return libroRepository.save(libro);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public void borrarLibro(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        Libro libroBorrar = buscarPorId(id);

        try{
            libroRepository.delete(libroBorrar);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public Libro modificarLibro(Long id, Libro libro) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        validarLibro(libro);

        Libro libroGuardar = buscarPorId(id);

        libroGuardar.setId(id);
        libroGuardar.setCategoria(libro.getCategoria());
        libroGuardar.setTitulo(libro.getTitulo());
        libroGuardar.setAutor(libro.getAutor());
        libroGuardar.setCantidadDisponible(libro.getCantidadDisponible());

        try{
            return libroRepository.save(libroGuardar);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public Libro buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return libroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El libro no existe."));
    }

    public List<Libro> listarLibrosPorCategoria(String categoria) {
        return listarLibros().stream().filter(lib -> lib.getCategoria().equalsIgnoreCase(categoria)).toList();
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }


    private void validarLibro(Libro libro) {
        if(libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El titulo es obligatorio.");
        }

        if(libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }

        if(libro.getCategoria() == null ||libro.getCategoria().isBlank()) {
            throw new IllegalArgumentException("La categoria es obligatoria.");
        }

        if(libro.getCantidadDisponible() == null || libro.getCantidadDisponible() < 0) {
            throw new IllegalArgumentException("La cantidad del libro no puede ser negativa.");
        }
    }
}
