const nombresMeses = [
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
];
const nombresDiasSemana = [
    "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"
];

function getDaysInMonth(year, month) {
    let days = [];
    let total = new Date(year, month + 1, 0).getDate();
    for (let d = 1; d <= total; d++) {
        days.push(`${year}-${(month + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}`);
    }
    return days;
}

async function obtenerBloquesHorarios(agrupacionGlobalId, desde, hasta) {
    // Si no se pasan fechas, usar el mes actual
    if (!desde || !hasta) {
        const hoy = new Date();
        const year = hoy.getFullYear();
        const month = hoy.getMonth() + 1; // Enero = 1

        desde = `${year}-${month.toString().padStart(2, '0')}-01`;
        const ultimoDia = new Date(year, month, 0).getDate();
        hasta = `${year}-${month.toString().padStart(2, '0')}-${ultimoDia.toString().padStart(2, '0')}`;
    }

    const url = `/app/bloque-horarios/listar-fecha/${agrupacionGlobalId}?desde=${desde}&hasta=${hasta}`;

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error("No se pudieron obtener los bloques horarios");
        return await response.json();
    } catch (error) {
        console.error("Error al obtener bloques horarios:", error);
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message
        });
        return [];
    }
}



function forzarAnchoRowHeader() {
    document.querySelectorAll('.calendar_default_rowheader').forEach(td => {
        const table = td.closest('table');
        if (table) {
            table.style.width = "20px";
            table.style.minWidth = "20px";
            table.style.maxWidth = "20px";
            table.style.tableLayout = "fixed";
        }
    });
}


function eliminarTrSoloConCellSimple() {

}