/**
 * SERVIÇO DE MONTADORES - REGRAS DE NEGÓCIO
 * =========================================
 * Esta classe contém toda a lógica para gerenciar montadores de móveis:
 * - Validações de dados
 * - Comunicação com o banco de dados
 * - Tratamento de erros
 * - Operações CRUD específicas para montadores
 * É como o "gerente de RH" que cuida dos funcionários montadores.
 */
package com.lucasmicaeldev.montadordemoveis.demo.service;

import com.lucasmicaeldev.montadordemoveis.demo.model.Montador;
import com.lucasmicaeldev.montadordemoveis.demo.repository.MontadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service // Diz ao Spring: "Esta classe contém lógica de negócio"
public class MontadorService {

    @Autowired // Injeta automaticamente a "ferramenta" que acessa o banco
    private MontadorRepository montadorRepository;

    /**
     * FUNÇÃO: Listar todos os montadores ordenados por ID
     * =================================================
     * Busca todos os montadores do banco, organizados por ordem de cadastro
     * É como ver a lista de funcionários da empresa
     */
    public List<Montador> listarTodos() {
        // Busca todos e ordena por ID em ordem crescente (ASC = Ascending)
        return montadorRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    /**
     * FUNÇÃO: Buscar um montador específico pelo ID
     * ============================================
     * Encontra um montador usando seu número identificador
     * É como procurar a ficha de um funcionário específico
     */
    public Montador buscarPorId(Long id) {
        // Validação: ID não pode ser nulo
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo.");
        }

        // Busca no banco e retorna erro 404 se não encontrar
        return montadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Montador não encontrado."));
    }

    /**
     * FUNÇÃO: Criar novo montador no sistema
     * =====================================
     * Cadastra um novo montador com validações básicas
     * É como contratar um novo funcionário montador
     */
    public Montador criar(Montador montador) {
        // Validação básica: montador não pode ser nulo
        if (montador == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Montador não pode ser nulo.");
        }

        // Salva o montador no banco de dados e retorna o montador criado
        return montadorRepository.save(montador);
    }

    /**
     * FUNÇÃO: Atualizar dados de um montador existente
     * ===============================================
     * Modifica as informações de um montador já cadastrado
     * É como atualizar a ficha de um funcionário
     */
    public Montador atualizar(Long id, Montador montadorDetalhes) {
        // Primeiro busca o montador existente (se não existir, dará erro)
        Montador montador = buscarPorId(id);

        // Atualiza os campos principais do montador
        montador.setNomeCompleto(montadorDetalhes.getNomeCompleto());
        montador.setEmail(montadorDetalhes.getEmail());
        montador.setTelefone(montadorDetalhes.getTelefone());
        montador.setCpf(montadorDetalhes.getCpf());

        // Atualiza campos de endereço do montador
        montador.setRua(montadorDetalhes.getRua());
        montador.setNumero(montadorDetalhes.getNumero());
        montador.setBairro(montadorDetalhes.getBairro());
        montador.setCidadeEstado(montadorDetalhes.getCidadeEstado());
        montador.setObservacoes(montadorDetalhes.getObservacoes());

        // Salva as alterações no banco e retorna o montador atualizado
        return montadorRepository.save(montador);
    }

    /**
     * FUNÇÃO: Deletar um montador do sistema
     * =====================================
     * Remove permanentemente um montador do banco de dados
     * É como demitir um funcionário da empresa
     */
    public void deletar(Long id) {
        // Busca o montador (se não existir, buscarPorId já dará erro)
        Montador montador = buscarPorId(id);

        // Se existe, remove do banco
        if (montador != null) {
            montadorRepository.delete(montador);
        }
    }
}