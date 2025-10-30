let businessBeginsHour = 7;
let businessEndsHour = 20;
async function fetchRangoHorario(idAgrupacion) {
    try {
        const response = await fetch(`/app/rango-horas/${idAgrupacion}`);
        if (response.ok) {
            const data = await response.json();
            businessBeginsHour = data.rangoInicio;
            businessEndsHour = data.rangoFin;
            // Asigna el valor real a los inputs
            document.getElementById('horaInicio').value = data.rangoInicio;
            document.getElementById('horaFin').value = data.rangoFin;
            validarTurnoNoche();
        } else {
            console.warn("No se encontró rango horario para la agrupación", idAgrupacion);
        }
    } catch (error) {
        console.error("Error al obtener rango horario:", error);
    }
}

async function actualizarRangoHorario() {
    const horaInicio = document.getElementById('horaInicio').value;
    const horaFin = document.getElementById('horaFin').value;
    const idAgrupacion = window.idAgrupacion; // O como obtengas el id

    const response = await fetch(`/app/rango-horas/${agrupacionGlobalId}/actualizar?horaInicio=${horaInicio}&horaFin=${horaFin}`, {
        method: 'POST'
    });
    if (response.ok) {
        const data = await response.json();
        // Actualiza variables globales
        businessBeginsHour = data.rangoInicio;
        businessEndsHour = data.rangoFin;
        validarTurnoNoche();
        renderizarCalendarioActual();

    } else {
        alert("No se pudo actualizar el rango horario.");
    }
}