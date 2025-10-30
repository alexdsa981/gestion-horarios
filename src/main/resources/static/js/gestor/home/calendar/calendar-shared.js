// --- Destaca horas laborales en el calendario (para fines visuales) ---
function marcarHorasLaborales(args, beginsHour, endsHour) {
    const hour = args.cell.start.getHours();
    const isWorkingHour = hour >= beginsHour && hour < endsHour;
    if ([0, 6].includes(args.cell.start.dayOfWeek()) && isWorkingHour) {
        args.cell.properties.business = true;
    }
}

// --- Handlers para edición de eventos (drag, resize, click derecho) ---
function crearHandlersEdicion(calendar, getRightPanelId = null) {
    let lastEventState = {};

    return {
        onEventResized: function (args) {
            if (getRightPanelId) {
                const panel = document.getElementById(getRightPanelId());
                if (panel) panel.style.display = "none";
            }
            const id = args.e.data.id;
            const idColaborador = args.e.data.idColaborador;
            const idSede = args.e.data.resource;
            const idAgrupacion = args.e.data.idAgrupacion;
            const fecha = args.newStart.toString("yyyy-MM-dd");
            const horaInicio = args.newStart.toString("HH:mm");
            const horaFin = args.newEnd.toString("HH:mm");

            const dto = {
                idColaborador: Number(idColaborador),
                idSede: Number(idSede),
                idAgrupacion: idAgrupacion ? Number(idAgrupacion) : undefined,
                fecha: fecha,
                horaInicio: horaInicio,
                horaFin: horaFin
            };

            fetch(`/app/bloque-horarios/editar/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            })
            .then(response => {
                if (!response.ok) throw new Error("Error al guardar cambio de tamaño en el servidor");
                return response.json();
            })
            .then(data => {
                actualizarEventoEnCalendario(args.e, data, calendar);

                // Solo refresca si el evento cambió de día (muy raro en resize)
                if (args.e.start().toString().substring(0, 10) !== data.fecha && window.refrescarMiniCalendario) {
                    window.refrescarMiniCalendario(data.fecha);
                }

                Swal.fire({
                    icon: "success",
                    title: "Horario ajustado",
                    timer: 1000,
                    showConfirmButton: false
                });
            })
            .catch(error => {
                revertirEventoEnCalendario(args.e, lastEventState, calendar);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message
                });
            });
        },

        onEventMove: function (args) {
            if (getRightPanelId) {
                const panel = document.getElementById(getRightPanelId());
                if (panel) panel.style.display = "none";
            }
            lastEventState = estadoEvento(args.e);
        },

        onEventMoved: function (args) {
            const origenFecha = args.e.start().toString().substring(0, 10); // "YYYY-MM-DD"
            const destinoFecha = args.newStart.toString("yyyy-MM-dd");

            const day = destinoFecha;
            const workStart = new DayPilot.Date(`${day}T${String(calendar.businessBeginsHour).padStart(2, "0")}:00:00`);
            const workEnd = new DayPilot.Date(`${day}T${String(calendar.businessEndsHour).padStart(2, "0")}:00:00`);

            const isWorkingHour = args.newStart >= workStart && args.newEnd <= workEnd;

            if (!isWorkingHour) {
                revertirEventoEnCalendario(args.e, lastEventState, calendar);
                Swal.fire({
                    icon: "warning",
                    title: "Fuera de horario laboral",
                    text: `No puedes mover el evento fuera del horario de ${calendar.businessBeginsHour}:00 a ${calendar.businessEndsHour}:00.`,
                });
                return;
            }

            // Persistir movimiento en backend
            const id = args.e.data.id;
            const idColaborador = args.e.data.idColaborador;
            const idSede = args.e.data.resource;
            const idAgrupacion = args.e.data.idAgrupacion;
            const fecha = destinoFecha;
            const horaInicio = args.newStart.toString("HH:mm");
            const horaFin = args.newEnd.toString("HH:mm");

            const dto = {
                idColaborador: Number(idColaborador),
                idSede: Number(idSede),
                idAgrupacion: idAgrupacion ? Number(idAgrupacion) : undefined,
                fecha: fecha,
                horaInicio: horaInicio,
                horaFin: horaFin
            };

            fetch(`/app/bloque-horarios/editar/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            })
            .then(async response => {
                if (!response.ok) {
                    // Intenta leer el error personalizado
                    let mensaje = "Error al guardar movimiento en el servidor";
                    try {
                        const errorData = await response.json();
                        if (errorData && errorData.error) {
                            mensaje = errorData.error;
                        }
                    } catch {
                        // Si no es JSON, intenta como texto
                        try {
                            const textError = await response.text();
                            if (textError && textError.trim().length > 0) {
                                mensaje = textError;
                            }
                        } catch {}
                    }
                    throw new Error(mensaje);
                }
                return response.json();
            })
            .then(data => {
                actualizarEventoEnCalendario(args.e, data, calendar);

                // Solo refresca si el evento cambió de día
                if (origenFecha !== destinoFecha && window.refrescarMiniCalendario) {
                    window.refrescarMiniCalendario(origenFecha);
                    window.refrescarMiniCalendario(destinoFecha);
                }

                Swal.fire({
                    icon: "success",
                    title: "Horario actualizado",
                    timer: 1000,
                    showConfirmButton: false
                });
            })
            .catch(error => {
                revertirEventoEnCalendario(args.e, lastEventState, calendar);
                Swal.fire({
                    icon: "warning",
                    title: "Aviso",
                    text: error.message
                });
            });
        },

        onEventRightClick: function(args) {
            Swal.fire({
                title: "¿Eliminar este bloque?",
                icon: "warning",
                showCancelButton: true,
                confirmButtonText: "Sí, eliminar",
                cancelButtonText: "Cancelar"
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch(`/app/bloque-horarios/eliminar/${args.e.id()}`, {
                        method: "DELETE"
                    })
                    .then(response => {
                        if (!response.ok) throw new Error("No se pudo eliminar en el servidor");
                        calendar.events.remove(args.e.id());
                        Swal.fire({
                            title: "Eliminado",
                            icon: "success",
                            timer: 1200,
                            showConfirmButton: false
                        });
                        if (getRightPanelId) {
                            const panel = document.getElementById(getRightPanelId());
                            if (panel) panel.style.display = "none";
                        }
                    })
                    .catch(error => {
                        Swal.fire({
                            title: "Error",
                            text: error.message,
                            icon: "error"
                        });
                    });
                }
            });
        }
    };
}

// --- Helpers para manejar el estado del evento ---
function estadoEvento(e) {
    return {
        id: e.id(),
        start: e.start().toString(),
        end: e.end().toString(),
        resource: e.resource(),
        text: e.text(),
        backColor: e.data.backColor,
        idColaborador: e.data.idColaborador,
        grupoAnidado: e.data.grupoAnidado,
        horaInicioAlmuerzo: e.data.horaInicioAlmuerzo || null,
        horaFinAlmuerzo: e.data.horaFinAlmuerzo || null,
        turnoNoche: e.data.turnoNoche || false,
    };
}

function actualizarEventoEnCalendario(e, data, calendar) {
    e.data.text = data.nombreColaborador;
    e.data.start = `${data.fecha}T${data.horaInicio}`;
    e.data.end = `${data.fecha}T${data.horaFin}`;
    e.data.resource = data.idSede;
    e.data.backColor = data.color;
    e.data.idColaborador = data.idColaborador;
    e.data.grupoAnidado = data.grupoAnidado;
    e.data.horaInicioAlmuerzo = data.horaInicioAlmuerzo || null;
    e.data.horaFinAlmuerzo = data.horaFinAlmuerzo || null;
    e.data.turnoNoche = data.isTurnoNoche || false;
    calendar.events.update(e);
}

function revertirEventoEnCalendario(e, lastState, calendar) {
    calendar.events.update({
        id: lastState.id,
        start: lastState.start,
        end: lastState.end,
        resource: lastState.resource,
        text: lastState.text,
        backColor: lastState.backColor,
        idColaborador: lastState.idColaborador,
        grupoAnidado: lastState.grupoAnidado,
        horaInicioAlmuerzo: lastState.horaInicioAlmuerzo || null,
        horaFinAlmuerzo: lastState.horaFinAlmuerzo || null,
        turnoNoche: lastState.turnoNoche || false,
    });
}