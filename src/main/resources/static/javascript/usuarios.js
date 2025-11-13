document.addEventListener('DOMContentLoaded', () => {
    // A. Inicialização: Aponta para o formulário de Cliente
    carregarUsuarios(); 
    document.getElementById('form-cliente').addEventListener('submit', salvarUsuario);
    
    // B. Funcionalidade de Pesquisa (Filtro)
    document.getElementById('search-client').addEventListener('keyup', filtrarClientes);
    
    // C. Listener para Limpar e Cancelar (opcional, se precisar de lógica extra)
    // document.getElementById('btn-cancelar').addEventListener('click', limparFormulario); 
});

// A ROTA É MANTIDA como /api/usuarios, conforme sua solicitação
const API_BASE_URL = '/api/usuarios'; 

// Função para exibir mensagem de feedback (MANTIDA)
function exibirMensagem(texto, tipo) {
    const msgDiv = document.getElementById('feedback-message');
    msgDiv.textContent = texto;
    // Certifique-se de que sua classe CSS para 'message' e 'message-tipo' esteja definida
    msgDiv.className = `message message-${tipo}`; 
    msgDiv.style.display = 'block';
    setTimeout(() => {
        msgDiv.style.display = 'none';
    }, 5000);
}

// Limpa o formulário de Cliente e reseta o ID de edição
function limparFormulario() {
    // Aponta para o ID do FORM de Cliente
    document.getElementById('form-cliente').reset(); 
    // Aponta para o ID do input hidden de Cliente
    document.getElementById('cliente-id').value = ''; 
    
    // REMOVIDO: lógica de 'required' e 'senha'
    
    document.getElementById('btn-salvar').textContent = 'Salvar Cliente';
}

// ---------------------- CRUD: CREATE & UPDATE (Mapeado para Cliente) ----------------------

// usuarios.js

async function salvarUsuario(event) {
    event.preventDefault();

    const id = document.getElementById('cliente-id')?.value || '';
    const nomeCompleto = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    // O input se chama 'documento', mas no backend é 'cpf'
    const documento = document.getElementById('documento').value; 
    
    // CAMPOS DE ENDEREÇO E OBSERVAÇÕES OBTIDOS SEPARADAMENTE
    const rua = document.getElementById('rua').value;
    const numero = document.getElementById('numero').value;
    const bairro = document.getElementById('bairro').value;
    const cidade_estado = document.getElementById('cidade_estado').value;
    const observacoes = document.getElementById('observacoes').value;

    // REMOVIDO: A concatenação 'const endereco = ...'

    const usuarioData = { 
        id,
        // CORRIGIDO: O input do nome se torna 'nomeCompleto' no JSON (igual ao Java)
        nomeCompleto, 
        email,
        telefone,
        cpf: documento, // Mapeado de volta para 'cpf' no objeto JSON (igual ao Java)
        
        // NOVOS CAMPOS ENVIADOS SEPARADAMENTE (Alinhados com Usuario.java)
        rua,
        numero,
        bairro,
        cidadeEstado: cidade_estado, // Envia 'cidadeEstado' para mapear com o Java (camelCase)
        observacoes
        
        // NOTA: O campo 'endereco' unificado não é mais enviado.
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(usuarioData)
        });

        // ... restante da lógica de sucesso e erro ...
        const data = await response.json();

        if (response.ok) {
            exibirMensagem(`Cliente ${id ? 'atualizado' : 'cadastrado'} com sucesso!`, 'success');
            limparFormulario();
            carregarUsuarios();
        } else {
            const erroMsg = data.message || `Erro ao ${id ? 'atualizar' : 'cadastrar'} cliente.`;
            exibirMensagem(erroMsg, 'error');
        }
    } catch (error) {
        console.error('Erro de rede:', error);
        exibirMensagem('Erro de comunicação com o servidor. Tente novamente.', 'error');
    }
}

// ---------------------- CRUD: READ (Renderização de Cliente) ----------------------

// usuarios.js (Função carregarUsuarios)

async function carregarUsuarios() {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) throw new Error('Falha ao carregar clientes');
        
        // DECLARAÇÃO E LEITURA DA VARIÁVEL CLIENTES
        const clientes = await response.json();
        const listaClientes = document.getElementById('lista-clientes'); 
        listaClientes.innerHTML = ''; 

        clientes.forEach(cliente => { 
            const tr = document.createElement('tr'); // DEVE SER DECLARADO AQUI
            
            // 1. Reconstruir a string de endereço para exibição na lista
            const enderecoCompleto = `${cliente.rua || ''}, ${cliente.numero || ''} - ${cliente.bairro || ''}, ${cliente.cidadeEstado || ''}`;

            tr.innerHTML = `
                <td>${cliente.id}</td>
                <td>${cliente.nomeCompleto || '-'}</td>
                <td>${cliente.telefone || '-'}</td>
                
                <td>${enderecoCompleto.trim().replace(/^,|-|,\s*$|^\s*/g, '') || '-'}</td> 
                
                <td class="action-icons">
                    <i class="fas fa-edit edit-action" title="Editar" onclick="editarUsuario(${cliente.id})"></i>
                    <i class="fas fa-trash-alt delete-action" title="Excluir" onclick="deletarUsuario(${cliente.id}, '${cliente.nomeCompleto}')"></i>
                </td>
            `;
            listaClientes.appendChild(tr); // Corrigido para listaClientes
        });

    } catch (error) {
        console.error('Erro ao carregar clientes:', error);
        exibirMensagem('Não foi possível carregar a lista de clientes.', 'error');
    }
}
// ---------------------- CRUD: EDIT (Carregar dados de Cliente) ----------------------

// usuarios.js

async function editarUsuario(id) { 
    try {
        const response = await fetch(`${API_BASE_URL}/${id}`);
        if (!response.ok) {
            throw new Error('Cliente não encontrado.');
        }
        const cliente = await response.json();
        
        // 1. Preencher os campos do formulário
        document.getElementById('cliente-id').value = cliente.id;
        
        // 2. CORREÇÃO DE NOMES (Alinhamento JS/Java)
        document.getElementById('nome').value = cliente.nomeCompleto; 
        document.getElementById('email').value = cliente.email;
        document.getElementById('telefone').value = cliente.telefone;
        document.getElementById('documento').value = cliente.cpf; // O input 'documento' usa o campo 'cpf' do JSON
        
        // 3. NOVOS CAMPOS SEPARADOS DE ENDEREÇO
        document.getElementById('rua').value = cliente.rua || ''; 
        document.getElementById('numero').value = cliente.numero || ''; 
        document.getElementById('bairro').value = cliente.bairro || ''; 
        // O campo 'cidade_estado' do HTML usa o campo 'cidadeEstado' do JSON
        document.getElementById('cidade_estado').value = cliente.cidadeEstado || '';
        document.getElementById('observacoes').value = cliente.observacoes || '';

        // Exibir o formulário de edição/cadastro (se ele estiver oculto)
        // Por exemplo:
        document.getElementById('form-cliente').style.display = 'block';
        
    } catch (error) {
        console.error('Erro ao carregar dados para edição:', error);
        exibirMensagem('Erro ao carregar os dados do cliente para edição.', 'error');
    }
}

// ---------------------- CRUD: DELETE (Exclusão de Cliente) ----------------------

async function deletarUsuario(id, nome) { // Mantemos o nome da função
    if (!confirm(`Tem certeza que deseja excluir o cliente: ${nome}?`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            exibirMensagem(`Cliente ${nome} excluído com sucesso!`, 'success');
            carregarUsuarios(); // Recarrega a lista
        } else {
            exibirMensagem('Erro ao excluir cliente.', 'error');
        }
    } catch (error) {
        console.error('Erro de rede ao deletar:', error);
        exibirMensagem('Erro de comunicação com o servidor.', 'error');
    }
}

// ---------------------- FUNÇÕES ADICIONAIS DE CLIENTE ----------------------

// Função para Filtragem (Barra de Pesquisa - Front-end)
function filtrarClientes(e) {
    const searchTerm = e.target.value.toLowerCase();
    const tableBody = document.getElementById('lista-clientes'); 
    // Se o JS for chamado antes do body ser renderizado, pode dar erro aqui.
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