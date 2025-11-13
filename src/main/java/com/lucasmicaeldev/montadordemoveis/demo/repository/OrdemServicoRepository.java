package com.lucasmicaeldev.montadordemoveis.demo.repository;

import com.lucasmicaeldev.montadordemoveis.demo.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Importar
import org.springframework.stereotype.Repository;

// ...

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    
    // Método anterior (que causa erro na inicialização/runtime)
    // long countByStatus(String status); 
    
    // ✅ NOVO MÉTODO (USANDO @Query) - Muito mais robusto para Enums/PostgreSQL
    @Query("SELECT COUNT(o) FROM OrdemServico o WHERE o.status = :status")
    long contarPorStatus(@org.springframework.data.repository.query.Param("status") com.lucasmicaeldev.montadordemoveis.demo.model.StatusOS status);
    
    // ... os outros métodos (findTop5, etc.) ...
}