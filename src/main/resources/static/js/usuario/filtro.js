document.querySelectorAll('#filtroNombre, #filtroSpring, #filtroRol').forEach(el => {
    el.addEventListener('input', filtrarTablas);
    el.addEventListener('change', filtrarTablas);
});

function limpiarFiltros() {
    document.getElementById('filtroNombre').value = '';
    document.getElementById('filtroSpring').value = '';
    document.getElementById('filtroRol').value = '';
    filtrarTablas();
}

function filtrarTablas() {
    const nombre = document.getElementById('filtroNombre').value.toLowerCase();
    const spring = document.getElementById('filtroSpring').value;
    const rol = document.getElementById('filtroRol').value;

    filtrarTabla(document.querySelectorAll('#filtroUsuarios ~ .card')[0], nombre, spring, rol);
}

function filtrarTabla(card, nombre, spring, rol) {
    const filas = card.querySelectorAll('tbody tr');

    filas.forEach(fila => {
        const nombreUsuario = fila.children[3].textContent.toLowerCase();
        const springUsuario = fila.children[2].textContent.trim();
        const rolUsuario = fila.children[4].textContent.trim();

        const coincideNombre = nombre === '' || nombreUsuario.includes(nombre);
        const coincideSpring = spring === '' || springUsuario === spring;
        const coincideRol = rol === '' || rolUsuario === rol;

        fila.style.display = (coincideNombre && coincideSpring && coincideRol) ? '' : 'none';
    });
}
