async function createTeam() {
    if (!requireProject()) return;
    const name = document.getElementById('team-name').value;
    if (!name) return alert("Enter team name");
    try {
        const res = await fetch('/api/teams', { method: 'POST', headers: getAuthHeaders(), body: JSON.stringify({ name: name }) });
        if (!res.ok) return alert('Failed to create team');
        document.getElementById('team-name').value = ''; loadGraph();
    } catch (e) { alert('Cannot connect to server.'); }
}

async function createWorker() {
    if (!requireProject()) return;
    const name = document.getElementById('worker-name').value;
    const role = document.getElementById('worker-role').value;
    const teamId = document.getElementById('worker-team-select').value;
    if (!name || !teamId) return alert("Enter name and select a team!");
    try {
        const res = await fetch('/api/workers', { method: 'POST', headers: getAuthHeaders(), body: JSON.stringify({ name, role, teamId }) });
        if (!res.ok) return alert('Failed to hire worker');
        document.getElementById('worker-name').value = ''; document.getElementById('worker-role').value = ''; loadGraph();
    } catch (e) { alert('Cannot connect to server.'); }
}

async function assignTeamToService() {
    if (!requireProject()) return;
    const teamId = document.getElementById('assign-team-select').value;
    if (!appState.selectedNodeId || !teamId) return alert("Select a team to assign!");
    try {
        const res = await fetch(`/api/infra/services/${appState.selectedNodeId}/assign-team/${teamId}`, { method: 'POST', headers: getAuthHeaders() });
        if (!res.ok) return alert('Failed to assign team');
        closeAllPanels(); loadGraph();
    } catch (e) { alert('Cannot connect to server.'); }
}
