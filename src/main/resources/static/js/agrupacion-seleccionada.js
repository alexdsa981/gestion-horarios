let agrupacionGlobalId = document.getElementById('agrupacion-select').value;

document.getElementById('agrupacion-select').addEventListener('change', async function() {
    agrupacionGlobalId = this.value;
    await fetch('/actualizar-agrupacion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'agrupacionId=' + encodeURIComponent(agrupacionGlobalId)
    });
    window.location.reload();
});