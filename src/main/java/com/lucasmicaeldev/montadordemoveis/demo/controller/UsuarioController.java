/**
 * CONTROLADOR DE USUÁRIOS/CLIENTES - SISTEMA CRUD
 * ==============================================
 * Esta classe gerencia todas as operações com usuários (clientes):
 * - Criar novos clientes
 * - Listar todos os clientes
 * - Buscar um cliente específico
 * - Atualizar dados de um cliente
 * - Deletar um cliente
 * É como um gerente de cadastro que cuida de todas as informações dos clientes.
 */
package com.lucasmicaeldev.montadordemoveis.demo.controller;

import com.lucasmicaeldev.montadordemoveis.demo.model.Usuario;
import com.lucasmicaeldev.montadordemoveis.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Diz ao Spring: "Esta classe responde a chamadas da internet"
@RequestMapping("api/usuarios") // Todas as funções começam com "/api/usuarios"
public class UsuarioController {

    @Autowired // Injeta automaticamente a "ferramenta" de gerenciar usuários
    private UsuarioService usuarioService;

    /**
     * FUNÇÃO: Listar todos os clientes (READ - parte do CRUD)
     * =====================================================
     * Retorna uma lista com todos os clientes cadastrados
     * É como abrir uma lista telefônica e ver todos os nomes
     */
    @GetMapping // Responde quando alguém faz uma consulta GET para "/api/usuarios"
    public List<Usuario> listarTodos() {
        // Chama o serviço para buscar todos os usuários do banco
        return usuarioService.listarTodos();
    }

    /**
     * FUNÇÃO: Buscar um cliente específico (READ - parte do CRUD)
     * =========================================================
     * Encontra um cliente pelo seu ID (número identificador)
     * É como procurar uma pessoa específica na lista telefônica
     */
    @GetMapping("/{id}") // Responde para "/api/usuarios/123" (onde 123 é o ID)
    public Usuario buscarPorId(@PathVariable Long id) {
        // Chama o serviço para encontrar o usuário com esse ID
        return usuarioService.buscarPorId(id);
    }

    /**
     * FUNÇÃO: Criar novo cliente (CREATE - parte do CRUD)
     * ==================================================
     * Cadastra um novo cliente no sistema
     * É como adicionar um novo nome na lista telefônica
     */
    @PostMapping // Responde quando alguém envia dados para criar um usuário
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        //
        // OBSERVAÇÃO: O Spring automaticamente converte os dados enviados
        // do formulário web para um objeto Usuario que podemos usar
        //

        // Chama o serviço para salvar o novo usuário no banco
        Usuario novoUsuario = usuarioService.criar(usuario);

        // Retorna o usuário criado com status 201 (Criado com sucesso)
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    /**
     * FUNÇÃO: Atualizar dados de um cliente (UPDATE - parte do CRUD)
     * =============================================================
     * Modifica as informações de um cliente já existente
     * É como corrigir informações na lista telefônica
     */
    @PutMapping("/{id}") // Responde para atualização em "/api/usuarios/123"
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetalhes) {
        // Chama o serviço para atualizar o usuário com os novos dados
        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuarioDetalhes);

        // Retorna o usuário atualizado com status 200 (OK)
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * FUNÇÃO: Deletar um cliente (DELETE - parte do CRUD)
     * ==================================================
     * Remove um cliente do sistema permanentemente
     * É como apagar um nome da lista telefônica
     */
    @DeleteMapping("/{id}") // Responde para exclusão em "/api/usuarios/123"
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        // Chama o serviço para remover o usuário do banco
        usuarioService.deletar(id);

        // Retorna status 204 (Removido com sucesso, sem conteúdo)
        return ResponseEntity.noContent().build();
    }
}
