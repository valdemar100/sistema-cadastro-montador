package com.lucasmicaeldev.montadordemoveis.demo.repository;

import com.lucasmicaeldev.montadordemoveis.demo.model.Montador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MontadorRepository extends JpaRepository<Montador, Long> {
    // Método para validação de email (como você fez no Usuario)
    Optional<Montador> findByEmail(String email);
}