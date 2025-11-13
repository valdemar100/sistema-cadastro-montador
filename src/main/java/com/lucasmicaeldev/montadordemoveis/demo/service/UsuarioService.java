/**
 * SERVIÇO DE USUÁRIOS - REGRAS DE NEGÓCIO
 * =======================================
 * Esta classe contém toda a lógica de negócio para gerenciar usuários:
 * - Validações (email único, dados obrigatórios)
 * - Comunicação com o banco de dados
 * - Autenticação de login
 * - Tratamento de erros
 * É como o "cérebro" do sistema que pensa antes de agir no banco.
 */
package com.lucasmicaeldev.montadordemoveis.demo.service;

import com.lucasmicaeldev.montadordemoveis.demo.model.Usuario;
import com.lucasmicaeldev.montadordemoveis.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service // Diz ao Spring: "Esta classe contém lógica de negócio"
public class UsuarioService {

    @Autowired // Injeta automaticamente a "ferramenta" que acessa o banco
    private UsuarioRepository usuarioRepository;

    /**
     * FUNÇÃO: Listar todos os usuários ordenados por ID
     * ===============================================
     * Busca todos os usuários do banco, organizados do menor ID para o maior
     * É como ver a lista de clientes da empresa em ordem de cadastro
     */
    public List<Usuario> listarTodos() {
        // Busca todos e ordena por ID em ordem crescente (ASC = Ascending)
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    /**
     * FUNÇÃO: Buscar um usuário específico pelo ID
     * ===========================================
     * Encontra um usuário usando seu número identificador
     * É como procurar uma ficha específica no arquivo de clientes
     */
    public Usuario buscarPorId(Long id) {
        // Validação: ID não pode ser nulo
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID não pode ser nulo.");
        }

        // Busca no banco e retorna erro 404 se não encontrar
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    /**
     * FUNÇÃO: Criar novo usuário no sistema
     * ====================================
     * Cadastra um novo usuário com validações de segurança
     * É como registrar um novo cliente na empresa
     */
    public Usuario criar(Usuario usuario) {
        // TRATAMENTO ESPECIAL: Se não tem senha (cliente), define string vazia
        // Isso evita erro de "campo obrigatório" no banco de dados
        // Em sistemas reais, clientes podem ter senhas opcionais
        if (usuario.getSenha() == null) {
            usuario.setSenha(""); // Garante que não é NULL no banco
        }

        // Normaliza o email para minúsculas para garantir consistência
        if (usuario.getEmail() != null) {
            usuario.setEmail(usuario.getEmail().toLowerCase().trim());
        }

        // VALIDAÇÃO IMPORTANTE: Email deve ser único no sistema
        // Verifica se já existe outro usuário com o mesmo email
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado.");
        }

        // Salva o usuário no banco de dados e retorna o usuário criado
        return usuarioRepository.save(usuario);
    }

    /**
     * FUNÇÃO: Atualizar dados de um usuário existente
     * ==============================================
     * Modifica as informações de um usuário já cadastrado
     * É como atualizar a ficha de um cliente
     */
    public Usuario atualizar(Long id, Usuario usuarioDetalhes) {
        // Primeiro busca o usuário existente (se não existir, dará erro)
        Usuario usuario = buscarPorId(id);

        // Atualiza todos os campos com os novos dados
        usuario.setNomeCompleto(usuarioDetalhes.getNomeCompleto());

        // Normaliza o email para minúsculas
        if (usuarioDetalhes.getEmail() != null) {
            usuario.setEmail(usuarioDetalhes.getEmail().toLowerCase().trim());
        } else {
            usuario.setEmail(usuarioDetalhes.getEmail());
        }

        // TRATAMENTO ESPECIAL DA SENHA: só atualiza se foi fornecida uma nova
        if (usuarioDetalhes.getSenha() != null && !usuarioDetalhes.getSenha().isEmpty()) {
            // Em sistemas reais, aqui seria aplicada criptografia na senha
            usuario.setSenha(usuarioDetalhes.getSenha());
        }

        // Atualiza demais campos de contato e endereço
        usuario.setTelefone(usuarioDetalhes.getTelefone());
        usuario.setCpf(usuarioDetalhes.getCpf());
        usuario.setDataNascimento(usuarioDetalhes.getDataNascimento());
        usuario.setBairro(usuarioDetalhes.getBairro());
        usuario.setRua(usuarioDetalhes.getRua());
        usuario.setNumero(usuarioDetalhes.getNumero());
        usuario.setCidadeEstado(usuarioDetalhes.getCidadeEstado());
        usuario.setObservacoes(usuarioDetalhes.getObservacoes());

        // Salva as alterações no banco e retorna o usuário atualizado
        return usuarioRepository.save(usuario);
    }

    /**
     * FUNÇÃO: Deletar um usuário do sistema
     * ====================================
     * Remove permanentemente um usuário do banco de dados
     * É como remover a ficha de um cliente dos arquivos
     */
    public void deletar(Long id) {
        // Busca o usuário (se não existir, buscarPorId já dará erro)
        Usuario usuario = buscarPorId(id);

        // Se existe, remove do banco
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    /**
     * FUNÇÃO: Autenticar usuário no sistema (LOGIN)
     * ============================================
     * Verifica se email e senha estão corretos para permitir o login
     * É como verificar a identidade de alguém na portaria
     */
    public Usuario autenticar(String email, String senha) {
        // Normaliza o email para minúsculas para evitar problemas com case-sensitive
        String emailNormalizado = email != null ? email.toLowerCase().trim() : null;

        // Busca o usuário pelo email no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElse(null); // Se não encontrar, retorna null

        // VALIDAÇÕES DE SEGURANÇA:
        // 1. Usuário não existe
        // 2. Usuário não tem senha (é cliente sem acesso ao sistema)
        // 3. Senha está vazia
        if (usuario == null || usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha incorretos.");
        }

        // VERIFICAÇÃO DA SENHA (comparação simples - em produção seria criptografada)
        if (usuario.getSenha().equals(senha)) {
            return usuario; // LOGIN SUCESSO: retorna os dados do usuário
        } else {
            // LOGIN FALHOU: senha incorreta
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha incorretos.");
        }
    }
}