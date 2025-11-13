package com.lucasmicaeldev.montadordemoveis.demo.repository;

import com.lucasmicaeldev.montadordemoveis.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Adicione esta importação (boa prática)
import java.util.Optional;

@Repository 
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Data JPA reconhece o padrão "findByEmail" e gera a query.
    Optional<Usuario> findByEmail(String email);
}