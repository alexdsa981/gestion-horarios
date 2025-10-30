// -------- MODAL EDICIÓN DE FECHAS --------
let fpEditarFechasLicencia = null;
let licenciaIdEditando = null;

function abrirModalEditarFechasLicencia(idLicencia) {
    licenciaIdEditando = idLicencia;
    const modal = new bootstrap.Modal(document.getElementById('modalEditarFechasLicencia'));
    modal.show();

    const selectedAno = document.getElementById("selectorAno").value;
    const selectedMes = document.getElementById("selectorMes").value; // 0 = enero

    fetch(`/app/licencias/${idLicencia}/fechas?anio=${selectedAno}&mes=${selectedMes}`)
        .then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener las fechas de la licencia"))
        .then(fechas => {
            if (!fpEditarFechasLicencia) {
                fpEditarFechasLicencia = flatpickr("#flatpickrEditarFechasLicencia", {
                    mode: "multiple",
                    dateFormat: "Y-m-d",
                    locale: "es",
                    inline: true,
                    defaultDate: fechas,
                });
            } else {
                fpEditarFechasLicencia.setDate(fechas, true);
            }
        })
        .catch(err => {
            Swal.fire({ icon: "error", title: "Error", text: err });
        });
    }


// Guardar cambios en las fechas editadas
document.getElementById('formEditarFechasLicencia').addEventListener('submit', function(e){
    e.preventDefault();
    const fechas = fpEditarFechasLicencia.selectedDates.map(d => fpEditarFechasLicencia.formatDate(d, "Y-m-d"));
    const anio = document.getElementById("selectorAno").value;
    const mes = document.getElementById("selectorMes").value; // 0 = enero

    if (!licenciaIdEditando || fechas.length === 0) {
        Swal.fire({ icon: "warning", title: "Selecciona fechas", text: "Debes seleccionar al menos una fecha." });
        return;
    }
    fetch(`/app/licencias/${licenciaIdEditando}/fechas?anio=${anio}&mes=${mes}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(fechas)
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                try {
                    const errorObj = JSON.parse(text);
                    throw new Error(errorObj.error || text || "No se pudieron actualizar las fechas");
                } catch {
                    throw new Error(text || "No se pudieron actualizar las fechas");
                }
            });
        }
        Swal.fire({ icon: "success", title: "Fechas actualizadas", text: "Se actualizaron correctamente." });
        bootstrap.Modal.getInstance(document.getElementById('modalEditarFechasLicencia')).hide();
        cargarLicenciasActivas();
        cargarEventosVacaciones();
    })
    .catch(error => {
        let msg = error.message;
        try {
            const parsed = JSON.parse(msg);
            if (parsed && parsed.error) {
                msg = parsed.error;
            }
        } catch (e) {}
        Swal.fire({ icon: "error", title: "Error", text: msg });
    });
});