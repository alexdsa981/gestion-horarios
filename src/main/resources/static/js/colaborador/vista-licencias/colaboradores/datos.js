// Asegúrate que este archivo se incluya DESPUÉS de que el fragmento esté en el DOM
window.listaColaboradoresPorAgrupacion = [];

// Espera al DOM y ejecuta la función
document.addEventListener("DOMContentLoaded", () => {
    // Si agrupacionGlobalId está definido, carga el selector
    if (typeof agrupacionGlobalId !== "undefined" && agrupacionGlobalId !== null) {
        cargarColaboradoresSelector();
    } else {
        console.error("agrupacionGlobalId no está definido.");
    }
});

async function cargarColaboradoresSelector() {
    const cuerpoTabla = document.getElementById('tablaColaboradoresBody');
    if (!cuerpoTabla) {
        console.warn("No se encontró el tbody con id 'tablaColaboradoresBody'");
        return;
    }

    try {
        // Valida agrupacionGlobalId antes de fetch
        if (typeof agrupacionGlobalId === "undefined" || agrupacionGlobalId === null) {
            throw new Error('agrupacionGlobalId no está definido');
        }

        const response = await fetch("/app/colaboradores/agrupacion/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener colaboradores');

        const colaboradores = await response.json();
        console.log("Colaboradores recibidos:", colaboradores);

        window.listaColaboradoresPorAgrupacion = colaboradores;

        cuerpoTabla.innerHTML = '';

        if (!Array.isArray(colaboradores) || colaboradores.length === 0) {
            cuerpoTabla.innerHTML = `
                <tr><td colspan="1" class="text-center text-muted">No hay colaboradores</td></tr>
            `;
            return;
        }

        // Crea dinámicamente los <tr> y los inserta
        colaboradores.forEach((colaborador) => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td>
                    <span class="d-flex align-items-center gap-2">
                        <span style="background:${colaborador.color};width:1.2em;height:1.2em;display:inline-block;border-radius:50%;border:1px solid #ccc"></span>
                        ${colaborador.nombreCompleto}
                    </span>
                </td>
            `;
            fila.style.cursor = "pointer";
            fila.addEventListener('click', () => {
                mostrarVacacionesColaborador(colaborador.id, colaborador.nombreCompleto);
            });
            cuerpoTabla.appendChild(fila);
        });
    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `
            <tr><td colspan="1" class="text-center text-danger">Error al cargar colaboradores</td></tr>
        `;
    }
}