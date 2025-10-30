function validarTurnoNoche() {
    var horaFin = document.getElementById('horaFin').value;
    var btnTurnoNoche = document.getElementById('fab-turno-noche');
    if (parseInt(horaFin, 10) === 21) {
        btnTurnoNoche.style.display = 'flex';
    } else {
        btnTurnoNoche.style.display = 'none';
    }
}

document.getElementById('fab-turno-noche').addEventListener('click', function() {
  var modalTN = new bootstrap.Modal(document.getElementById('modal-turno-noche'));
  modalTN.show();
});


/*SUBMIT*/
document.getElementById("form-turno-noche").addEventListener("submit", async function(e) {
    e.preventDefault();

    const colaboradorSelect = document.getElementById("tn-colaborador");
    const idColaborador = Number(colaboradorSelect.value);
    const sedeSelect = document.getElementById("tn-sede");
    const idSede = Number(sedeSelect.value);
    const fecha = document.getElementById("tn-dia").value;

    // Usa tu variable global agrupacionGlobalId, igual que en tu otro form
    const idAgrupacion = typeof agrupacionGlobalId !== "undefined" ? agrupacionGlobalId : null;

    if (!fecha) {
        Swal.fire({ icon: "warning", title: "Fecha requerida", text: "Selecciona una fecha." });
        return;
    }
    if (!idColaborador || !idSede) {
        Swal.fire({ icon: "warning", title: "Campos requeridos", text: "Selecciona colaborador y sede." });
        return;
    }

    // Horarios fijos Turno Noche
    const horaInicio = "20:00";
    const horaFin = "21:00";

    const data = {
        fecha: fecha,
        horaInicio: horaInicio,
        horaFin: horaFin,
        idColaborador: idColaborador,
        idSede: idSede,
        idAgrupacion: idAgrupacion,
        horaInicioAlmuerzo: null,
        horaFinAlmuerzo: null
    };

    fetch("/app/bloque-horarios/agregar/turno-noche", {
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
            throw new Error(errorData.error || "Error al crear el turno noche");
        }
    })
    .then(async bloqueCreado => {
        await recargarEventosMiniCalendario(bloqueCreado.fecha, columnas);
        forzarAnchoRowHeader();
        Swal.fire({
            icon: "success",
            title: "Éxito",
            text: "Turno Noche asignado correctamente."
        });
        bootstrap.Modal.getInstance(document.getElementById("modal-turno-noche")).hide();
    })
    .catch(error => {
        Swal.fire({
            icon: "warning",
            title: "Aviso",
            text: error.message
        });
    });
});

document.addEventListener('DOMContentLoaded', function () {
    const picker = new Litepicker({
        element: document.getElementById('tn-dia'),
        format: 'YYYY-MM-DD',
        lang: 'es-ES',
        firstDay: 1, // Lunes
        dropdowns: {
            minYear: 2020,
            maxYear: 2035,
            months: true,
            years: true
        }
    });

    function getLocalDateString() {
        const now = new Date();
        const offset = now.getTimezoneOffset();
        const local = new Date(now.getTime() - offset * 60000);
        return local.toISOString().slice(0, 10);
    }
    const hoy = getLocalDateString();
    document.getElementById('tn-dia').value = hoy;
    picker.setDate(hoy);
});