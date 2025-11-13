/**
 * CONTROLADOR DE SAÚDE E NAVEGAÇÃO
 * ================================
 * Esta classe gerencia a página inicial e verifica se o sistema está funcionando.
 * É como um recepcionista que direciona as pessoas e verifica se tudo está OK.
 */
package com.lucasmicaeldev.montadordemoveis.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@Controller // Diz ao Spring: "Esta classe controla páginas web"
public class HealthController {

    /**
     * FUNÇÃO: Página inicial do sistema
     * ================================
     * Quando alguém acessa a página principal, redireciona para o login
     * É como ter uma porta que automaticamente te leva para a recepção
     */
    @GetMapping("/") // Escuta quando alguém acessa a página inicial "/"
    public String home() {
        // Redireciona automaticamente para a página de login
        return "redirect:/login.html";
    }

    /**
     * FUNÇÃO: Verificar saúde do sistema (API)
     * =======================================
     * Retorna informações sobre se o sistema está funcionando
     * É como um check-up médico do sistema
     */
    @GetMapping("/api/health") // Escuta chamadas para "/api/health"
    @org.springframework.web.bind.annotation.ResponseBody // Retorna dados JSON
    public Map<String, String> health() {
        // Retorna um "relatório de saúde" do sistema
        return Map.of(
                "status", "UP", // Sistema funcionando
                "service", "Sistema de Cadastro", // Nome do serviço
                "database", "H2 File Database", // Tipo de banco
                "location", "./Documentos (arquivo local)"); // Onde está o banco
    }

    /**
     * FUNÇÃO: Status detalhado do sistema (API)
     * ========================================
     * Retorna informações completas sobre o sistema e suas funcionalidades
     * É como um painel de controle que mostra tudo que está disponível
     */
    @GetMapping("/api/status") // Escuta chamadas para "/api/status"
    @org.springframework.web.bind.annotation.ResponseBody // Retorna dados JSON
    public Map<String, Object> status() {
        // Retorna informações detalhadas do sistema
        return Map.of(
                "status", "OK", // Tudo funcionando
                "message", "Sistema de Cadastro - Montador de Móveis está funcionando!", // Mensagem amigável
                "timestamp", java.time.Instant.now().toString(), // Hora atual
                "version", "1.0.0", // Versão do sistema
                "endpoints", Map.of( // Lista de páginas disponíveis
                        "health", "/api/health", // Página de saúde
                        "login", "/login.html", // Página de login
                        "dashboard", "/index.html")); // Página principal
    }
}
