function marcarHorasLaborales(args, beginsHour, endsHour) {
    const hour = args.cell.start.getHours();
    const isWorkingHour = hour >= beginsHour && hour < endsHour;
    if ([0, 6].includes(args.cell.start.dayOfWeek()) && isWorkingHour) {
        args.cell.properties.business = true;
    }
}

// Handlers para edición de eventos, encapsula el estado lastEventState por instancia
function crearHandlersEdicion(calendar, getRightPanelId = null) {
    // Cada calendar tiene su propio estado para revertir movimientos
    let lastEventState = {};

    return {
        onEventResized: function (args) {
            if (getRightPanelId) {
                const panel = document.getElementById(getRightPanelId());
                if (panel) panel.style.display = "none";
            }

            // DTO para backend
            const id = args.e.data.id;
            const idColaborador = args.e.data.idColaborador;
            const idSede = args.e.data.resource;
            const fecha = args.newStart.toString("yyyy-MM-dd");
            const horaInicio = args.newStart.toString("HH:mm");
            const horaFin = args.newEnd.toString("HH:mm");

            const dto = {
                idColaborador: Number(idColaborador),
                idSede: Number(idSede),
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
                args.e.data.text = data.nombreColaborador;
                args.e.data.start = `${data.fecha}T${data.horaInicio}`;
                args.e.data.end = `${data.fecha}T${data.horaFin}`;
                args.e.data.resource = data.idSede;
                args.e.data.backColor = data.color;
                args.e.data.idColaborador = data.idColaborador;
                args.e.data.grupoAnidado = data.grupoAnidado;
                calendar.events.update(args.e);

                Swal.fire({
                    icon: "success",
                    title: "Horario ajustado",
                    timer: 1000,
                    showConfirmButton: false
                });
            })
            .catch(error => {
                // Revertir visualmente si error
                calendar.events.update({
                    id: args.e.data.id,
                    start: args.e.data.start,
                    end: args.e.data.end,
                    resource: args.e.data.resource,
                    text: args.e.data.text,
                    backColor: args.e.data.backColor,
                    idColaborador: args.e.data.idColaborador,
                    grupoAnidado: args.e.data.grupoAnidado,
                });
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
            lastEventState = {
                id: args.e.id(),
                start: args.e.start().toString(),
                end: args.e.end().toString(),
                resource: args.e.resource(),
                text: args.e.text(),
                backColor: args.e.data.backColor,
                idColaborador: args.e.data.idColaborador,
                grupoAnidado: args.e.data.grupoAnidado,
            };
        },

        onEventMoved: function (args) {
            const day = args.newStart.toString("yyyy-MM-dd");
            const workStart = new DayPilot.Date(`${day}T${String(calendar.businessBeginsHour).padStart(2, "0")}:00:00`);
            const workEnd = new DayPilot.Date(`${day}T${String(calendar.businessEndsHour).padStart(2, "0")}:00:00`);

            const isWorkingHour = args.newStart >= workStart && args.newEnd <= workEnd; 

            if (!isWorkingHour) {
                calendar.events.update({
                    id: lastEventState.id,
                    start: lastEventState.start,
                    end: lastEventState.end,
                    resource: lastEventState.resource,
                    text: lastEventState.text,
                    backColor: lastEventState.backColor,
                    idColaborador: lastEventState.idColaborador,
                    grupoAnidado: lastEventState.grupoAnidado,
                });
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
            const fecha = args.newStart.toString("yyyy-MM-dd");
            const horaInicio = args.newStart.toString("HH:mm");
            const horaFin = args.newEnd.toString("HH:mm");

            const dto = {
                idColaborador: Number(idColaborador),
                idSede: Number(idSede),
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
                if (!response.ok) throw new Error("Error al guardar movimiento en el servidor");
                return response.json();
            })
            .then(data => {
                args.e.data.text = data.nombreColaborador;
                args.e.data.start = `${data.fecha}T${data.horaInicio}`;
                args.e.data.end = `${data.fecha}T${data.horaFin}`;
                args.e.data.resource = data.idSede;
                args.e.data.backColor = data.color;
                args.e.data.idColaborador = data.idColaborador;
                args.e.data.grupoAnidado = data.grupoAnidado;
                calendar.events.update(args.e);

                Swal.fire({
                    icon: "success",
                    title: "Horario actualizado",
                    timer: 1000,
                    showConfirmButton: false
                });
            })
            .catch(error => {
                calendar.events.update({
                    id: lastEventState.id,
                    start: lastEventState.start,
                    end: lastEventState.end,
                    resource: lastEventState.resource,
                    text: lastEventState.text,
                    backColor: lastEventState.backColor,
                    idColaborador: lastEventState.idColaborador,
                    grupoAnidado: lastEventState.grupoAnidado,
                });

                Swal.fire({
                    icon: "error",
                    title: "Error",
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
                        // Si fue exitoso, eliminar visualmente
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