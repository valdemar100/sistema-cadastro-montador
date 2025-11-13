package com.lucasmicaeldev.montadordemoveis.demo.dto; 

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Usando Lombok para simplificar o código (sem necessidade de DTOs de CRUD, mas útil para resposta)
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    // Nomes das variáveis ajustados para os cards do seu front-end
    private long totalUsuarios;      // Cliente
    private long montadoresAtivos;   // Montador
    private long montagensCriadas;   // OrdemServico total
    private long montagensPendentes; // OrdemServico com status "PENDENTE"
    
    // Você pode adicionar uma lista das próximas ordens aqui, mas focaremos nas contagens por enquanto.
}