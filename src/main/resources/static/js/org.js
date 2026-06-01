async function createTeam() {
    const name = document.getElementById('team-name').value;
    if (!name) return alert("Enter team name");
    await fetch('/create-team/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: name })
    });
    document.getElementById('team-name').value = '';
    loadGraph();
}

async function createWorker() {
    const name = document.getElementById('worker-name').value;
    const role = document.getElementById('worker-role').value;
    const teamId = document.getElementById('worker-team-select').value;
    if (!name || !teamId) return alert("Enter name and select a team!");
    await fetch('/api/workers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, role, teamId })
    });
    document.getElementById('worker-name').value = '';
    document.getElementById('worker-role').value = '';
    loadGraph();
}

async function assignTeamToService() {
    const teamId = document.getElementById('assign-team-select').value;
    if (!appState.selectedNodeId || !teamId) return alert("Select a team to assign!");
    await fetch(`/api/infra/services/${appState.selectedNodeId}/assign-team/${teamId}`, { method: 'POST' });
    closeAllPanels(); loadGraph();
}