async function createMicroservice() {
    const name = document.getElementById('ms-name').value;
    const lang = document.getElementById('ms-lang').value;
    if (!name.trim()) return alert("Enter node name");
    try {
        const res = await fetch('/api/infra/microservices', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, language: lang }) });
        if (!res.ok) throw new Error(res.statusText);
        document.getElementById('ms-name').value = '';
        loadGraph();
    } catch(e) { alert("Failed to create node: " + e.message); }
}

async function createNote() {
    const title = document.getElementById('note-title').value;
    const text = document.getElementById('note-text').value;
    const color = document.getElementById('note-color').value;
    const targetType = document.getElementById('note-target-type').value;
    let targetId = null;

    if (!title.trim()) return alert("Enter note title!");
    if (targetType === 'SERVICE') {
        if (!appState.selectedNodeId) return alert("Select a Service node on the graph first!");
        targetId = appState.selectedNodeId;
    } else if (targetType === 'CLUSTER') {
        targetId = document.getElementById('note-cluster-select').value;
        if (!targetId) return alert("Select a cluster from the list!");
    }

    try {
        const res = await fetch('/api/infra/notes', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ title, text, color, targetType, targetId }) });
        if (!res.ok) throw new Error(res.statusText);
        document.getElementById('note-title').value = ''; document.getElementById('note-text').value = '';
        loadGraph();
    } catch(e) { alert("Failed to create note: " + e.message); }
}

async function createCluster() {
    const name = document.getElementById('cluster-name').value;
    const color = document.getElementById('cluster-color').value;
    const selectedNodes = appState.network.getSelectedNodes().filter(id => !String(id).startsWith('hub_'));
    if (!name.trim() || selectedNodes.length < 2) return alert("Select at least 2 nodes and enter a name.");

    try {
        const res = await fetch('/api/infra/clusters', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, color, nodeIds: selectedNodes }) });
        if (!res.ok) throw new Error(res.statusText);
        closeAllPanels(); document.getElementById('cluster-name').value = '';
        loadGraph();
    } catch(e) { alert("Failed to create cluster: " + e.message); }
}

async function deleteSelectedNode() {
    if (!appState.selectedNodeId) return;
    try {
        const res = await fetch(`/api/infra/microservices/${appState.selectedNodeId}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(res.statusText);
        closeAllPanels(); loadGraph();
    } catch(e) { alert("Failed to delete node: " + e.message); }
}

async function deleteSelectedEdge() {
    if (!appState.selectedEdgeId) return;
    const edge = appState.edgesDataset.get(appState.selectedEdgeId);
    try {
        const res = await fetch(`/api/infra/dependencies/${edge.from}/${edge.to}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(res.statusText);
        closeAllPanels(); loadGraph();
    } catch(e) { alert("Failed to delete edge: " + e.message); }
}

async function analyzeBlastRadius() {
    const targetId = document.getElementById('blast-target').value; if(!targetId) return;
    try {
        const res = await fetch(`/api/infra/blast-radius/${targetId}`);
        if (!res.ok) throw new Error(res.statusText);
        const affected = await res.json();
        const affectedIds = affected.map(s => s.id); affectedIds.push(targetId);

        const nodesToUpdate = [];
        appState.nodesDataset.forEach(node => {
            if(node.type === 'NOTE' || String(node.id).startsWith('hub_')) return;
            let newColor = '#3f3f46'; let newSize = 35;
            if(node.id === targetId) { newColor = '#f43f5e'; newSize = 60; } else if(affectedIds.includes(node.id)) { newColor = '#fb923c'; newSize = 50; }
            nodesToUpdate.push({ id: node.id, icon: { ...node.icon, color: newColor, size: newSize } });
        });
        appState.nodesDataset.update(nodesToUpdate);
    } catch(e) { alert("Failed to analyze: " + e.message); }
}