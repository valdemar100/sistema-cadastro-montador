/**
 * MODELO USUARIO - REPRESENTAÇÃO DOS DADOS NO BANCO
 * =================================================
 * Esta classe representa como os dados de usuários são guardados no banco:
 * - Dados pessoais (nome, email, telefone, CPF)
 * - Endereço completo (rua, número, bairro, cidade)
 * - Informações de acesso (senha para funcionários)
 * - Observações adicionais
 * É como uma "ficha de cadastro" digital de clientes e funcionários.
 */
package com.lucasmicaeldev.montadordemoveis.demo.model;

import jakarta.persistence.Entity; // Diz que é uma tabela no banco
import jakarta.persistence.GeneratedValue; // ID gerado automaticamente
import jakarta.persistence.GenerationType; // Tipo de geração do ID
import jakarta.persistence.Id; // Campo que é a chave primária
import lombok.AllArgsConstructor; // Cria construtor com todos os campos
import lombok.Data; // Cria getters/setters automaticamente
import lombok.NoArgsConstructor; // Cria construtor vazio

@Entity // Diz ao JPA: "Esta classe vira uma tabela no banco de dados"
@Data // Lombok cria automaticamente getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok cria construtor vazio: new Usuario()
@AllArgsConstructor // Lombok cria construtor com todos os campos
public class Usuario {

    // CHAVE PRIMÁRIA (ID único de cada usuário)
    @Id // Define como chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID gerado automaticamente (1, 2, 3...)
    private Long id;

    // DADOS PESSOAIS DO USUÁRIO
    private String nomeCompleto; // Nome completo do cliente/funcionário
    private String email; // Email único para contato e login
    private String senha; // Senha para funcionários (clientes podem não ter)
    private String telefone; // Telefone para contato
    private String cpf; // CPF para identificação
    private String dataNascimento; // Data de nascimento (formato texto simplificado)

    // ENDEREÇO COMPLETO DO USUÁRIO
    private String rua; // Nome da rua
    private String numero; // Número da casa/apartamento
    private String bairro; // Bairro onde mora
    private String cidadeEstado; // Cidade e estado (Ex: "São Paulo - SP")

    // INFORMAÇÕES ADICIONAIS
    private String observacoes; // Campo livre para anotações importantes
}
