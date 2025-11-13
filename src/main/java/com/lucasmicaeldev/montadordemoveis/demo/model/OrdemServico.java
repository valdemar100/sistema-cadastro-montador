/**
 * MODELO ORDEM DE SERVIÇO - REPRESENTAÇÃO DOS TRABALHOS NO BANCO
 * ==============================================================
 * Esta classe representa como os dados das ordens de serviço são guardados no banco:
 * - Número único da ordem (OS-1, OS-2, etc.)
 * - IDs do cliente e montador responsáveis
 * - Endereço onde será realizado o serviço
 * - Data agendada para a montagem
 * - Status atual do trabalho (PENDENTE, AGENDADA, CONCLUÍDA)
 * - Descrição detalhada e tipo de móvel
 * É como uma "ordem de trabalho" completa para cada montagem.
 */
package com.lucasmicaeldev.montadordemoveis.demo.model;

import jakarta.persistence.Entity; // Diz que é uma tabela no banco
import jakarta.persistence.EnumType; // Para usar enum como string no banco
import jakarta.persistence.Enumerated; // Para mapear enum no banco
import jakarta.persistence.GeneratedValue; // ID gerado automaticamente
import jakarta.persistence.GenerationType; // Tipo de geração do ID
import jakarta.persistence.Id; // Campo que é a chave primária
import jakarta.persistence.Table; // Para especificar nome da tabela
import lombok.AllArgsConstructor; // Cria construtor com todos os campos
import lombok.Data; // Cria getters/setters automaticamente
import lombok.NoArgsConstructor; // Cria construtor vazio
import java.time.LocalDate; // Para datas mais robustas

@Entity // Diz ao JPA: "Esta classe vira uma tabela no banco de dados"
@Data // Lombok cria automaticamente getters, setters, toString, equals, hashCode
@Table(name = "ordem_servico") // Nome específico da tabela no banco
@NoArgsConstructor // Lombok cria construtor vazio: new OrdemServico()
@AllArgsConstructor // Lombok cria construtor com todos os campos
public class OrdemServico {

    // CHAVE PRIMÁRIA (ID único de cada ordem de serviço)
    @Id // Define como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID gerado automaticamente (1, 2, 3...)
    private Long id;

    // IDENTIFICAÇÃO DA ORDEM
    private String numeroOs; // Número único da OS (ex: "OS-001", "OS-002")

    // RELACIONAMENTOS (usando IDs simples para facilitar)
    private Long clienteId; // ID do cliente que solicitou o serviço
    private Long montadorId; // ID do montador responsável pelo trabalho

    // INFORMAÇÕES DO SERVIÇO
    private String enderecoServico; // Endereço onde será realizada a montagem
    private LocalDate dataAgendamento; // Data agendada para o serviço (formato robusto)

    // STATUS DA ORDEM (usando enum para garantir valores válidos)
    @Enumerated(EnumType.STRING) // Salva o enum como texto no banco (ex: "PENDENTE")
    private StatusOS status; // Status atual: PENDENTE, AGENDADA, EM_ANDAMENTO, CONCLUIDA

    // DETALHES DO TRABALHO
    private String descricao; // Descrição detalhada do que deve ser montado
    private String tipoDeMovel; // Tipo de móvel (ex: "Guarda-roupa", "Cozinha planejada")
}