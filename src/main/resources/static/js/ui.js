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
    appState.selectedNodeId = null;
    appState.selectedEdgeId = null;
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

function toggleNoteTarget() {
    const type = document.getElementById('note-target-type').value;
    const clusterSelect = document.getElementById('note-cluster-select');
    if (type === 'CLUSTER') clusterSelect.classList.remove('hidden');
    else clusterSelect.classList.add('hidden');
}

document.addEventListener('DOMContentLoaded', () => {
    const selectBtn = document.getElementById('custom-select-btn');
    const selectMenu = document.getElementById('custom-select-menu');
    const selectText = document.getElementById('custom-select-text');
    const hiddenInput = document.getElementById('ms-lang');

    if (selectBtn) {
        selectBtn.addEventListener('click', () => {
            selectMenu.classList.toggle('opacity-0'); selectMenu.classList.toggle('invisible'); selectMenu.classList.toggle('scale-95');
        });
        document.querySelectorAll('.selectable-option').forEach(item => {
            item.addEventListener('click', () => {
                hiddenInput.value = item.getAttribute('data-value');
                selectText.innerHTML = item.innerHTML;
                selectMenu.classList.add('opacity-0', 'invisible', 'scale-95');
            });
        });
    }
});