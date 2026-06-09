const graphOptions = {
    edges: { width: 1.5, color: { color: '#52525b', highlight: '#818cf8' }, arrows: { to: { enabled: true, scaleFactor: 0.5 } }, smooth: { type: 'continuous', roundness: 0.5 } },
    physics: {
        forceAtlas2Based: { gravitationalConstant: -70, centralGravity: 0.005, springLength: 150 },
        solver: 'forceAtlas2Based', timestep: 0.3
    },
    interaction: { hover: true, tooltipDelay: 200, selectConnectedEdges: false, multiselect: true },
    manipulation: {
        enabled: true, addNode: false,
        addEdge: async function(edgeData, callback) {
            stopLinkingUI();
            if (edgeData.from === edgeData.to) return callback(null);
            try {
                const res = await fetch('/api/infra/dependencies', {
                    method: 'POST',
                    headers: getAuthHeaders(),
                    body: JSON.stringify({ sourceId: edgeData.from, targetId: edgeData.to })
                });
                if (res.ok) callback(edgeData); else callback(null);
            } catch(err) { callback(null); }
        }
    }
};

async function loadGraph() {
    if (!appState.currentProjectId) {
        showProjectSelector();
        return;
    }

    try {
        const token = localStorage.getItem('jwt');
        if (!token) return;

        const response = await fetch('/api/infra/topology', {
            headers: getAuthHeaders()
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        const data = await response.json();

        const uniqueClusters = {};
        const clusterSelect = document.getElementById('note-cluster-select');
        clusterSelect.innerHTML = '<option value="" disabled selected>Select a cluster...</option>';

        const hubNodes = [];
        const hubEdges = [];
        appState.availableTeams = [];

        data.nodes.forEach(n => {
            if (n.type === 'NOTE') {
                n.shape = 'box';
                n.font = { multi: 'html', color: '#ffffff', size: 12, face: 'Inter' };
                n.label = `<b>${n.title}</b>\n${n.text}`;
                n.color = { background: n.color + '25', border: n.color, highlight: { border: '#ffffff' } };
                n.borderWidth = 1.5;
                n.shadow = { enabled: true, color: n.color + '40', size: 10 };
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
                else if (lang.includes('csharp') || lang.includes('.net')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf17a'; n.icon.color = '#818cf8'; }
                else if (lang.includes('php')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf457'; n.icon.color = '#a78bfa'; }
                else if (lang.includes('ruby')) { n.icon.code = '\uf3a5'; n.icon.color = '#f43f5e'; }
                else if (lang.includes('rust')) { n.icon.code = '\uf013'; n.icon.color = '#f97316'; }
                else if (lang.includes('postgres') || lang.includes('mysql') || lang.includes('database')) { n.icon.code = '\uf1c0'; n.icon.color = '#60a5fa'; }
                else if (lang.includes('mongo')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf4fa'; n.icon.color = '#4ade80'; }
                else if (lang.includes('redis') || lang.includes('cache')) { n.icon.code = '\uf0e7'; n.icon.color = '#f87171'; }
                else if (lang.includes('neo4j')) { n.icon.code = '\uf542'; n.icon.color = '#60a5fa'; }
                else if (lang.includes('kafka') || lang.includes('rabbit') || lang.includes('broker')) { n.icon.code = '\uf6ff'; n.icon.color = '#c084fc'; }
                else if (lang.includes('kubernetes')) { n.icon.code = '\uf655'; n.icon.color = '#3b82f6'; }
                else if (lang.includes('docker')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf395'; n.icon.color = '#3b82f6'; }
                else if (lang.includes('aws') || lang.includes('storage')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf375'; n.icon.color = '#fb923c'; }
                else if (lang.includes('gateway') || lang.includes('proxy')) { n.icon.code = '\uf362'; n.icon.color = '#2dd4bf'; }
                else if (lang.includes('react')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf41b'; n.icon.color = '#22d3ee'; }
                else if (lang.includes('vue')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf41f'; n.icon.color = '#34d399'; }
                else if (lang.includes('angular')) { n.icon.face = '"Font Awesome 6 Brands"'; n.icon.code = '\uf420'; n.icon.color = '#e11d48'; }
                else if (lang.includes('mobile')) { n.icon.code = '\uf3ce'; n.icon.color = '#a78bfa'; }
                else if (lang.includes('frontend')) { n.icon.code = '\uf108'; n.icon.color = '#22d3ee'; }
            }

            if (n.clusterId) {
                if (!uniqueClusters[n.clusterId]) {
                    uniqueClusters[n.clusterId] = n.clusterName;
                    hubNodes.push({ id: 'hub_' + n.clusterId, shape: 'dot', size: 0, color: 'rgba(0,0,0,0)', label: '', physics: true });
                }
                hubEdges.push({ id: 'edge_hub_' + n.id, from: n.id, to: 'hub_' + n.clusterId, color: { color: 'rgba(0,0,0,0)', highlight: 'rgba(0,0,0,0)', hover: 'rgba(0,0,0,0)' }, length: 50, physics: true, arrows: '' });
            }
        });

        const teamOptions = appState.availableTeams.map(n => ({ id: n.id, label: n.name }));
        if(typeof updateSelectOptions === 'function') {
            updateSelectOptions('wrapper-worker-team', teamOptions);
            updateSelectOptions('wrapper-assign-team', teamOptions);
            const clusterOptions = Object.keys(uniqueClusters).map(id => ({ id: id, label: '📦 ' + uniqueClusters[id] }));
            updateSelectOptions('wrapper-note-cluster', clusterOptions);
        }

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
                    const pos = positions[id];
                    if (pos.x === undefined) continue;
                    hasPositions = true;
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
                ctx.fill();
                ctx.fillStyle = "#ffffff"; ctx.fillText(cluster.name, minX + 12, minY - 8);
            }
        });

        appState.network.on("click", function (params) {
            closeAllPanels();
            const realSelectedNodes = params.nodes.filter(id => !String(id).startsWith('hub_'));

            if (realSelectedNodes.length > 1) {
                document.getElementById('cluster-action-panel').classList.remove('opacity-0', 'invisible', 'translate-y-4', 'pointer-events-none');
            }
            else if (realSelectedNodes.length === 1) {
                appState.selectedNodeId = realSelectedNodes[0];
                const node = appState.nodesDataset.get(appState.selectedNodeId);

                let content = '';
                const assignSection = document.getElementById('assign-team-section');
                if(assignSection) assignSection.classList.add('hidden');

                if (node.type === 'NOTE') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Sticky Note</div><div class="text-base font-bold" style="color: ${node.color}">${node.title}</div>`;
                } else if (node.type === 'TEAM') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Team</div><div class="text-base text-zinc-100 font-bold">${node.label}</div>`;
                } else if (node.type === 'WORKER') {
                    content = `<div class="text-[10px] text-zinc-500 uppercase tracking-widest mb-1">Worker</div><div class="text-base text-zinc-100 font-bold">${node.label}</div><div class="text-xs text-pink-300 mt-1">${node.group}</div>`;
                } else {
                    if(assignSection) assignSection.classList.remove('hidden');
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

function exportPng() {
    if (!appState.network) return alert('Load a graph first');

    appState.network.redraw();

    let canvas;
    const container = document.getElementById('mynetwork');

    if (appState.network.body && appState.network.body.canvas && appState.network.body.canvas.canvas) {
        canvas = appState.network.body.canvas.canvas;
    } else if (container) {
        const allC = container.querySelectorAll('canvas');
        if (allC.length > 0) canvas = allC[allC.length - 1];
    }
    if (!canvas || canvas.width === 0 || canvas.height === 0) {
        return alert('Canvas not available - draw something on the graph first');
    }

    const exportCanvas = document.createElement('canvas');
    exportCanvas.width = canvas.width;
    exportCanvas.height = canvas.height;
    const ctx = exportCanvas.getContext('2d');

    const bgColor = getComputedStyle(document.body).backgroundColor || '#09090b';
    ctx.fillStyle = bgColor;
    ctx.fillRect(0, 0, exportCanvas.width, exportCanvas.height);

    ctx.drawImage(canvas, 0, 0);

    const link = document.createElement('a');
    link.download = 'blast-radius-' + (appState.currentProjectName || 'graph') + '.png';
    link.href = exportCanvas.toDataURL('image/png');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

window.onload = () => {
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
        const savedProjectId = localStorage.getItem('currentProjectId');
        const savedProjectName = localStorage.getItem('currentProjectName');
        if (savedProjectId) {
            appState.currentProjectId = savedProjectId;
            appState.currentProjectName = savedProjectName;
            loadGraph();
        } else {
            showProjectSelector();
        }
    }
};
