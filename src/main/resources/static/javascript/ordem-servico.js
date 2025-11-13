document.addEventListener('DOMContentLoaded', () => {
    // URL base da sua API para Ordens de Serviço
    // URL base da API - relativa para funcionar tanto local quanto Railway
    const BASE_URL = '/api/ordens'; 
    
    // Elementos do DOM
    const formOS = document.getElementById('form-os');
    const listaOS = document.getElementById('lista-os');
    const osIdInput = document.getElementById('os-id');
    const feedbackMessage = document.getElementById('feedback-message');
    const btnSalvar = document.getElementById('btn-salvar');

    // --- Funções de Feedback e Utilidade ---

    function exibirFeedback(mensagem, tipo = 'success') {
        feedbackMessage.textContent = mensagem;
        feedbackMessage.className = `message message-${tipo}`;
        feedbackMessage.style.display = 'block';
        setTimeout(() => {
            feedbackMessage.style.display = 'none';
        }, 4000);
    }

    function limparFormulario() {
        formOS.reset();
        osIdInput.value = '';
        btnSalvar.textContent = 'Salvar OS';
        btnSalvar.classList.add('btn-primary');
        document.getElementById('numeroOs').value = ''; // Garante que o campo readonly seja limpo
    }

    // --- Funções CRUD ---

    // READ: Carrega todas as Ordens de Serviço e preenche a tabela
    async function carregarOrdens() {
        try {
            const response = await fetch(BASE_URL);
            if (!response.ok) {
                throw new Error('Erro ao carregar Ordens de Serviço: ' + response.statusText);
            }
            const ordens = await response.json();
            listaOS.innerHTML = ''; // Limpa a lista antes de preencher

            if (ordens.length === 0) {
                listaOS.innerHTML = '<tr><td colspan="7" class="text-center">Nenhuma Ordem de Serviço cadastrada.</td></tr>';
                return;
            }

            ordens.forEach(os => {
                const row = listaOS.insertRow();
                
                // Mapeia o status do Enum para um display amigável (se necessário)
                const statusDisplay = os.status ? os.status.replace(/_/g, ' ') : 'N/A';

                row.innerHTML = `
                    <td>${os.numeroOs}</td>
                    <td>${os.clienteId}</td>
                    <td>${os.montadorId}</td>
                    <td>${os.dataAgendamento ? new Date(os.dataAgendamento).toLocaleDateString('pt-BR') : 'Sem Data'}</td>
                    <td>${os.tipoDeMovel}</td>
                    <td><span class="status-${os.status.toLowerCase()}">${statusDisplay}</span></td>
                    <td class="text-center">
                        <div class="action-icons">
                            <i class="fas fa-edit edit-action" onclick="preencherFormularioParaEdicao(${os.id})"></i>
                            <i class="fas fa-trash-alt delete-action" onclick="deletarOrdemServico(${os.id})"></i>
                        </div>
                    </td>
                `;
            });

        } catch (error) {
            console.error('Erro:', error);
            exibirFeedback('Falha ao carregar a lista de Ordens de Serviço.', 'error');
        }
    }
    
    // CREATE/UPDATE: Salva ou Atualiza uma Ordem de Serviço
    formOS.addEventListener('submit', async (e) => {
        e.preventDefault();

        const id = osIdInput.value;
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${BASE_URL}/${id}` : BASE_URL;

        const dadosOS = {
            id: id ? parseInt(id) : null,
            numeroOs: document.getElementById('numeroOs').value,
            clienteId: parseInt(document.getElementById('clienteId').value),
            montadorId: parseInt(document.getElementById('montadorId').value),
            enderecoServico: document.getElementById('enderecoServico').value,
            dataAgendamento: document.getElementById('dataAgendamento').value, // Formato YYYY-MM-DD
            status: document.getElementById('status').value, // Valor do Enum
            descricao: document.getElementById('descricao').value,
            tipoDeMovel: document.getElementById('tipoDeMovel').value,
        };
        
        // Verifica se os IDs são números válidos (preventivo)
        if (isNaN(dadosOS.clienteId) || isNaN(dadosOS.montadorId)) {
            exibirFeedback('Os IDs de Cliente e Montador devem ser números válidos.', 'error');
            return;
        }

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(dadosOS),
            });

            if (!response.ok) {
                // Tentativa de capturar a mensagem de erro do Spring
                const errorBody = await response.text();
                throw new Error(`Erro ao ${id ? 'atualizar' : 'criar'} OS: ${response.status} - ${errorBody}`);
            }

            const osSalva = await response.json();
            const mensagem = id ? `Ordem de Serviço ${osSalva.numeroOs} atualizada com sucesso!` : `Ordem de Serviço ${osSalva.numeroOs} criada com sucesso!`;
            
            exibirFeedback(mensagem, 'success');
            limparFormulario();
            carregarOrdens();

        } catch (error) {
            console.error('Erro:', error);
            exibirFeedback(`Falha ao ${id ? 'atualizar' : 'criar'} a Ordem de Serviço. Verifique o console.`, 'error');
        }
    });

    // Função global para edição (acessível pelos botões da tabela)
    window.preencherFormularioParaEdicao = async (id) => {
        try {
            const response = await fetch(`${BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error('OS não encontrada para edição.');
            }
            const os = await response.json();
            
            // Preenche os campos do formulário
            osIdInput.value = os.id;
            document.getElementById('numeroOs').value = os.numeroOs;
            document.getElementById('clienteId').value = os.clienteId;
            document.getElementById('montadorId').value = os.montadorId;
            document.getElementById('enderecoServico').value = os.enderecoServico;
            document.getElementById('dataAgendamento').value = os.dataAgendamento; // Já está no formato YYYY-MM-DD
            document.getElementById('status').value = os.status;
            document.getElementById('descricao').value = os.descricao;
            document.getElementById('tipoDeMovel').value = os.tipoDeMovel;

            // Altera o botão de Salvar para Atualizar
            btnSalvar.textContent = 'Atualizar OS';
            // Mantém a classe btn-primary para manter a estilização
            btnSalvar.classList.add('btn-primary');
            
            exibirFeedback(`Pronto para editar a OS ${os.numeroOs}.`, 'info');
            
        } catch (error) {
            console.error('Erro:', error);
            exibirFeedback('Falha ao buscar dados da OS para edição.', 'error');
        }
    };
    
    // DELETE: Deleta uma Ordem de Serviço
    window.deletarOrdemServico = async (id) => {
        if (!confirm('Tem certeza que deseja deletar esta Ordem de Serviço? Esta ação é irreversível.')) {
            return;
        }

        try {
            const response = await fetch(`${BASE_URL}/${id}`, {
                method: 'DELETE',
            });

            if (response.status !== 204) { // 204 No Content é o esperado para um DELETE bem-sucedido
                throw new Error('Falha ao deletar a Ordem de Serviço.');
            }

            exibirFeedback('Ordem de Serviço deletada com sucesso.', 'success');
            carregarOrdens();

        } catch (error) {
            console.error('Erro:', error);
            exibirFeedback('Falha ao deletar a Ordem de Serviço.', 'error');
        }
    };

    // Inicialização: Carrega a lista quando a página é carregada
    carregarOrdens();
    
    // Adiciona evento ao botão "Limpar Campos"
    document.querySelector('.btn-tertiary').addEventListener('click', limparFormulario);
});