/**
 * CONTROLADOR DE ORDENS DE SERVIÇO - SISTEMA CRUD
 * ===============================================
 * Esta classe gerencia todas as operações com ordens de serviço:
 * - Criar novas ordens de serviço
 * - Listar todas as ordens de serviço
 * - Buscar uma ordem específica
 * - Atualizar dados de uma ordem
 * - Deletar uma ordem
 * É como um gerente de operações que controla todos os trabalhos de montagem.
 */
package com.lucasmicaeldev.montadordemoveis.demo.controller;

import com.lucasmicaeldev.montadordemoveis.demo.dto.OrdemServicoResponseDTO; // DTO
import com.lucasmicaeldev.montadordemoveis.demo.model.OrdemServico;
import com.lucasmicaeldev.montadordemoveis.demo.service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Diz ao Spring: "Esta classe responde a chamadas da internet"
@RequestMapping("api/ordens") // Todas as funções começam com "/api/ordens"
public class OrdemServicoController {

    @Autowired // Injeta automaticamente a "ferramenta" de gerenciar ordens de serviço
    private OrdemServicoService osService;

    /**
     * FUNÇÃO: Listar todas as ordens de serviço (READ - parte do CRUD)
     * ==============================================================
     * Retorna uma lista com todas as ordens cadastradas, incluindo nomes dos
     * clientes e montadores
     * É como consultar a agenda de trabalhos da empresa
     */
    @GetMapping // Responde quando alguém faz uma consulta GET para "/api/ordens"
    public ResponseEntity<List<OrdemServicoResponseDTO>> listarOrdens() {
        // Chama o serviço para buscar todas as ordens com informações completas
        // (DTO = Data Transfer Object - formato especial que inclui nomes além dos IDs)
        List<OrdemServicoResponseDTO> ordens = osService.listarTodosDTOs();
        return ResponseEntity.ok(ordens);
    }

    /**
     * FUNÇÃO: Buscar uma ordem específica (READ - parte do CRUD)
     * =========================================================
     * Encontra uma ordem pelo seu ID (número identificador)
     * É como procurar um trabalho específico na agenda
     */
    @GetMapping("/{id}") // Responde para "/api/ordens/123" (onde 123 é o ID)
    public OrdemServico buscarPorId(@PathVariable Long id) {
        // Chama o serviço para encontrar a ordem com esse ID
        return osService.buscarPorId(id);
    }

    /**
     * FUNÇÃO: Criar nova ordem de serviço (CREATE - parte do CRUD)
     * ===========================================================
     * Cadastra uma nova ordem de serviço no sistema
     * É como agendar um novo trabalho de montagem
     */
    @PostMapping // Responde quando alguém envia dados para criar uma ordem
    public ResponseEntity<OrdemServico> criarOrdemServico(@RequestBody OrdemServico os) {
        // Chama o serviço para salvar a nova ordem no banco
        OrdemServico novaOs = osService.criar(os);

        // Retorna a ordem criada com status 201 (Criado com sucesso)
        return new ResponseEntity<>(novaOs, HttpStatus.CREATED);
    }

    /**
     * FUNÇÃO: Atualizar dados de uma ordem (UPDATE - parte do CRUD)
     * ============================================================
     * Modifica as informações de uma ordem já existente
     * É como alterar detalhes de um trabalho já agendado
     */
    @PutMapping("/{id}") // Responde para atualização em "/api/ordens/123"
    public ResponseEntity<OrdemServico> atualizarOrdemServico(@PathVariable Long id,
            @RequestBody OrdemServico osDetalhes) {
        // Chama o serviço para atualizar a ordem com os novos dados
        OrdemServico osAtualizada = osService.atualizar(id, osDetalhes);

        // Retorna a ordem atualizada com status 200 (OK)
        return ResponseEntity.ok(osAtualizada);
    }

    /**
     * FUNÇÃO: Deletar uma ordem de serviço (DELETE - parte do CRUD)
     * ============================================================
     * Remove uma ordem do sistema permanentemente
     * É como cancelar um trabalho de montagem
     */
    @DeleteMapping("/{id}") // Responde para exclusão em "/api/ordens/123"
    public ResponseEntity<Void> deletarOrdemServico(@PathVariable Long id) {
        // Chama o serviço para remover a ordem do banco
        osService.deletar(id);

        // Retorna status 204 (Removido com sucesso, sem conteúdo)
        return ResponseEntity.noContent().build();
    }
}