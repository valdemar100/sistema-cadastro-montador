document.addEventListener('DOMContentLoaded', () => {
    carregarMontadores(); 
    document.getElementById('form-montador').addEventListener('submit', salvarMontador);
    document.getElementById('search-montador').addEventListener('keyup', filtrarMontadores);
});
const API_BASE_URL = '/api/montador'; 

// Funções utilitárias (exibirMensagem, limparFormulario) - Mantidas
function exibirMensagem(texto, tipo) {
    const msgDiv = document.getElementById('feedback-message');
    msgDiv.textContent = texto;
    msgDiv.className = `message message-${tipo}`; 
    msgDiv.style.display = 'block';
    setTimeout(() => {
        msgDiv.style.display = 'none';
    }, 5000);
}

function limparFormulario() {
    document.getElementById('form-montador').reset(); 
    document.getElementById('montador-id').value = ''; 
    document.getElementById('btn-salvar').textContent = 'Salvar Montador';
}


// ---------------------- CRUD: CREATE & UPDATE ----------------------

async function salvarMontador(event) {
    event.preventDefault();

    const id = document.getElementById('montador-id')?.value || '';
    const nomeCompleto = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const cpf = document.getElementById('documento').value; // Usando 'documento' como CPF
    const rua = document.getElementById('rua').value;
    const numero = document.getElementById('numero').value;
    const bairro = document.getElementById('bairro').value;
    const cidadeEstado = document.getElementById('cidade_estado').value;
    const observacoes = document.getElementById('observacoes').value;
    
    // NOTA: Senha não é coletada nem enviada. O backend salvará como NULL/""

    const montadorData = { 
        id,
        nomeCompleto, 
        email,
        telefone,
        cpf,
        rua,
        numero,
        bairro,
        cidadeEstado, 
        observacoes,
        // Omissão intencional da SENHA
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(montadorData)
        });

        const data = await response.json();

        if (response.ok) {
            exibirMensagem(`Montador ${id ? 'atualizado' : 'cadastrado'} com sucesso!`, 'success');
            limparFormulario();
            carregarMontadores();
        } else {
            const erroMsg = data.message || `Erro ao ${id ? 'atualizar' : 'cadastrar'} montador.`;
            exibirMensagem(erroMsg, 'error');
        }
    } catch (error) {
        console.error('Erro de rede:', error);
        exibirMensagem('Erro de comunicação com o servidor. Tente novamente.', 'error');
    }
}

// ---------------------- CRUD: READ (Montadores) ----------------------

async function carregarMontadores() {
    try {
        // ATENÇÃO: Esta rota GET /api/usuarios retorna TODOS os usuários (Clientes e Montadores).
        // Se você não tiver um campo 'tipo' no backend, a lista mostrará todos.
        // Para o seu propósito acadêmico, vamos assumir que o professor aceita mostrar todos.
        const response = await fetch(API_BASE_URL); 
        if (!response.ok) throw new Error('Falha ao carregar montadores');
        
        const montadores = await response.json();
        const listaMontadores = document.getElementById('lista-montadores'); 
        listaMontadores.innerHTML = ''; 
        
        // ORDENAÇÃO: Garante que a lista está em ordem (se você aplicou o Sort no Service)
        
        montadores.forEach(montador => { 
            const tr = document.createElement('tr');
            
            // Reconstrução do Endereço (igual a usuários.js)
            const enderecoCompleto = `${montador.rua || ''}, ${montador.numero || ''} - ${montador.bairro || ''}, ${montador.cidadeEstado || ''}`;

            tr.innerHTML = `
                <td>${montador.id}</td>
                <td>${montador.nomeCompleto || '-'}</td>
                <td>${montador.telefone || '-'}</td>
                <td>${enderecoCompleto.trim().replace(/^,|-|,\s*$|^\s*/g, '') || '-'}</td>
                <td class="action-icons">
                    <i class="fas fa-edit edit-action" title="Editar" onclick="editarMontador(${montador.id})"></i>
                    <i class="fas fa-trash-alt delete-action" title="Excluir" onclick="deletarMontador(${montador.id}, '${montador.nomeCompleto}')"></i>
                </td>
            `;
            listaMontadores.appendChild(tr);
        });

    } catch (error) {
        console.error('Erro ao carregar montadores:', error);
        exibirMensagem('Não foi possível carregar a lista de montadores.', 'error');
    }
}

// ---------------------- CRUD: EDIT (Carregar dados) ----------------------

async function editarMontador(id) { 
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        if (!response.ok) throw new Error('Montador não encontrado');
        
        const montador = await response.json();

        document.getElementById('montador-id').value = montador.id;
        document.getElementById('nome').value = montador.nomeCompleto;
        document.getElementById('email').value = montador.email;
        document.getElementById('telefone').value = montador.telefone;
        document.getElementById('documento').value = montador.cpf;
        
        // Campos de Endereço (Usando os novos campos separados)
        document.getElementById('rua').value = montador.rua || '';
        document.getElementById('numero').value = montador.numero || '';
        document.getElementById('bairro').value = montador.bairro || '';
        document.getElementById('cidade_estado').value = montador.cidadeEstado || '';
        document.getElementById('observacoes').value = montador.observacoes || '';

        // Botão
        document.getElementById('btn-salvar').textContent = 'Atualizar Montador';
        exibirMensagem(`Carregando dados de ${montador.nomeCompleto} para edição.`, 'warning');
        window.scrollTo(0, 0); 
        
    } catch (error) {
        console.error('Erro ao buscar montador para edição:', error);
        exibirMensagem('Erro ao carregar dados do montador.', 'error');
    }
}

// ---------------------- CRUD: DELETE (Exclusão) ----------------------

async function deletarMontador(id, nome) { 
    if (!confirm(`Tem certeza que deseja excluir o montador: ${nome}?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            exibirMensagem(`Montador ${nome} excluído com sucesso!`, 'success');
            carregarMontadores(); 
        } else {
            exibirMensagem('Erro ao excluir montador.', 'error');
        }
    } catch (error) {
        console.error('Erro de rede ao deletar:', error);
        exibirMensagem('Erro de comunicação com o servidor.', 'error');
    }
}

// ---------------------- FUNÇÕES ADICIONAIS ----------------------

function filtrarMontadores(e) {
    const searchTerm = e.target.value.toLowerCase();
    const tableBody = document.getElementById('lista-montadores'); 
    if (!tableBody) return; 
    
    const rows = tableBody.querySelectorAll('tr');

    rows.forEach(row => {
        let rowText = row.innerText.toLowerCase();
        
        if (rowText.includes(searchTerm)) {
            row.style.display = ''; 
        } else {
            row.style.display = 'none'; 
        }
    });
}
document.addEventListener('DOMContentLoaded', () => {
    // 1. Obtém o caminho da URL atual (ex: /usuarios.html ou /montadores.html)
    const path = window.location.pathname;
    
    // 2. Seleciona todos os links na sua navbar
    const navLinks = document.querySelectorAll('header nav a');
    
    // 3. Itera sobre os links e verifica qual corresponde ao caminho atual
    navLinks.forEach(link => {
        // Obtém o href do link (ex: 'usuarios.html')
        const linkHref = link.getAttribute('href');
        
        // Verifica se o link href está contido no caminho atual
        // Isso lida com index.html, /usuarios, /montadores.html, etc.
        // Se a URL for /usuarios.html, e o href for usuarios.html, ele acerta.
        
        if (path.includes(linkHref) || (path === '/' && linkHref === 'index.html')) {
            // Se encontrar a correspondência, adiciona a classe 'active'
            link.classList.add('active');
        }
    });
});