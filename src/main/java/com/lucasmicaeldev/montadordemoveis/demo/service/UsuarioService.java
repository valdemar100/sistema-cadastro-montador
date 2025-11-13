/**
 * SERVIÃ‡O DE USUÃRIOS - REGRAS DE NEGÃ“CIO
 * =======================================
 * Esta classe contÃ©m toda a lÃ³gica de negÃ³cio para gerenciar usuÃ¡rios:
 * - ValidaÃ§Ãµes (email Ãºnico, dados obrigatÃ³rios)
 * - ComunicaÃ§Ã£o com o banco de dados
 * - AutenticaÃ§Ã£o de login
 * - Tratamento de erros
 * Ã‰ como o "cÃ©rebro" do sistema que pensa antes de agir no banco.
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

@Service // Diz ao Spring: "Esta classe contÃ©m lÃ³gica de negÃ³cio"
public class UsuarioService {

    @Autowired // Injeta automaticamente a "ferramenta" que acessa o banco
    private UsuarioRepository usuarioRepository;

    /**
     * FUNÃ‡ÃƒO: Listar todos os usuÃ¡rios ordenados por ID
     * ===============================================
     * Busca todos os usuÃ¡rios do banco, organizados do menor ID para o maior
     * Ã‰ como ver a lista de clientes da empresa em ordem de cadastro
     */
    public List<Usuario> listarTodos() {
        // Busca todos e ordena por ID em ordem crescente (ASC = Ascending)
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    /**
     * FUNÃ‡ÃƒO: Buscar um usuÃ¡rio especÃ­fico pelo ID
     * ===========================================
     * Encontra um usuÃ¡rio usando seu nÃºmero identificador
     * Ã‰ como procurar uma ficha especÃ­fica no arquivo de clientes
     */
    public Usuario buscarPorId(Long id) {
        // ValidaÃ§Ã£o: ID nÃ£o pode ser nulo
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID nÃ£o pode ser nulo.");
        }

        // Busca no banco e retorna erro 404 se nÃ£o encontrar
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UsuÃ¡rio nÃ£o encontrado."));
    }

    /**
     * FUNÃ‡ÃƒO: Criar novo usuÃ¡rio no sistema
     * ====================================
     * Cadastra um novo usuÃ¡rio com validaÃ§Ãµes de seguranÃ§a
     * Ã‰ como registrar um novo cliente na empresa
     */
    public Usuario criar(Usuario usuario) {
        // TRATAMENTO ESPECIAL: Se nÃ£o tem senha (cliente), define string vazia
        // Isso evita erro de "campo obrigatÃ³rio" no banco de dados
        // Em sistemas reais, clientes podem ter senhas opcionais
        if (usuario.getSenha() == null) {
            usuario.setSenha(""); // Garante que nÃ£o Ã© NULL no banco
        }

        // Normaliza o email para minÃºsculas para garantir consistÃªncia
        if (usuario.getEmail() != null) {
            usuario.setEmail(usuario.getEmail());
        }

        // VALIDAÃ‡ÃƒO IMPORTANTE: Email deve ser Ãºnico no sistema
        // Verifica se jÃ¡ existe outro usuÃ¡rio com o mesmo email
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail jÃ¡ cadastrado.");
        }

        // Salva o usuÃ¡rio no banco de dados e retorna o usuÃ¡rio criado
        return usuarioRepository.save(usuario);
    }

    /**
     * FUNÃ‡ÃƒO: Atualizar dados de um usuÃ¡rio existente
     * ==============================================
     * Modifica as informaÃ§Ãµes de um usuÃ¡rio jÃ¡ cadastrado
     * Ã‰ como atualizar a ficha de um cliente
     */
    public Usuario atualizar(Long id, Usuario usuarioDetalhes) {
        // Primeiro busca o usuÃ¡rio existente (se nÃ£o existir, darÃ¡ erro)
        Usuario usuario = buscarPorId(id);

        // Atualiza todos os campos com os novos dados
        usuario.setNomeCompleto(usuarioDetalhes.getNomeCompleto());

        // Normaliza o email para minÃºsculas
        if (usuarioDetalhes.getEmail() != null) {
            usuario.setEmail(usuarioDetalhes.getEmail());
        } else {
            usuario.setEmail(usuarioDetalhes.getEmail());
        }

        // TRATAMENTO ESPECIAL DA SENHA: sÃ³ atualiza se foi fornecida uma nova
        if (usuarioDetalhes.getSenha() != null && !usuarioDetalhes.getSenha().isEmpty()) {
            // Em sistemas reais, aqui seria aplicada criptografia na senha
            usuario.setSenha(usuarioDetalhes.getSenha());
        }

        // Atualiza demais campos de contato e endereÃ§o
        usuario.setTelefone(usuarioDetalhes.getTelefone());
        usuario.setCpf(usuarioDetalhes.getCpf());
        usuario.setDataNascimento(usuarioDetalhes.getDataNascimento());
        usuario.setBairro(usuarioDetalhes.getBairro());
        usuario.setRua(usuarioDetalhes.getRua());
        usuario.setNumero(usuarioDetalhes.getNumero());
        usuario.setCidadeEstado(usuarioDetalhes.getCidadeEstado());
        usuario.setObservacoes(usuarioDetalhes.getObservacoes());

        // Salva as alteraÃ§Ãµes no banco e retorna o usuÃ¡rio atualizado
        return usuarioRepository.save(usuario);
    }

    /**
     * FUNÃ‡ÃƒO: Deletar um usuÃ¡rio do sistema
     * ====================================
     * Remove permanentemente um usuÃ¡rio do banco de dados
     * Ã‰ como remover a ficha de um cliente dos arquivos
     */
    public void deletar(Long id) {
        // Busca o usuÃ¡rio (se nÃ£o existir, buscarPorId jÃ¡ darÃ¡ erro)
        Usuario usuario = buscarPorId(id);

        // Se existe, remove do banco
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    /**
     * FUNÃ‡ÃƒO: Autenticar usuÃ¡rio no sistema (LOGIN)
     * ============================================
     * Verifica se email e senha estÃ£o corretos para permitir o login
     * Ã‰ como verificar a identidade de alguÃ©m na portaria
     */
    public Usuario autenticar(String email, String senha) {
        // Normaliza o email para minÃºsculas para evitar problemas com case-sensitive
        String emailNormalizado = email != null ? email : null;

        // Busca o usuÃ¡rio pelo email no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElse(null); // Se nÃ£o encontrar, retorna null

        // VALIDAÃ‡Ã•ES DE SEGURANÃ‡A:
        // 1. UsuÃ¡rio nÃ£o existe
        // 2. UsuÃ¡rio nÃ£o tem senha (Ã© cliente sem acesso ao sistema)
        // 3. Senha estÃ¡ vazia
        if (usuario == null || usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UsuÃ¡rio ou senha incorretos.");
        }

        // VERIFICAÃ‡ÃƒO DA SENHA (comparaÃ§Ã£o simples - em produÃ§Ã£o seria criptografada)
        if (usuario.getSenha().equals(senha)) {
            return usuario; // LOGIN SUCESSO: retorna os dados do usuÃ¡rio
        } else {
            // LOGIN FALHOU: senha incorreta
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UsuÃ¡rio ou senha incorretos.");
        }
    }
}