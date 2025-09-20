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

    // Construir el objeto que espera el backend (Agrega_BH_DTO)
    const data = {
        fecha: fecha,
        horaInicio: inicio,
        horaFin: fin,
        idColaborador: idColaborador,
        idSede: sede,
        idAgrupacion: agrupacionGlobalId // <--- USO DIRECTO DE LA VARIABLE GLOBAL
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
            // Si trae un mensaje de error personalizado, lo mostramos
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