/**
 * ENUM STATUS DA ORDEM DE SERVIÇO - ESTADOS POSSÍVEIS
 * ===================================================
 * Este enum define todos os status possíveis que uma ordem de serviço pode ter:
 * - PENDENTE: Ordem criada, aguardando agendamento
 * - AGENDADA: Data e montador definidos
 * - EM_ANDAMENTO: Montador está executando o trabalho
 * - CONCLUIDA: Trabalho finalizado com sucesso
 * - CANCELADA: Ordem cancelada por algum motivo
 * 
 * É como os "estados" que um trabalho pode ter durante seu ciclo de vida.
 */
package com.lucasmicaeldev.montadordemoveis.demo.model;

public enum StatusOS {
    // DEFINIÇÃO DOS STATUS POSSÍVEIS
    PENDENTE("Pendente"), // Ordem criada, aguardando organização
    AGENDADA("Agendada"), // Data marcada, montador definido
    EM_ANDAMENTO("Em Andamento"), // Montador executando o trabalho
    CONCLUIDA("Concluída"), // Trabalho finalizado com sucesso
    CANCELADA("Cancelada"); // Ordem cancelada (cliente desistiu, etc.)

    // CAMPO INTERNO: descrição legível para humanos
    private final String descricao;

    /**
     * CONSTRUTOR: Define a descrição de cada status
     * ============================================
     * Cada status tem uma descrição amigável que pode ser mostrada na tela
     */
    StatusOS(String descricao) {
        this.descricao = descricao;
    }

    /**
     * FUNÇÃO: Obter descrição legível do status
     * ========================================
     * Retorna a descrição em português do status
     * Ex: StatusOS.PENDENTE.getDescricao() retorna "Pendente"
     */
    public String getDescricao() {
        return descricao;
    }
}