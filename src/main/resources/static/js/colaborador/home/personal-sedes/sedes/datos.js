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
    await actualizarSedesActivasGlobal(); // Variable global y columnas LISTAS
    inicializarCalendario();              // Eventos y primer render
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
