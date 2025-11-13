/**
 * CLASSE DATALOADER - INICIALIZAÇÃO DO SISTEMA
 * =============================================
 * Esta classe é responsável por criar dados iniciais no banco quando o sistema inicia.
 * É como preparar a casa antes de receber visitas - coloca algumas informações básicas.
 */
package com.lucasmicaeldev.montadordemoveis.demo;

import com.lucasmicaeldev.montadordemoveis.demo.model.Usuario;
import com.lucasmicaeldev.montadordemoveis.demo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // Diz ao Spring: "Essa classe faz parte do sistema, use ela!"
public class DataLoader implements CommandLineRunner {

    // Conexão com o banco de dados para manipular usuários
    private final UsuarioRepository usuarioRepo;

    /**
     * CONSTRUTOR - Preparação da classe
     * ================================
     * Recebe a "ferramenta" para acessar o banco de dados de usuários
     */
    public DataLoader(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    /**
     * FUNÇÃO PRINCIPAL - Executa quando o sistema inicia
     * ================================================
     * Como um "setup inicial" - roda uma vez quando a aplicação liga
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // PASSO 1: Aguarda 1 segundo para garantir que o banco está pronto
            // (É como esperar a geladeira ligar antes de colocar comida)
            Thread.sleep(1000);

            // PASSO 2: Verifica se já existem usuários no banco
            // (Só cria dados se o banco estiver vazio)
            if (usuarioRepo.count() == 0) {

                // PASSO 3: Cria um usuário administrador para testes
                Usuario u = new Usuario();
                u.setNomeCompleto("Lucas Micael Lima de Souza");
                u.setEmail("lucasbjj49@gmail.com");
                u.setSenha("181920");

                // PASSO 4: Salva o usuário no banco de dados
                usuarioRepo.save(u);
                System.out.println("✅ Usuário criado: lucasbjj49@gmail.com / 181920");

            } else {
                // Se já existem dados, não faz nada
                System.out.println("ℹ️ Dados já existem no banco, pulando inicialização");
            }

        } catch (Exception e) {
            // Se algo der errado, mostra o erro mas não quebra o sistema
            System.err.println("❌ Erro ao inicializar dados: " + e.getMessage());
        }
    }
}
