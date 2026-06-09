window.appState = {
    network: null,
    selectedNodeId: null,
    selectedEdgeId: null,
    nodesDataset: null,
    edgesDataset: null,
    availableTeams: [],
    currentProjectId: null,
    currentProjectName: null,
    projects: []
};

function requireProject() {
    if (!appState.currentProjectId) {
        showProjectSelector();
        return false;
    }
    return true;
}

function getAuthHeaders() {
    const headers = { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('jwt') };
    if (appState.currentProjectId) {
        headers['Project-Id'] = appState.currentProjectId;
    }
    return headers;
}

async function loadProjects() {
    try {
        const res = await fetch('/api/projects', { headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') } });
        if (res.ok) {
            appState.projects = await res.json();
            renderProjectList();
        } else {
            console.error('Failed to load projects:', res.status);
        }
    } catch (e) {
        console.error('Failed to load projects', e);
    }
}

function renderProjectList() {
    const list = document.getElementById('project-list');
    if (!list) return;
    list.innerHTML = '';
    if (appState.projects.length === 0) {
        list.innerHTML = '<div class="text-center text-zinc-500 text-sm py-8">No projects yet. Create one above.</div>';
        return;
    }
    appState.projects.forEach(p => {
        const div = document.createElement('div');
        div.className = 'project-item flex items-center justify-between p-3 rounded-xl bg-black/30 border border-white/5 hover:border-indigo-500/30 cursor-pointer transition-all';
        div.innerHTML = `
            <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center">
                    <i class="fa-solid fa-diagram-project text-white text-xs"></i>
                </div>
                <div>
                    <div class="text-sm font-medium text-zinc-200">${p.name}</div>
                    <div class="text-[10px] text-zinc-500">${p.id ? p.id.substring(0, 8) + '...' : ''}</div>
                </div>
            </div>
            <button onclick="event.stopPropagation(); deleteProject('${p.id}')" class="text-zinc-600 hover:text-rose-400 transition-colors p-1">
                <i class="fa-solid fa-trash-can text-xs"></i>
            </button>
        `;
        div.onclick = () => selectProject(p.id, p.name);
        list.appendChild(div);
    });
}

function selectProject(id, name) {
    appState.currentProjectId = id;
    appState.currentProjectName = name;
    localStorage.setItem('currentProjectId', id);
    localStorage.setItem('currentProjectName', name);
    const modal = document.getElementById('project-modal');
    if (modal) modal.classList.add('hidden');
    const display = document.getElementById('project-name-display');
    if (display) display.textContent = name;
    if (typeof loadGraph === 'function') loadGraph();
}

async function createNewProject() {
    const nameInput = document.getElementById('new-project-name');
    const name = nameInput ? nameInput.value.trim() : '';
    if (!name) return;
    try {
        const res = await fetch('/api/projects', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + localStorage.getItem('jwt') },
            body: JSON.stringify({ name })
        });
        if (res.ok) {
            const project = await res.json();
            if (nameInput) nameInput.value = '';
            await loadProjects();
            selectProject(project.id, project.name);
        } else {
            alert('Failed to create project: ' + (await res.text()));
        }
    } catch (e) {
        alert('Cannot connect to server. Is the backend running?');
    }
}

async function deleteProject(id) {
    if (!confirm('Delete this project and all its data?')) return;
    try {
        const res = await fetch('/api/projects/' + id, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwt') }
        });
        if (res.ok) {
            if (appState.currentProjectId === id) {
                appState.currentProjectId = null;
                appState.currentProjectName = null;
                const display = document.getElementById('project-name-display');
                if (display) display.textContent = 'Select Project';
            }
            await loadProjects();
        } else {
            alert('Failed to delete project');
        }
    } catch (e) {
        alert('Cannot connect to server.');
    }
}

function showProjectSelector() {
    const modal = document.getElementById('project-modal');
    if (!modal) return;
    modal.classList.remove('hidden');
    loadProjects();
}

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
        const el = document.getElementById(id);
        if (el) el.classList.add('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
    });
}

function startLinkingMode() {
    if (!requireProject()) return;
    appState.network.addEdgeMode(); closeAllPanels();
    document.getElementById('main-canvas-container').classList.add('linking-mode');
    const toast = document.getElementById('linking-toast');
    if (toast) toast.classList.remove('opacity-0', '-translate-y-10');
}
function cancelLinking() { if (appState.network) appState.network.disableEditMode(); stopLinkingUI(); }
function stopLinkingUI() {
    document.getElementById('main-canvas-container').classList.remove('linking-mode');
    const toast = document.getElementById('linking-toast');
    if (toast) toast.classList.add('opacity-0', '-translate-y-10');
}

function initCustomSelects() {
    document.querySelectorAll('.custom-select').forEach(wrapper => {
        const btn = wrapper.querySelector('.select-btn');
        const menu = wrapper.querySelector('.select-menu');
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
            if (li) {
                hiddenInput.value = li.getAttribute('data-value');
                const textEl = newBtn.querySelector('.select-text');
                if (textEl) textEl.innerHTML = li.innerHTML;
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

function toggleNoteTarget() {
    const type = document.getElementById('note-target-type').value;
    const clusterWrapper = document.getElementById('wrapper-note-cluster');
    if (type === 'CLUSTER') {
        if (clusterWrapper) clusterWrapper.classList.remove('hidden');
    } else {
        if (clusterWrapper) clusterWrapper.classList.add('hidden');
    }
}

function updateSelectOptions(wrapperId, options) {
    const wrapper = document.getElementById(wrapperId);
    if (!wrapper) return;
    const menu = wrapper.querySelector('.select-menu');
    const text = wrapper.querySelector('.select-text');
    const hiddenInput = wrapper.querySelector('input[type="hidden"]');
    if (!menu) return;

    let innerDiv = menu.querySelector('.p-1');
    if (!innerDiv) {
        innerDiv = document.createElement('div');
        innerDiv.className = 'p-1 flex flex-col gap-0.5 pb-2';
        menu.appendChild(innerDiv);
    }
    innerDiv.innerHTML = '';

    if (!options || options.length === 0) {
        innerDiv.innerHTML = '<div class="px-3 py-2 text-xs text-zinc-500 text-center">No teams — create one first</div>';
        if (text) text.innerHTML = '<span class="text-zinc-500">No teams</span>';
        if (hiddenInput) hiddenInput.value = '';
        return;
    }

    options.forEach(opt => {
        const li = document.createElement('li');
        li.className = 'selectable-option px-2.5 py-1.5 rounded-lg hover:bg-white/10 cursor-pointer flex items-center gap-2 text-sm text-zinc-200';
        li.setAttribute('data-value', opt.id);
        li.innerHTML = `<i class="fa-solid fa-users text-purple-400 w-4 text-center"></i> ${opt.label}`;
        innerDiv.appendChild(li);
    });

    if (text && options.length > 0) {
        text.innerHTML = `<i class="fa-solid fa-users text-purple-400 w-4 text-center"></i> ${options[0].label}`;
        if (hiddenInput) hiddenInput.value = options[0].id;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const noteTarget = document.getElementById('note-target-type');
    if (noteTarget) {
        noteTarget.addEventListener('change', toggleNoteTarget);
    }
    initCustomSelects();
});
