// ====================================================================
// CONSTANTES DE URL - URLs relativas para funcionar tanto local quanto Railway
// ====================================================================
const API_DASHBOARD_URL = '/api/dashboard'; 
const API_ORDENS_URL = '/api/ordens'; 

// ====================================================================
// FUNÇÕES UTILITÁRIAS
// ====================================================================
function getStatusClass(status) {
    if (!status) return 'pendente'; 
    const s = status.toUpperCase();
    if (s === 'CONFIRMADA' || s === 'CONCLUIDA') return 'confirmada';
    if (s === 'AGENDADA') return 'agendada';
    return 'pendente';
}

// ====================================================================
// 1. CARREGA DADOS DO RESUMO (CARDS)
// ====================================================================
async function carregarDadosResumo() {
    try {
        const response = await fetch(API_DASHBOARD_URL);
        
        if (!response.ok) {
            throw new Error(`Erro HTTP ao carregar o dashboard! Status: ${response.status}`);
        }

        const dados = await response.json(); 
        
        // CORREÇÃO FINAL: Usando as propriedades em camelCase minúsculo
        document.getElementById('totalClientes').innerHTML = `<i class="fas fa-user-plus"></i> ${dados.clientes || '0'}`;
        document.getElementById('totalMontadores').innerHTML = `<i class="fas fa-user-check"></i> ${dados.montadores || '0'}`;
        document.getElementById('totalOrdensServico').innerHTML = `<i class="fas fa-box-open"></i> ${dados.ordensTotais || '0'}`; 
        document.getElementById('osPendentes').innerHTML = `<i class="fas fa-clock"></i> ${dados.osPendentes || '0'}`;
        document.getElementById('osAgendadas').innerHTML = `<i class="fas fa-calendar-check"></i> ${dados.osAgendadas || '0'}`;
        
    } catch (error) {
        console.error("Falha ao buscar métricas do Dashboard:", error);
        const fallbackHTML = `<i class="fas fa-exclamation-triangle"></i> ERRO API`;
        // Aplica o fallback em caso de falha de conexão
        document.getElementById('totalClientes').innerHTML = fallbackHTML;
        document.getElementById('totalMontadores').innerHTML = fallbackHTML;
        document.getElementById('totalOrdensServico').innerHTML = fallbackHTML;
        document.getElementById('osPendentes').innerHTML = fallbackHTML;
        document.getElementById('osAgendadas').innerHTML = fallbackHTML;
    }
}

// ====================================================================
// 2. CARREGA DADOS DA TABELA (DINÂMICO)
// ====================================================================
async function carregarOrdensNaTabela() {
    const tbody = document.getElementById('tabelaOrdens'); 
    if (!tbody) return;

    try {
        const response = await fetch(API_ORDENS_URL);
        
        if (!response.ok) {
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        
        const ordens = await response.json(); 
        
        tbody.innerHTML = ''; // Limpa a tabela

        if (ordens.length === 0) {
             tbody.innerHTML = '<tr><td colspan="7" class="text-center">Nenhuma Ordem de Serviço encontrada.</td></tr>';
             return;
        }

        ordens.forEach(os => {
            // Mapeamento usando as propriedades exatas do seu JSON (apenas IDs e status)
            const numeroOs = os.numeroOs || os.id; 
            const cliente = `ID: ${os.clienteId || 'N/A'}`; // Mostra ID
            const endereco = os.enderecoServico || 'N/A';
            const montador = `ID: ${os.montadorId || '-'}`; // Mostra ID
            
            const statusText = os.status ? os.status.charAt(0).toUpperCase() + os.status.slice(1).toLowerCase() : 'Pendente';
            const statusClass = getStatusClass(os.status);
            
            const dataFormatada = os.dataAgendamento 
                ? new Date(os.dataAgendamento).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric' }) 
                : '-';
            
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${numeroOs}</td> 
                <td>${cliente}</td> 
                <td>${endereco}</td> 
                <td>${dataFormatada}</td> 
                <td>${montador}</td> 
                <td><span class="status ${statusClass}">${statusText}</span></td>
                <td><i class="fas fa-eye action-icon" title="Ver Detalhes" onclick="visualizarOrdem(${os.id})"></i></td>
            `;
            tbody.appendChild(row);
        });

    } catch (error) {
        console.error("Falha ao buscar Ordens de Serviço:", error);
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Erro ao carregar dados da API. Verifique o Console.</td></tr>';
    }
}

// ====================================================================
// INICIALIZAÇÃO
// ====================================================================
document.addEventListener('DOMContentLoaded', () => {
    // Carrega cards de resumo
    carregarDadosResumo(); 
    
    // Carrega tabela de ordens de serviço (dinamicamente)
    carregarOrdensNaTabela(); 
});