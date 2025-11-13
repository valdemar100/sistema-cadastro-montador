/**
 * SERVIÇO DE ORDENS DE SERVIÇO - REGRAS DE NEGÓCIO
 * ================================================
 * Esta classe contém toda a lógica para gerenciar ordens de serviço:
 * - Validações de dados
 * - Geração automática de números de OS
 * - Busca de nomes de clientes e montadores
 * - Conversão de dados para o frontend (DTOs)
 * - Controle de status das ordens
 * É como o "gerente de operações" que organiza todos os trabalhos.
 */
package com.lucasmicaeldev.montadordemoveis.demo.service;

import com.lucasmicaeldev.montadordemoveis.demo.dto.OrdemServicoResponseDTO;
import com.lucasmicaeldev.montadordemoveis.demo.model.OrdemServico;
import com.lucasmicaeldev.montadordemoveis.demo.model.StatusOS; // Importação do Enum de status
import com.lucasmicaeldev.montadordemoveis.demo.repository.OrdemServicoRepository;
import com.lucasmicaeldev.montadordemoveis.demo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort; // Para ordenação dos resultados
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service // Diz ao Spring: "Esta classe contém lógica de negócio"
public class OrdemServicoService {

    @Autowired // Injeta automaticamente a "ferramenta" que acessa ordens de serviço
    private OrdemServicoRepository osRepository;

    @Autowired // Injeta automaticamente a "ferramenta" que acessa usuários (para buscar nomes)
    private UsuarioRepository usuarioRepository;

    /**
     * FUNÇÃO: Listar todas as ordens de serviço ordenadas
     * =================================================
     * Busca todas as ordens do banco, ordenadas da mais recente para a mais antiga
     * É como ver a agenda de trabalhos organizada por data de criação
     */
    public List<OrdemServico> listarTodos() {
        // Ordena por ID em ordem decrescente (DESC = mais recente primeiro)
        return osRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * FUNÇÃO: Buscar uma ordem específica pelo ID
     * ==========================================
     * Encontra uma ordem usando seu número identificador
     * É como procurar um trabalho específico na agenda
     */
    public OrdemServico buscarPorId(Long id) {
        // Validação: ID não pode ser nulo
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo.");
        }

        // Busca no banco e retorna erro 404 se não encontrar
        return osRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de Serviço não encontrada."));
    }

    /**
     * FUNÇÃO: Criar nova ordem de serviço
     * ==================================
     * Cadastra uma nova ordem com configurações automáticas
     * É como agendar um novo trabalho de montagem
     */
    public OrdemServico criar(OrdemServico os) {
        // CONFIGURAÇÃO AUTOMÁTICA 1: Define status inicial como PENDENTE
        if (os.getStatus() == null) {
            os.setStatus(StatusOS.PENDENTE); // Usa o enum StatusOS
        }

        // CONFIGURAÇÃO AUTOMÁTICA 2: Gera número da OS automaticamente
        if (os.getNumeroOs() == null || os.getNumeroOs().isEmpty()) {
            // Conta quantas ordens existem e cria o próximo número
            // Formato: "OS-1", "OS-2", "OS-3", etc.
            os.setNumeroOs("OS-" + (osRepository.count() + 1));
        }

        // Salva a ordem no banco de dados e retorna a ordem criada
        return osRepository.save(os);
    }

    /**
     * FUNÇÃO: Atualizar dados de uma ordem existente
     * =============================================
     * Modifica as informações de uma ordem já cadastrada
     * É como alterar detalhes de um trabalho agendado
     */
    public OrdemServico atualizar(Long id, OrdemServico osDetalhes) {
        // Primeiro busca a ordem existente (se não existir, dará erro)
        OrdemServico os = buscarPorId(id);

        // Atualiza todos os campos com os novos dados
        os.setNumeroOs(osDetalhes.getNumeroOs());
        os.setClienteId(osDetalhes.getClienteId());
        os.setMontadorId(osDetalhes.getMontadorId());
        os.setEnderecoServico(osDetalhes.getEnderecoServico());
        os.setDataAgendamento(osDetalhes.getDataAgendamento());
        os.setStatus(osDetalhes.getStatus()); // Usa o enum StatusOS
        os.setDescricao(osDetalhes.getDescricao());
        os.setTipoDeMovel(osDetalhes.getTipoDeMovel());

        // Salva as alterações no banco e retorna a ordem atualizada
        return osRepository.save(os);
    }

    /**
     * FUNÇÃO: Deletar uma ordem de serviço
     * ===================================
     * Remove permanentemente uma ordem do banco de dados
     * É como cancelar um trabalho agendado
     */
    public void deletar(Long id) {
        // Busca a ordem (se não existir, buscarPorId já dará erro)
        OrdemServico os = buscarPorId(id);

        // Se existe, remove do banco
        if (os != null) {
            osRepository.delete(os);
        }
    }

    /**
     * FUNÇÃO: Contar total de ordens de serviço
     * ========================================
     * Retorna o número total de ordens cadastradas
     * É como contar quantos trabalhos a empresa já teve
     */
    public long contarTodas() {
        return osRepository.count();
    }

    /**
     * FUNÇÃO AUXILIAR: Converter ordem para DTO com nomes
     * =================================================
     * Transforma uma ordem simples em uma ordem com nomes de cliente e montador
     * É como "enriquecer" a informação para facilitar a visualização
     */
    @SuppressWarnings("null")
    private OrdemServicoResponseDTO toDTO(OrdemServico os) {
        // BUSCA O NOME DO CLIENTE pelo ID
        String clienteNome = "Cliente Não Encontrado";
        if (os.getClienteId() != null) {
            clienteNome = usuarioRepository.findById(os.getClienteId())
                    .map(usuario -> usuario.getNomeCompleto()) // Pega o nome completo
                    .orElse("Cliente Não Encontrado"); // Se não encontrar, usa mensagem padrão
        }

        // BUSCA O NOME DO MONTADOR pelo ID
        String montadorNome = "Montador Não Encontrado";
        if (os.getMontadorId() != null) {
            montadorNome = usuarioRepository.findById(os.getMontadorId())
                    .map(usuario -> usuario.getNomeCompleto()) // Pega o nome completo
                    .orElse("Montador Não Encontrado"); // Se não encontrar, usa mensagem padrão
        }

        // CRIA O DTO (Data Transfer Object) com todas as informações
        OrdemServicoResponseDTO dto = new OrdemServicoResponseDTO();
        dto.setId(os.getId());
        dto.setNumeroOs(os.getNumeroOs());
        dto.setClienteId(os.getClienteId());
        dto.setMontadorId(os.getMontadorId());
        dto.setEnderecoServico(os.getEnderecoServico());
        dto.setDataAgendamento(os.getDataAgendamento());
        dto.setStatus(os.getStatus());
        dto.setDescricao(os.getDescricao());
        dto.setTipoDeMovel(os.getTipoDeMovel());

        // ADICIONA OS NOMES (isso é o que o frontend realmente quer ver!)
        dto.setClienteNome(clienteNome);
        dto.setMontadorNome(montadorNome);

        return dto;
    }

    /**
     * FUNÇÃO: Listar todas as ordens com nomes completos
     * ================================================
     * Retorna lista de ordens de serviço com nomes de clientes e montadores
     * É como ver a agenda completa com nomes ao invés de números
     */
    public List<OrdemServicoResponseDTO> listarTodosDTOs() {
        // Busca todas as ordens ordenadas por ID decrescente
        List<OrdemServico> ordens = osRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        // Converte cada ordem simples em ordem com nomes (DTO)
        // stream() = processa cada item da lista
        // map() = transforma cada item usando a função toDTO
        // collect() = junta tudo numa nova lista
        return ordens.stream()
                .map(this::toDTO) // Aplica a função toDTO em cada ordem
                .collect(Collectors.toList()); // Coleta tudo numa lista
    }
}