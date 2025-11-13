package com.lucasmicaeldev.montadordemoveis.demo.controller;

import com.lucasmicaeldev.montadordemoveis.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;


@RestController
@RequestMapping("/api/dashboard") 
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // Endpoint para buscar todas as métricas do dashboard
    @GetMapping
    public ResponseEntity<Map<String, Long>> getMetricas() {
        Map<String, Long> metricas = dashboardService.obterMetricas();
        return ResponseEntity.ok(metricas);
    }
}