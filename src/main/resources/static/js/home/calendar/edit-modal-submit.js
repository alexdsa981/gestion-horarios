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

    const colabSelect = document.getElementById("edit-colaborador");
    const fechaNueva = document.getElementById("edit-fecha").value;
    const horaInicio = document.getElementById("edit-horaInicioHora").value;
    const minutoInicio = document.getElementById("edit-horaInicioMinuto").value;
    const horaFin = document.getElementById("edit-horaFinHora").value;
    const minutoFin = document.getElementById("edit-horaFinMinuto").value;

    const inicio = parseInt(horaInicio, 10) * 60 + parseInt(minutoInicio, 10);
    const fin = parseInt(horaFin, 10) * 60 + parseInt(minutoFin, 10);
    if (inicio >= fin) {
        Swal.fire({ icon: "warning", title: "Horas no válidas", text: "La hora de fin debe ser mayor que la hora de inicio." });
        return;
    }

    const fechaOriginal = event.data.start.substring(0, 10); // yyyy-MM-dd

    const dto = {
        idColaborador: Number(colabSelect.value),
        idSede: Number(document.getElementById("edit-sede").value),
        fecha: fechaNueva,
        horaInicio: `${horaInicio}:${minutoInicio}`,
        horaFin: `${horaFin}:${minutoFin}`
    };

    fetch(`/app/bloque-horarios/editar/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    })
    .then(async response => {
        if (!response.ok) {
            // Intenta leer el error personalizado del backend
            let mensaje = "Error al editar en el servidor";
            try {
                const errorJson = await response.clone().json();
                if (errorJson && errorJson.error) {
                    mensaje = errorJson.error;
                }
            } catch {
                // Si no es JSON, intenta como texto
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
        // Actualiza los datos del evento
        event.data.text = data.nombreColaborador;
        event.data.start = `${data.fecha}T${data.horaInicio}`;
        event.data.end = `${data.fecha}T${data.horaFin}`;
        event.data.resource = data.idSede;
        event.data.backColor = data.color;

        // Si la fecha cambió, mueve el evento al nuevo mini-calendar
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