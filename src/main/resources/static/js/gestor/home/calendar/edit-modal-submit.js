// Asume que lastCalendarUsed y event tienen la propiedad turnoNoche correctamente seteada

document.getElementById("edit-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const id = this.dataset.eventId;

    if (!id) {
        Swal.fire({ icon: "error", title: "No hay evento seleccionado", text: "No se encontró el ID del evento." });
        return;
    }

    let event = null;
    if (lastCalendarUsed && lastCalendarUsed.events) {
        event = lastCalendarUsed.events.find(id) || lastCalendarUsed.events.find(String(id)) || lastCalendarUsed.events.find(Number(id));
    }
    if (!event && typeof calendar !== "undefined" && calendar.events) {
        event = calendar.events.find(id) || calendar.events.find(String(id)) || calendar.events.find(Number(id));
    }
    if (!event) {
        Swal.fire({ icon: "error", title: "No se encontró el evento", text: "No se encontró el evento en el calendario." });
        return;
    }

    // Detectar si es turno noche
    const isTurnoNoche = !!event.data.turnoNoche;

    const colabSelect = document.getElementById("edit-colaborador");
    const fechaNueva = document.getElementById("edit-fecha").value;

    // SOLO tomar horas si NO es turno noche
    let horaInicio, minutoInicio, horaFin, minutoFin, inicio, fin;
    if (!isTurnoNoche) {
        horaInicio = document.getElementById("edit-horaInicioHora").value;
        minutoInicio = document.getElementById("edit-horaInicioMinuto").value;
        horaFin = document.getElementById("edit-horaFinHora").value;
        minutoFin = document.getElementById("edit-horaFinMinuto").value;

        inicio = parseInt(horaInicio, 10) * 60 + parseInt(minutoInicio, 10);
        fin = parseInt(horaFin, 10) * 60 + parseInt(minutoFin, 10);
        if (inicio >= fin) {
            Swal.fire({ icon: "warning", title: "Horas no válidas", text: "La hora de fin debe ser mayor que la hora de inicio." });
            return;
        }
    } else {
        horaInicio = "";
        minutoInicio = "";
        horaFin = "";
        minutoFin = "";
        inicio = null;
        fin = null;
    }

    const fechaOriginal = event.data.start.substring(0, 10); // yyyy-MM-dd

    // --- ALMUERZO ---
    let horaInicioAlmuerzo = null;
    let horaFinAlmuerzo = null;
    if (!isTurnoNoche) {
        const almuerzoSwitch = document.getElementById("edit-tiene-almuerzo");
        const almuerzoInicio = document.getElementById("edit-almuerzo-inicio").value;
        const almuerzoFin = document.getElementById("edit-almuerzo-fin").value;
        if (almuerzoSwitch && almuerzoSwitch.checked) {
            horaInicioAlmuerzo = almuerzoInicio ? almuerzoInicio : null;
            horaFinAlmuerzo = almuerzoFin ? almuerzoFin : null;

            // Validación de almuerzo dentro del bloque
            function toMinutes(str) {
                let [h, m] = str.split(":").map(Number);
                return h * 60 + m;
            }
            const minInicio = toMinutes(horaInicio + ":" + minutoInicio);
            const minFin = toMinutes(horaFin + ":" + minutoFin);
            const minAlmuerzoInicio = horaInicioAlmuerzo ? toMinutes(horaInicioAlmuerzo) : null;
            const minAlmuerzoFin = horaFinAlmuerzo ? toMinutes(horaFinAlmuerzo) : null;

            if (!horaInicioAlmuerzo || !horaFinAlmuerzo) {
                Swal.fire({
                    icon: "warning",
                    title: "Horario de almuerzo incompleto",
                    text: "Debes completar las horas de inicio y fin de almuerzo."
                });
                return;
            }

            if (minAlmuerzoInicio < minInicio || minAlmuerzoFin > minFin) {
                Swal.fire({
                    icon: "warning",
                    title: "Almuerzo fuera de rango",
                    text: "El horario de almuerzo debe estar completamente dentro del bloque horario."
                });
                return;
            }
            if (minAlmuerzoFin <= minAlmuerzoInicio) {
                Swal.fire({
                    icon: "warning",
                    title: "Horas de almuerzo no válidas",
                    text: "La hora de fin de almuerzo debe ser mayor que la hora de inicio."
                });
                return;
            }
        }
    }

    const dto = {
        idColaborador: Number(colabSelect.value),
        idSede: Number(document.getElementById("edit-sede").value),
        fecha: fechaNueva,
        horaInicio: !isTurnoNoche ? `${horaInicio}:${minutoInicio}` : "",
        horaFin: !isTurnoNoche ? `${horaFin}:${minutoFin}` : "",
        horaInicioAlmuerzo: !isTurnoNoche ? horaInicioAlmuerzo : null,
        horaFinAlmuerzo: !isTurnoNoche ? horaFinAlmuerzo : null
        // Puedes agregar idAgrupacion si lo usas en edición también
    };

    fetch(`/app/bloque-horarios/editar/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    })
    .then(async response => {
        if (!response.ok) {
            let mensaje = "Error al editar en el servidor";
            try {
                const errorJson = await response.clone().json();
                if (errorJson && errorJson.error) {
                    mensaje = errorJson.error;
                }
            } catch {
                try {
                    const errorText = await response.clone().text();
                    if (errorText && errorText.trim().length > 0) {
                        mensaje = errorText;
                    }
                } catch {}
            }
            throw new Error(mensaje);
        }
        return response.json();
    })
    .then(data => {
        event.data.text = data.nombreColaborador;
        event.data.start = `${data.fecha}T${data.horaInicio}`;
        event.data.end = `${data.fecha}T${data.horaFin}`;
        event.data.resource = data.idSede;
        event.data.backColor = data.color;
        event.data.horaInicioAlmuerzo = data.horaInicioAlmuerzo ? data.horaInicioAlmuerzo : null;
        event.data.horaFinAlmuerzo = data.horaFinAlmuerzo ? data.horaFinAlmuerzo : null;
        event.data.turnoNoche = data.isTurnoNoche;
        if (fechaNueva !== fechaOriginal) {
            if (lastCalendarUsed && lastCalendarUsed.events && typeof lastCalendarUsed.events.remove === "function") {
                lastCalendarUsed.events.remove(event);
            }
            const nuevoCalId = "mini-calendar-" + fechaNueva;
            const nuevoCalDiv = document.getElementById(nuevoCalId);
            if (nuevoCalDiv && nuevoCalDiv.calendar && typeof nuevoCalDiv.calendar.events.add === "function") {
                nuevoCalDiv.calendar.events.add(event.data);
            }
            if (lastCalendarUsed && typeof lastCalendarUsed.update === "function") {
                lastCalendarUsed.update();
            }
            if (nuevoCalDiv && nuevoCalDiv.calendar && typeof nuevoCalDiv.calendar.update === "function") {
                nuevoCalDiv.calendar.update();
            }
        } else {
            if (lastCalendarUsed && typeof lastCalendarUsed.events.update === "function") {
                lastCalendarUsed.events.update(event);
            }
        }
        forzarAnchoRowHeader();
        Swal.fire({ icon: "success", title: "Editado correctamente", timer: 1200, showConfirmButton: false });
        cerrarModalEdicionBloque();
    })
    .catch(error => {
        Swal.fire({ icon: "error", title: "Error", text: error.message });
    });
});

// Cerrar el panel con el botón cancelar
document.getElementById("edit-cancel").addEventListener("click", function () {
    document.getElementById("right-panel").style.display = "none";
});

document.getElementById('edit-tiene-almuerzo').addEventListener('change', function() {
    const almuerzoDiv = document.getElementById('edit-almuerzo-horas');
    almuerzoDiv.style.display = this.checked ? '' : 'none';

    if (this.checked) {
        // Coloca valores por defecto solo si están vacíos
        if (!document.getElementById('edit-almuerzo-inicio').value)
            document.getElementById('edit-almuerzo-inicio').value = "12:00";
        if (!document.getElementById('edit-almuerzo-fin').value)
            document.getElementById('edit-almuerzo-fin').value = "13:00";
    } else {
        document.getElementById('edit-almuerzo-inicio').value = "";
        document.getElementById('edit-almuerzo-fin').value = "";
    }
});