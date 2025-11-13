package com.lucasmicaeldev.montadordemoveis.demo.dto;

import com.lucasmicaeldev.montadordemoveis.demo.model.StatusOS;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponseDTO {
    
    private Long id;
    private String numeroOs;
    private Long clienteId;
    private Long montadorId;
    private String enderecoServico;
    private LocalDate dataAgendamento;
    private StatusOS status;
    private String descricao;
    private String tipoDeMovel;

    // NOVOS CAMPOS PARA EXIBIÇÃO NO FRONTEND
    private String clienteNome; // Mapped to: os.clienteNome no JS
    private String montadorNome; // Mapped to: os.montadorNome no JS
}