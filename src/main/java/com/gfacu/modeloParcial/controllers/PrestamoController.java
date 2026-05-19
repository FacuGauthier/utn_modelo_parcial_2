package com.gfacu.modeloParcial.controllers;

import com.gfacu.modeloParcial.models.Prestamo;
import com.gfacu.modeloParcial.services.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {
    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<Prestamo> listarPrestamos() {
        return prestamoService.listarPrestamos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prestamo> buscarPorId(@PathVariable Long id) {
        Prestamo prestamo = prestamoService.buscarPorId(id);

        return ResponseEntity.ok().body(prestamo);
    }

    @GetMapping("/usuario/{id}")
    public List<Prestamo> listarPrestamosPorUsuario(@PathVariable Long id) {
        return prestamoService.listarPorUsuario(id);
    }

    @GetMapping("/vencidos")
    public List<Prestamo> listarVencidosPorUsuario() {
        return prestamoService.listarPrestamosVencidos();
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Prestamo prestamo) {
        Prestamo prestamoCreado = prestamoService.registrarPrestamo(prestamo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prestamoCreado);
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Prestamo> finalizarPrestamo(@PathVariable Long id) {
        prestamoService.finalizarPrestamo(id);
        return ResponseEntity.noContent().build();
    }
}
