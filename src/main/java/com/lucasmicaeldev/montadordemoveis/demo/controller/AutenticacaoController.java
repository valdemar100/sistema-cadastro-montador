/**
 * CONTROLADOR DE LOGIN E AUTENTICAÇÃO
 * ===================================
 * Esta classe gerencia o sistema de login do site:
 * - Verifica se o email e senha estão corretos
 * - Permite ou nega o acesso ao sistema
 * É como um porteiro que verifica se a pessoa tem autorização para entrar.
 */
package com.lucasmicaeldev.montadordemoveis.demo.controller;

import com.lucasmicaeldev.montadordemoveis.demo.model.Usuario;
import com.lucasmicaeldev.montadordemoveis.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // Diz ao Spring: "Esta classe responde a chamadas da internet"
@RequestMapping("/api") // Todas as funções começam com "/api"
public class AutenticacaoController {

    @Autowired // Injeta automaticamente a "ferramenta" de gerenciar usuários
    private UsuarioService usuarioService;

    /**
     * FUNÇÃO: Sistema de Login
     * =======================
     * Recebe email e senha do usuário e verifica se estão corretos
     * É como mostrar sua identidade na portaria de um prédio
     */
    @PostMapping("/login") // Responde quando alguém envia dados para "/api/login"
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        try {
            // PASSO 1: Chama o serviço para verificar se email e senha existem
            // É como consultar a lista de moradores autorizados
            usuarioService.autenticar(usuario.getEmail(), usuario.getSenha());

            // PASSO 2: Se chegou até aqui, o login deu certo!
            // Retorna uma mensagem de sucesso
            return ResponseEntity.ok(Map.of(
                    "success", true, // Login bem-sucedido
                    "message", "Login realizado com sucesso!", // Mensagem amigável
                    "redirect", "/index.html" // Página para onde ir depois
            ));

        } catch (Exception e) {
            // PASSO 3: Se deu erro, significa que email ou senha estão errados
            // Retorna uma mensagem de erro
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, // Login falhou
                    "message", "Usuário ou senha incorretos." // Mensagem de erro
            ));
        }
    }
}