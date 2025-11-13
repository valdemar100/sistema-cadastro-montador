package com.lucasmicaeldev.montadordemoveis.demo.service;

import com.lucasmicaeldev.montadordemoveis.demo.model.StatusOS;
import com.lucasmicaeldev.montadordemoveis.demo.repository.MontadorRepository;
import com.lucasmicaeldev.montadordemoveis.demo.repository.OrdemServicoRepository;
import com.lucasmicaeldev.montadordemoveis.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MontadorRepository montadorRepository;

    @Autowired
    private OrdemServicoRepository osRepository;

    public Map<String, Long> obterMetricas() {
        Map<String, Long> metricas = new HashMap<>();

        // Contagem total de clientes (usuários comuns)
        metricas.put("clientes", usuarioRepository.count());

        // Contagem total de montadores
        metricas.put("montadores", montadorRepository.count());

        // Contagem total de ordens de serviço
        metricas.put("ordensTotais", osRepository.count());

        // Contagem de ordens pendentes
        metricas.put("osPendentes", osRepository.contarPorStatus(StatusOS.PENDENTE));

        // Contagem de ordens agendadas
        metricas.put("osAgendadas", osRepository.contarPorStatus(StatusOS.AGENDADA));

        return metricas;
    }
}