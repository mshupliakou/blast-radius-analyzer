window.appState = {
    network: null,
    selectedNodeId: null,
    selectedEdgeId: null,
    nodesDataset: null,
    edgesDataset: null,
    availableTeams: []
};

function switchTab(tabId) {
    document.getElementById('tab-infra').classList.add('hidden');
    document.getElementById('tab-org').classList.add('hidden');
    document.getElementById('btn-tab-infra').className = 'tab-btn tab-inactive flex-1 py-2 rounded-xl text-xs font-semibold uppercase tracking-wider';
    document.getElementById('btn-tab-org').className = 'tab-btn tab-inactive flex-1 py-2 rounded-xl text-xs font-semibold uppercase tracking-wider';
    document.getElementById(`tab-${tabId}`).classList.remove('hidden');
    document.getElementById(`btn-tab-${tabId}`).className = 'tab-btn tab-active flex-1 py-2 rounded-xl text-xs font-semibold uppercase tracking-wider';
}

function closeAllPanels() {
    appState.selectedNodeId = null; appState.selectedEdgeId = null;
    ['action-panel', 'edge-action-panel', 'cluster-action-panel'].forEach(id => {
        document.getElementById(id).classList.add('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
    });
}

function startLinkingMode() {
    appState.network.addEdgeMode(); closeAllPanels();
    document.getElementById('main-canvas-container').classList.add('linking-mode');
    document.getElementById('linking-toast').classList.remove('opacity-0', '-translate-y-10');
}
function cancelLinking() { appState.network.disableEditMode(); stopLinkingUI(); }
function stopLinkingUI() {
    document.getElementById('main-canvas-container').classList.remove('linking-mode');
    document.getElementById('linking-toast').classList.add('opacity-0', '-translate-y-10');
}

// Движок кастомных селектов (один для всех)
function initCustomSelects() {
    document.querySelectorAll('.custom-select').forEach(wrapper => {
        const btn = wrapper.querySelector('.select-btn');
        const menu = wrapper.querySelector('.select-menu');
        const text = wrapper.querySelector('.select-text');
        const hiddenInput = wrapper.querySelector('input[type="hidden"]');

        const newBtn = btn.cloneNode(true);
        btn.parentNode.replaceChild(newBtn, btn);

        newBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            document.querySelectorAll('.select-menu').forEach(m => {
                if (m !== menu) m.classList.add('opacity-0', 'invisible', 'scale-95');
            });
            menu.classList.toggle('opacity-0');
            menu.classList.toggle('invisible');
            menu.classList.toggle('scale-95');
        });

        menu.onclick = (e) => {
            const li = e.target.closest('li.selectable-option');
            if(li) {
                hiddenInput.value = li.getAttribute('data-value');
                text.innerHTML = li.innerHTML;
                menu.classList.add('opacity-0', 'invisible', 'scale-95');
                hiddenInput.dispatchEvent(new Event('change'));
            }
        };
    });

    document.addEventListener('click', (e) => {
        if (!e.target.closest('.custom-select')) {
            document.querySelectorAll('.select-menu').forEach(m => m.classList.add('opacity-0', 'invisible', 'scale-95'));
        }
    });
}

// Логика показа списка кластеров для нотаток
function toggleNoteTarget() {
    const type = document.getElementById('note-target-type').value;
    const clusterWrapper = document.getElementById('wrapper-note-cluster');
    if (type === 'CLUSTER') clusterWrapper.classList.remove('hidden');
    else clusterWrapper.classList.add('hidden');
}

// Запуск движка
document.addEventListener('DOMContentLoaded', () => {
    initCustomSelects();
    document.getElementById('note-target-type').addEventListener('change', toggleNoteTarget);
});