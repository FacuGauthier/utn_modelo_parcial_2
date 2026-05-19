package com.gfacu.modeloParcial.repositories;

import com.gfacu.modeloParcial.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo,Long> {
    boolean existsByUsuarioId(Long idUsuario);
    boolean existsByLibroId(Long idLibro);
    List<Prestamo> findByUsuarioId(Long idUsuario);
}
