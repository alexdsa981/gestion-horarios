function mostrarModalAgregarBloque(fechaISO = null) {
    rellenarSelectHorasMinutos(
        document.getElementById("modal-horaInicioHora"),
        document.getElementById("modal-horaInicioMinuto")
    );
    rellenarSelectHorasMinutos(
        document.getElementById("modal-horaFinHora"),
        document.getElementById("modal-horaFinMinuto")
    );
    const inputFecha = document.getElementById("modal-fecha");
    if (!fechaISO) {
        const hoy = new Date();
        fechaISO = hoy.toISOString().slice(0, 10);
    }
    inputFecha.value = fechaISO;
    const modal = new bootstrap.Modal(document.getElementById("modalAgregarBloque"));
    modal.show();
}

document.getElementById("fab-agregar-bloque").addEventListener("click", () => {
    mostrarModalAgregarBloque();
});

// Recarga los eventos de un minicalendario (por fecha)
async function recargarEventosMiniCalendario(fechaISO, columnas) {
    try {
        const response = await fetch(`/app/bloque-horarios/dia?agrupacionId=${agrupacionGlobalId}&fecha=${fechaISO}`);
        if (response.ok) {
            const eventosDia = await response.json();
            const eventos = eventosDia.map(b => ({
                id: b.id,
                resource: b.idSede,
                start: `${b.fecha}T${b.horaInicio}`,
                end: `${b.fecha}T${b.horaFin}`,
                text: b.nombreColaborador,
                backColor: b.color,
                idColaborador: b.idColaborador,
                grupoAnidado: b.grupoAnidado,
                horaInicioAlmuerzo: b.horaInicioAlmuerzo ? b.horaInicioAlmuerzo : null,
                horaFinAlmuerzo: b.horaFinAlmuerzo ? b.horaFinAlmuerzo : null
            }));
            const calId = "mini-calendar-" + fechaISO;
            inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventos);
        } else {
            Swal.fire({ icon: "error", title: "Error", text: "No se pudo recargar el calendario del día." });
        }
    } catch (err) {
        Swal.fire({ icon: "error", title: "Error", text: "Error de conexión al recargar eventos." });
    }
}

document.getElementById("form-agregar-bloque").addEventListener("submit", async function(e) {
    e.preventDefault();

    const colab = document.getElementById("modal-colaborador-select");
    const inicio = document.getElementById("modal-horaInicioHora").value + ":" + document.getElementById("modal-horaInicioMinuto").value + ":00";
    const fin = document.getElementById("modal-horaFinHora").value + ":" + document.getElementById("modal-horaFinMinuto").value + ":00";
    const sede = Number(document.getElementById("modal-sedes-select").value);
    const fecha = document.getElementById("modal-fecha").value;

    if (!fecha) {
        Swal.fire({ icon: "warning", title: "Fecha requerida", text: "Selecciona una fecha." });
        return;
    }

    const startDate = new Date(`${fecha}T${inicio}`);
    const endDate = new Date(`${fecha}T${fin}`);

    if (endDate <= startDate) {
        Swal.fire({
            icon: "warning",
            title: "Horas no válidas",
            text: "La hora de fin debe ser mayor que la hora de inicio.",
        });
        return;
    }

    const color = colab.options[colab.selectedIndex].dataset.color;
    const idColaborador = Number(colab.value);

    const tieneAlmuerzo = document.getElementById('modal-tiene-almuerzo').checked;
    let horaInicioAlmuerzo = null;
    let horaFinAlmuerzo = null;

    // VALIDACIÓN DE ALMUERZO DENTRO DEL BLOQUE
    if (tieneAlmuerzo) {
        horaInicioAlmuerzo = document.getElementById("modal-almuerzo-inicio").value || null;
        horaFinAlmuerzo = document.getElementById("modal-almuerzo-fin").value || null;

        if (!horaInicioAlmuerzo || !horaFinAlmuerzo) {
            Swal.fire({
                icon: "warning",
                title: "Horario de almuerzo incompleto",
                text: "Debes completar las horas de inicio y fin de almuerzo."
            });
            return;
        }

        // Función para convertir "HH:mm" a minutos desde medianoche
        function toMinutes(str) {
            let [h, m] = str.split(":").map(Number);
            return h * 60 + m;
        }

        const minInicio = toMinutes(document.getElementById("modal-horaInicioHora").value + ":" + document.getElementById("modal-horaInicioMinuto").value);
        const minFin = toMinutes(document.getElementById("modal-horaFinHora").value + ":" + document.getElementById("modal-horaFinMinuto").value);
        const minAlmuerzoInicio = toMinutes(horaInicioAlmuerzo);
        const minAlmuerzoFin = toMinutes(horaFinAlmuerzo);

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

    const data = {
        fecha: fecha,
        horaInicio: inicio,
        horaFin: fin,
        idColaborador: idColaborador,
        idSede: sede,
        idAgrupacion: agrupacionGlobalId,
        horaInicioAlmuerzo: tieneAlmuerzo ? horaInicioAlmuerzo : null,
        horaFinAlmuerzo: tieneAlmuerzo ? horaFinAlmuerzo : null
    };

    fetch("/app/bloque-horarios/agregar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(async response => {
        if (response.ok) {
            return response.json();
        } else {
            const errorData = await response.json();
            throw new Error(errorData.error || "Error al crear el bloque horario");
        }
    })
    .then(async bloqueCreado => {
        await recargarEventosMiniCalendario(bloqueCreado.fecha, columnas);
        forzarAnchoRowHeader();
        Swal.fire({
            icon: "success",
            title: "Éxito",
            text: "Bloque horario creado correctamente."
        });
        bootstrap.Modal.getInstance(document.getElementById("modalAgregarBloque")).hide();
    })
    .catch(error => {
        Swal.fire({
            icon: "warning",
            title: "Aviso",
            text: error.message
        });
    });
});

document.getElementById('modal-tiene-almuerzo').addEventListener('change', function() {
    const almuerzoDiv = document.getElementById('modal-almuerzo-horas');
    almuerzoDiv.style.display = this.checked ? '' : 'none';

    if (this.checked) {
        document.getElementById('modal-almuerzo-inicio').value = "12:00";
        document.getElementById('modal-almuerzo-fin').value = "13:00";
    }
});



['modal-almuerzo-inicio', 'modal-almuerzo-fin'].forEach(id => {
    const input = document.getElementById(id);
    if (!input) return;
    input.addEventListener('click', function() {
        if (this.showPicker) this.showPicker();
    });
    input.addEventListener('keydown', function(e) {
        if ((e.key === "Enter" || e.key === " ") && this.showPicker) {
            this.showPicker();
        }
    });
});