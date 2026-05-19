package com.gfacu.modeloParcial.services;

import com.gfacu.modeloParcial.exceptions.PrestamoInvalidoException;
import com.gfacu.modeloParcial.models.Estado;
import com.gfacu.modeloParcial.models.Libro;
import com.gfacu.modeloParcial.models.Prestamo;
import com.gfacu.modeloParcial.repositories.LibroRepository;
import com.gfacu.modeloParcial.repositories.PrestamoRepository;
import com.gfacu.modeloParcial.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
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

    public boolean finalizarPrestamo(Long id) {
        Prestamo prestamo = buscarPorId(id);

        prestamo.setEstado(Estado.FINALIZADO);

        Libro libro = prestamo.getLibro();
        libro.setCantidadDisponible(libro.getCantidadDisponible()+1);
        try{
            libroRepository.save(libro);
            prestamoRepository.save(prestamo);
            return true;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error en la integridad de datos.");
        }
    }

    public List<Prestamo> listarPorUsuario(Long idUsuario) {

    }

    public Prestamo buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return prestamoRepository.findById(id).orElseThrow(() -> new PrestamoInvalidoException("El prestamo no existe."));
    }
}
