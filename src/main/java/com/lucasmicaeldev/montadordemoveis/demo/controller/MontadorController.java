/**
 * CONTROLADOR DE MONTADORES - SISTEMA CRUD
 * ========================================
 * Esta classe gerencia todas as operações com montadores de móveis:
 * - Criar novos montadores
 * - Listar todos os montadores
 * - Buscar um montador específico
 * - Atualizar dados de um montador
 * - Deletar um montador
 * É como um gerente de RH que cuida do cadastro de todos os funcionários montadores.
 */
package com.lucasmicaeldev.montadordemoveis.demo.controller;

import com.lucasmicaeldev.montadordemoveis.demo.model.Montador;
import com.lucasmicaeldev.montadordemoveis.demo.service.MontadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Diz ao Spring: "Esta classe responde a chamadas da internet"
@RequestMapping("api/montador") // Todas as funções começam com "/api/montador"
public class MontadorController {

    @Autowired // Injeta automaticamente a "ferramenta" de gerenciar montadores
    private MontadorService montadorService;

    /**
     * FUNÇÃO: Listar todos os montadores (READ - parte do CRUD)
     * =======================================================
     * Retorna uma lista com todos os montadores cadastrados
     * É como consultar a lista de funcionários da empresa
     */
    @GetMapping // Responde quando alguém faz uma consulta GET para "/api/montador"
    public List<Montador> listarTodos() {
        // Chama o serviço para buscar todos os montadores do banco
        return montadorService.listarTodos();
    }

    /**
     * FUNÇÃO: Buscar um montador específico (READ - parte do CRUD)
     * ===========================================================
     * Encontra um montador pelo seu ID (número identificador)
     * É como procurar um funcionário específico na lista de RH
     */
    @GetMapping("/{id}") // Responde para "/api/montador/123" (onde 123 é o ID)
    public Montador buscarPorId(@PathVariable Long id) {
        // Chama o serviço para encontrar o montador com esse ID
        return montadorService.buscarPorId(id);
    }

    /**
     * FUNÇÃO: Criar novo montador (CREATE - parte do CRUD)
     * ===================================================
     * Cadastra um novo montador no sistema
     * É como contratar um novo funcionário montador
     */
    @PostMapping // Responde quando alguém envia dados para criar um montador
    public ResponseEntity<Montador> criarMontador(@RequestBody Montador montador) {
        // Chama o serviço para salvar o novo montador no banco
        Montador novoMontador = montadorService.criar(montador);

        // Retorna o montador criado com status 201 (Criado com sucesso)
        return new ResponseEntity<>(novoMontador, HttpStatus.CREATED);
    }

    /**
     * FUNÇÃO: Atualizar dados de um montador (UPDATE - parte do CRUD)
     * ==============================================================
     * Modifica as informações de um montador já existente
     * É como atualizar a ficha de um funcionário no RH
     */
    @PutMapping("/{id}") // Responde para atualização em "/api/montador/123"
    public ResponseEntity<Montador> atualizarMontador(@PathVariable Long id, @RequestBody Montador montadorDetalhes) {
        // Chama o serviço para atualizar o montador com os novos dados
        Montador montadorAtualizado = montadorService.atualizar(id, montadorDetalhes);

        // Retorna o montador atualizado com status 200 (OK)
        return ResponseEntity.ok(montadorAtualizado);
    }

    /**
     * FUNÇÃO: Deletar um montador (DELETE - parte do CRUD)
     * ===================================================
     * Remove um montador do sistema permanentemente
     * É como demitir um funcionário e remover da folha de pagamento
     */
    @DeleteMapping("/{id}") // Responde para exclusão em "/api/montador/123"
    public ResponseEntity<Void> deletarMontador(@PathVariable Long id) {
        // Chama o serviço para remover o montador do banco
        montadorService.deletar(id);

        // Retorna status 204 (Removido com sucesso, sem conteúdo)
        return ResponseEntity.noContent().build();
    }
}