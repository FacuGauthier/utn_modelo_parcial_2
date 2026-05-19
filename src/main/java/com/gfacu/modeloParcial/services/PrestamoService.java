package com.gfacu.modeloParcial.services;

import com.gfacu.modeloParcial.exceptions.PrestamoInvalidoException;
import com.gfacu.modeloParcial.models.Estado;
import com.gfacu.modeloParcial.models.Libro;
import com.gfacu.modeloParcial.models.Prestamo;
import com.gfacu.modeloParcial.repositories.LibroRepository;
import com.gfacu.modeloParcial.repositories.PrestamoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    public Prestamo registrarPrestamo(Prestamo prestamo) {
        boolean usuario = prestamoRepository.existsByUsuarioId(prestamo.getUsuario().getId());
        boolean libro = prestamoRepository.existsByLibroId(prestamo.getLibro().getId());

        if(!usuario) throw new PrestamoInvalidoException("No existe el usuario.");
        if(!libro) throw new PrestamoInvalidoException("No existe el libro.");

        if(prestamo.getLibro().getCantidadDisponible() < 1) {
            throw new PrestamoInvalidoException("No hay cantidades suficientes.");
        }

        if(prestamo.getFechaDevolucion().isAfter(prestamo.getFechaPrestamo())) {
            throw new PrestamoInvalidoException("La fecha de devolucion debe ser posterior a la fecha del prestamo.");
        }

        try{
            prestamo.setId(null);
            return prestamoRepository.save(prestamo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public void finalizarPrestamo(Long id) {
        Prestamo prestamo = buscarPorId(id);

        if(prestamo.getEstado() == Estado.FINALIZADO) throw new PrestamoInvalidoException("El prestamo ya esta finalizado.");

        prestamo.setEstado(Estado.FINALIZADO);

        Libro libro = libroRepository.findById(prestamo.getLibro().getId())
                .orElseThrow(() -> new PrestamoInvalidoException("Libro no encontrado"));

        libro.setCantidadDisponible(libro.getCantidadDisponible()+1);

        try{
            libroRepository.save(libro);
            prestamoRepository.save(prestamo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoRepository.findAll();
    }

    public List<Prestamo> listarPorUsuario(Long idUsuario) {
        if(idUsuario == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return prestamoRepository.findByUsuarioId(idUsuario);
    }

    public List<Prestamo> listarPrestamosVencidos() {
        List<Prestamo> prestamos = new ArrayList<>();

        for(Prestamo prestamo : prestamoRepository.findAll()) {
            if(prestamo.getEstado() == Estado.ACTIVO && prestamo.getFechaDevolucion().isBefore(LocalDate.now())) {
                prestamos.add(prestamo);
            }
        }

        return prestamos;
    }

    public Prestamo buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return prestamoRepository.findById(id).orElseThrow(() -> new PrestamoInvalidoException("El prestamo no existe."));
    }
}
