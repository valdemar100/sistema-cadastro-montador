/**
 * MODELO MONTADOR - REPRESENTAÇÃO DOS FUNCIONÁRIOS NO BANCO
 * =========================================================
 * Esta classe representa como os dados dos montadores são guardados no banco:
 * - Dados pessoais (nome, email, telefone, CPF)
 * - Endereço completo (para localização dos funcionários)
 * - Email único obrigatório (para identificação)
 * - Observações sobre habilidades ou ferramentas
 * É como uma "ficha de funcionário" especializada para montadores de móveis.
 */
package com.lucasmicaeldev.montadordemoveis.demo.model;

import jakarta.persistence.Column; // Para configurações específicas de colunas
import jakarta.persistence.Entity; // Diz que é uma tabela no banco
import jakarta.persistence.GeneratedValue; // ID gerado automaticamente
import jakarta.persistence.GenerationType; // Tipo de geração do ID
import jakarta.persistence.Id; // Campo que é a chave primária
import jakarta.persistence.Table; // Para especificar nome da tabela

import lombok.Data; // Cria getters/setters automaticamente
import lombok.NoArgsConstructor; // Cria construtor vazio
import lombok.AllArgsConstructor; // Cria construtor com todos os campos

@Entity // Diz ao JPA: "Esta classe vira uma tabela no banco de dados"
@Table(name = "montador") // Nome específico da tabela no banco
@Data // Lombok cria automaticamente getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok cria construtor vazio: new Montador()
@AllArgsConstructor // Lombok cria construtor com todos os campos
public class Montador {

    // CHAVE PRIMÁRIA (ID único de cada montador)
    @Id // Define como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID gerado automaticamente (1, 2, 3...)
    private Long id;

    // DADOS PESSOAIS DO MONTADOR
    private String nomeCompleto; // Nome completo do funcionário montador

    @Column(nullable = false, unique = true) // Email obrigatório e único no sistema
    private String email; // Email único para identificação

    private String telefone; // Telefone para contato
    private String cpf; // CPF do funcionário

    // ENDEREÇO COMPLETO DO MONTADOR
    // (Importante para saber onde o funcionário mora e calcular distâncias para
    // trabalhos)
    private String rua; // Nome da rua onde mora
    private String numero; // Número da casa/apartamento
    private String bairro; // Bairro onde mora
    private String cidadeEstado; // Cidade e estado (Ex: "Santos - SP")

    // INFORMAÇÕES PROFISSIONAIS
    private String observacoes; // Campo para anotações sobre:
                                // - Ferramentas que possui
                                // - Especialidades (ex: "móveis planejados")
                                // - Disponibilidade de horários
                                // - Qualquer informação relevante para o trabalho
}