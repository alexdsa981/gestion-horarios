//COLABORADORES
document.addEventListener('DOMContentLoaded', () => {
    cargarColaboradoresSelector();
});

function cargarSelectColaboradoresActivos(selectId) {
    const select = document.getElementById(selectId);
    if (!select || !window.listaColaboradoresPorAgrupacion) return;

    // Solo colaboradores activos
    const colaboradores = window.listaColaboradoresPorAgrupacion.filter(c => c.isActive);

    // Limpiar opciones previas
    select.innerHTML = "";

    // Agregar opciones
    colaboradores.forEach((colaborador, idx) => {
        const option = document.createElement("option");
        option.value = colaborador.id;
        option.textContent = colaborador.nombreCompleto;
        option.setAttribute("data-color", colaborador.color);
        // Opcional: estilos para texto largo
        option.style.textOverflow = "ellipsis";
        option.style.whiteSpace = "nowrap";
        option.style.overflow = "hidden";
        if(idx === 0) option.selected = true; // Selecciona el primero por defecto
        select.appendChild(option);
    });
}

//SEDES

document.addEventListener('DOMContentLoaded', () => {
    actualizarSedesActivasGlobal();
});

function cargarSelectSedesActivas(selectId) {
    const select = document.getElementById(selectId);
    if (!select || !window.listaSedesActivasPorAgrupacion) return;

    // Solo sedes activas
    const sedes = window.listaSedesActivasPorAgrupacion.filter(s => s.isActive);

    // Limpiar opciones previas
    select.innerHTML = "";

    // Agregar opciones
    sedes.forEach((sede, idx) => {
        const option = document.createElement("option");
        option.value = sede.id;
        option.textContent = sede.nombre;
        // Puedes agregar más atributos si necesitas
        if(idx === 0) option.selected = true; // Selecciona el primero por defecto
        select.appendChild(option);
    });
}

