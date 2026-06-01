const graphOptions = {
    edges: { width: 1.5, color: { color: '#52525b', highlight: '#818cf8' }, arrows: { to: { enabled: true, scaleFactor: 0.5 } }, smooth: { type: 'continuous', roundness: 0.5 } },
    physics: { forceAtlas2Based: { gravitationalConstant: -70, centralGravity: 0.005, springLength: 150 }, solver: 'forceAtlas2Based', timestep: 0.3 },
    interaction: { hover: true, tooltipDelay: 200, selectConnectedEdges: false, multiselect: true },
    manipulation: {
        enabled: true, addNode: false,
        addEdge: async function(edgeData, callback) {
            stopLinkingUI();
            if (edgeData.from === edgeData.to) return callback(null);
            try {
                const res = await fetch('/api/infra/dependencies', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ sourceId: edgeData.from, targetId: edgeData.to }) });
                if (res.ok) callback(edgeData); else callback(null);
            } catch(err) { callback(null); }
        }
    }
};

async function loadGraph() {
    try {
        const response = await fetch('/api/infra/topology');
        const data = await response.json();

        const uniqueClusters = {};
        appState.availableTeams = [];
        const hubNodes = []; const hubEdges = [];

        document.getElementById('note-cluster-select').innerHTML = '<option value="" disabled selected>Select a cluster...</option>';
        document.getElementById('worker-team-select').innerHTML = '<option value="" disabled selected>Select Team...</option>';
        document.getElementById('assign-team-select').innerHTML = '<option value="" disabled selected>Select Team...</option>';

        data.nodes.forEach(n => {
            if (n.type === 'NOTE') {
                n.shape = 'box'; n.font = { multi: 'html', color: '#ffffff', size: 12, face: 'Inter' };
                n.label = `<b>${n.title}</b>\n${n.text}`; n.color = { background: n.color + '25', border: n.color, highlight: { border: '#ffffff' } };
                n.borderWidth = 1.5; n.shadow = { enabled: true, color: n.color + '40', size: 10 };
            }
            else if (n.type === 'TEAM') {
                n.shape = 'icon'; n.icon = { face: '"Font Awesome 6 Free"', weight: "900", size: 50, code: '\uf0c0', color: '#a855f7' };
                appState.availableTeams.push({id: n.id, name: n.label});
            }
            else if (n.type === 'WORKER') {
                n.shape = 'icon'; n.icon = { face: '"Font Awesome 6 Free"', weight: "900", size: 30, code: '\uf007', color: '#ec4899' };
            }
            else {
                n.shape = 'icon'; n.icon = { face: '"Font Awesome 6 Free"', weight: "900", size: 35, code: '\uf013', color: '#71717a' };
                const lang = (n.group || '').toLowerCase();
                if (lang.includes('java')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf4e4'; n.icon.color = '#fb923c'; }
                else if (lang.includes('python')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf3e2'; n.icon.color = '#fbbf24'; }
                else if (lang.includes('node')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf3d3'; n.icon.color = '#34d399'; }
                else if (lang.includes('go')) { n.icon.code = '\uf233'; n.icon.color = '#38bdf8'; }
                else if (lang.includes('postgres') || lang.includes('mysql') || lang.includes('database')) { n.icon.code = '\uf1c0'; n.icon.color = '#60a5fa'; }
                else if (lang.includes('redis') || lang.includes('cache')) { n.icon.code = '\uf0e7'; n.icon.color = '#f87171'; }
            }

            if (n.clusterId) {
                if (!uniqueClusters[n.clusterId]) {
                    uniqueClusters[n.clusterId] = n.clusterName;
                    document.getElementById('note-cluster-select').innerHTML += `<option value="${n.clusterId}">${n.clusterName}</option>`;
                    hubNodes.push({ id: 'hub_' + n.clusterId, shape: 'dot', size: 0, color: 'rgba(0,0,0,0)', label: '', physics: true });
                }
                hubEdges.push({ id: 'edge_hub_' + n.id, from: n.id, to: 'hub_' + n.clusterId, color: { color: 'rgba(0,0,0,0)', highlight: 'rgba(0,0,0,0)', hover: 'rgba(0,0,0,0)' }, length: 50, physics: true, arrows: '' });
            }
        });

        appState.availableTeams.forEach(t => {
            document.getElementById('worker-team-select').innerHTML += `<option value="${t.id}">${t.name}</option>`;
            document.getElementById('assign-team-select').innerHTML += `<option value="${t.id}">${t.name}</option>`;
        });

        data.edges.forEach(e => {
            if (e.type === 'RELATES_TO') { e.dashes = [4, 4]; e.arrows = ''; e.color = { color: '#a1a1aa' }; e.width = 1; }
            if (e.type === 'MAINTAINED_BY') { e.dashes = [6, 6]; e.color = { color: '#a855f7' }; e.width = 2; }
            if (e.type === 'WORKS_IN') { e.arrows = 'to'; e.color = { color: '#ec4899' }; e.width = 1; }
        });

        appState.nodesDataset = new vis.DataSet([...data.nodes, ...hubNodes]);
        appState.edgesDataset = new vis.DataSet([...data.edges, ...hubEdges]);

        const container = document.getElementById('mynetwork');
        if (appState.network) appState.network.destroy();
        appState.network = new vis.Network(container, { nodes: appState.nodesDataset, edges: appState.edgesDataset }, graphOptions);

        appState.network.on("beforeDrawing", function (ctx) {
            const clusters = {};
            appState.nodesDataset.forEach(node => {
                if (node.clusterId && !String(node.id).startsWith('hub_')) {
                    if (!clusters[node.clusterId]) clusters[node.clusterId] = { name: node.clusterName, color: node.clusterColor, nodeIds: [] };
                    clusters[node.clusterId].nodeIds.push(node.id);
                }
            });
            for (const clusterId in clusters) {
                const cluster = clusters[clusterId];
                if (cluster.nodeIds.length === 0) continue;
                const positions = appState.network.getPositions(cluster.nodeIds);
                let minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity;
                let hasPositions = false;
                for (const id in positions) {
                    const pos = positions[id]; if (pos.x === undefined) continue; hasPositions = true;
                    const padding = 55;
                    if (pos.x - padding < minX) minX = pos.x - padding; if (pos.x + padding > maxX) maxX = pos.x + padding;
                    if (pos.y - padding < minY) minY = pos.y - padding; if (pos.y + padding > maxY) maxY = pos.y + padding;
                }
                if (!hasPositions) continue;
                const hex = cluster.color.replace('#', '');
                const r = parseInt(hex.substring(0, 2), 16) || 100, g = parseInt(hex.substring(2, 4), 16) || 100, b = parseInt(hex.substring(4, 6), 16) || 100;
                ctx.fillStyle = `rgba(${r}, ${g}, ${b}, 0.08)`; ctx.strokeStyle = `rgba(${r}, ${g}, ${b}, 0.6)`; ctx.lineWidth = 2; ctx.setLineDash([6, 6]);
                ctx.beginPath(); if (ctx.roundRect) { ctx.roundRect(minX, minY, maxX - minX, maxY - minY, 16); } else { ctx.rect(minX, minY, maxX - minX, maxY - minY); }
                ctx.fill(); ctx.stroke(); ctx.setLineDash([]);
                ctx.font = "600 12px Inter, sans-serif"; const textWidth = ctx.measureText(cluster.name).width;
                ctx.fillStyle = `rgba(${r}, ${g}, ${b}, 0.85)`; ctx.beginPath();
                if (ctx.roundRect) { ctx.roundRect(minX, minY - 24, textWidth + 24, 24, [10, 10, 0, 0]); } else { ctx.fillRect(minX, minY - 24, textWidth + 24, 24); }
                ctx.fill(); ctx.fillStyle = "#ffffff"; ctx.fillText(cluster.name, minX + 12, minY - 8);
            }
        });

        appState.network.on("click", function (params) {
            closeAllPanels();
            const realNodes = params.nodes.filter(id => !String(id).startsWith('hub_'));

            if (realNodes.length > 1) {
                document.getElementById('cluster-action-panel').classList.remove('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
            }
            else if (realNodes.length === 1) {
                appState.selectedNodeId = realNodes[0];
                const node = appState.nodesDataset.get(appState.selectedNodeId);

                let content = '';
                const assignSection = document.getElementById('assign-team-section');
                assignSection.classList.add('hidden');

                if (node.type === 'NOTE') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Sticky Note</div><div class="text-base font-bold" style="color: ${node.color}">${node.title}</div>`;
                } else if (node.type === 'TEAM') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Team</div><div class="text-base text-zinc-100 font-bold">${node.label}</div>`;
                } else if (node.type === 'WORKER') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Worker</div><div class="text-base text-zinc-100 font-bold">${node.label}</div><div class="text-xs text-pink-300 mt-1">${node.group}</div>`;
                } else {
                    assignSection.classList.remove('hidden');
                    let clusterBadge = node.clusterName ? `<div class="mt-2 text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Cluster</div><div class="text-xs text-white bg-black/40 px-2 py-1 rounded w-max border border-white/10" style="border-left: 3px solid ${node.clusterColor}">${node.clusterName}</div>` : '';
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Service</div><div class="text-base text-zinc-100 font-bold">${node.label}</div>${clusterBadge}`;
                }

                document.getElementById('node-info-content').innerHTML = content;
                document.getElementById('action-panel').classList.remove('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
                document.getElementById('blast-target').value = node.id;
            }
            else if (params.edges.length === 1 && !String(params.edges[0]).startsWith('edge_hub_')) {
                document.getElementById('edge-action-panel').classList.remove('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
                appState.selectedEdgeId = params.edges[0];
            }
        });
    } catch (error) { console.error(error); }
}

window.onload = loadGraph;