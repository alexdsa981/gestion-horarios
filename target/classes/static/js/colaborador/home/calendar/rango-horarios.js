let businessBeginsHour = 7;
let businessEndsHour = 20;
async function fetchRangoHorario(idAgrupacion) {
    try {
        const response = await fetch(`/app/rango-horas/${idAgrupacion}`);
        if (response.ok) {
            const data = await response.json();
            businessBeginsHour = data.rangoInicio;
            businessEndsHour = data.rangoFin;

        } else {
            console.warn("No se encontró rango horario para la agrupación", idAgrupacion);
        }
    } catch (error) {
        console.error("Error al obtener rango horario:", error);
    }
}
