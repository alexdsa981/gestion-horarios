// --- Variables globales ---
window.listaSedesActivasPorAgrupacion = []; // Sedes activas (para columnas y calendario)
window.columnas = []; // Columnas del calendario

// --- Utilidad para actualizar columnas ---
function actualizarColumnas() {
    window.columnas = window.listaSedesActivasPorAgrupacion.map(sede => ({
        name: sede.nombre,
        id: sede.id
    }));
    //console.log("[COLUMNAS] Actualizadas:", window.columnas);
}

// --- Fetch para sedes activas y actualizar columnas ---
async function actualizarSedesActivasGlobal() {
    try {
        const response = await fetch("/app/sedes/listar/activos/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener sedes activas');
        const sedes = await response.json();
        window.listaSedesActivasPorAgrupacion = sedes;
        actualizarColumnas();
        //console.log("[SEDES ACTIVAS] Variable global actualizada:", sedes);
        cargarSelectSedesActivas("edit-sede");
        cargarSelectSedesActivas("modal-sedes-select");
    } catch (error) {
        console.error("[SEDES ACTIVAS] No se pudieron cargar las sedes activas", error);
        window.listaSedesActivasPorAgrupacion = [];
        window.columnas = [];
    }
}

// --- Renderizar calendario siempre usando columnas actualizadas ---
function renderizarCalendarioActual() {
    const selectorAno = document.getElementById("selectorAno");
    const selectorMes = document.getElementById("selectorMes");
    const ano = Number(selectorAno.value);
    const mes = Number(selectorMes.value);
    const {desde, hasta} = getFechaRango(ano, mes);
    renderizarMesGrid(desde, hasta, ano, mes, window.columnas);
}

// --- Inicializar todo al cargar la página ---
document.addEventListener("DOMContentLoaded", async () => {
    await actualizarSedesActivasGlobal();
    inicializarCalendario();
});

// --- Inicializar calendario: eventos selectores y primer render ---
function inicializarCalendario() {
    generarOpcionesAno();
    generarOpcionesMes();


    const selectorAno = document.getElementById("selectorAno");
    const selectorMes = document.getElementById("selectorMes");

    renderizarCalendarioActual(); // Render inicial

    selectorAno.addEventListener("change", () => {
        renderizarCalendarioActual();
    });
    selectorMes.addEventListener("change", () => {
        renderizarCalendarioActual();
    });
}

// --- Renderizar tabla de sedes (todas) ---
document.getElementById('dropdownSedesBtn').addEventListener('click', cargarSedesTabla);

async function cargarSedesTabla() {
    const cuerpoTabla = document.getElementById('tablaSedesBody');
    try {
        const response = await fetch("/app/sedes/listar/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener todas las sedes');
        const sedes = await response.json();
        cuerpoTabla.innerHTML = '';
        sedes.forEach((sede, index) => {
            const fila = `
              <tr>
                  <td>${sede.nombre}</td>
                  <td class="text-center">
                      <div class="form-check form-switch d-inline-flex align-items-center justify-content-center mb-0">
                          <input type="checkbox" class="form-check-input chk-estado" id="sedeSwitch${sede.id}" data-id="${sede.id}" ${sede.isActive ? 'checked' : ''}>
                          <label class="form-check-label ms-2 small fw-bold" for="sedeSwitch${sede.id}" style="min-width:60px;">
                            ${sede.isActive ? 'Visible' : 'No visible'}
                          </label>
                      </div>
                  </td>
              </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Evento para cambiar estado
        cuerpoTabla.querySelectorAll('.chk-estado').forEach(chk => {
            chk.addEventListener('change', async function() {
                const sedeId = this.getAttribute('data-id');
                const nuevoEstado = this.checked;
                this.nextElementSibling.textContent = nuevoEstado ? 'Visible' : 'No visible';
                try {
                    const res = await fetch("/app/sedes/estado/" + agrupacionGlobalId + "/" + sedeId, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(nuevoEstado)
                    });
                    if (!res.ok) throw new Error('No se pudo actualizar el estado');

                    // 1. Refresca la variable global de sedes activas y columnas
                    await actualizarSedesActivasGlobal();

                    // 2. Vuelve a renderizar el calendario con las nuevas columnas
                    renderizarCalendarioActual();

                } catch (error) {
                    await Swal.fire({
                        icon: 'error',
                        title: 'Error actualizando estado',
                        text: 'No se pudo actualizar el estado de la sede.'
                    });
                    this.checked = !nuevoEstado;
                    this.nextElementSibling.textContent = !nuevoEstado ? 'Visible' : 'No visible';
                }
            });
        });
    } catch (error) {
        cuerpoTabla.innerHTML = `
            <tr><td colspan="3" class="text-center text-danger">Error al cargar las sedes</td></tr>
        `;
        console.error("[SEDES] Error al cargar todas las sedes", error);
    }

}