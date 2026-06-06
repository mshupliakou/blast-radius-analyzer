async function createTeam() {
    const name = document.getElementById('team-name').value;
    if (!name.trim()) return alert("Enter team name");
    try {
        const res = await fetch('/create-team/', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name })
        });
        if (!res.ok) throw new Error(res.statusText);
        document.getElementById('team-name').value = '';
        loadGraph();
    } catch(e) { alert("Failed to create team: " + e.message); }
}

async function createWorker() {
    const name = document.getElementById('worker-name').value;
    const role = document.getElementById('worker-role').value;
    const teamId = document.getElementById('worker-team-select').value;
    if (!name.trim() || !teamId) return alert("Enter name and select a team!");
    try {
        const res = await fetch('/api/workers', {
            method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, role, teamId })
        });
        if (!res.ok) throw new Error(res.statusText);
        document.getElementById('worker-name').value = ''; document.getElementById('worker-role').value = '';
        loadGraph();
    } catch(e) { alert("Failed to hire worker: " + e.message); }
}

async function assignTeamToService() {
    const teamId = document.getElementById('assign-team-select').value;
    if (!appState.selectedNodeId || !teamId) return alert("Select a team to assign!");
    try {
        const res = await fetch(`/api/infra/services/${appState.selectedNodeId}/assign-team/${teamId}`, { method: 'POST' });
        if (!res.ok) throw new Error(res.statusText);
        closeAllPanels(); loadGraph();
    } catch(e) { alert("Failed to assign team: " + e.message); }
}