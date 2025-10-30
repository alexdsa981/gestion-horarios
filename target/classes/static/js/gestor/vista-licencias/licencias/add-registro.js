const modalLicencia = document.getElementById("modalAgregarLicencia");
let fpLicencia = null;

// Limpieza al cerrar modal
modalLicencia.addEventListener("hidden.bs.modal", function () {
  document.activeElement.blur();
  if (fpLicencia) fpLicencia.clear();
});

// Flatpickr: solo una vez (al mostrar el modal)
document.addEventListener("DOMContentLoaded", function () {
  fpLicencia = flatpickr("#fechasLicenciaCalendar", {
    mode: "multiple",
    dateFormat: "Y-m-d",
    locale: "es",
    inline: true, // calendario siempre visible
    allowInput: false,
    defaultDate: [],
  });

  modalLicencia.addEventListener("show.bs.modal", function () {
    // Cargar motivos
    const motivoSelect = document.getElementById("motivo");
    motivoSelect.innerHTML = '<option value="">Selecciona...</option>';
    fetch("/app/tipo-licencias/activos")
      .then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener motivos"))
      .then(motivos => {
        motivos.forEach(m => {
          motivoSelect.insertAdjacentHTML("beforeend", `<option value="${m.id}">${m.nombre}</option>`);
        });
      })
      .catch(err => {
        Swal.fire({ icon: "error", title: "Error", text: err });
      });

    // Cargar colaboradores
    const colaboradorSelect = document.getElementById("colaborador");
    colaboradorSelect.innerHTML = '<option value="">Selecciona...</option>';
    if (typeof agrupacionGlobalId !== "undefined" && agrupacionGlobalId) {
      fetch(`/app/colaboradores/agrupacion/${agrupacionGlobalId}`)
        .then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener colaboradores"))
        .then(colaboradores => {
          colaboradores.forEach(c => {
            colaboradorSelect.insertAdjacentHTML("beforeend", `<option value="${c.id}">${c.nombreCompleto}</option>`);
          });
        })
        .catch(err => {
          Swal.fire({ icon: "error", title: "Error", text: err });
        });
    }
  });

  // Envío del formulario
  document.getElementById("formAgregarLicencia").addEventListener("submit", function(e) {
    e.preventDefault();
    const colaboradorId = document.getElementById("colaborador").value;
    const motivoId = document.getElementById("motivo").value;
    const fechas = fpLicencia.selectedDates.map(d => fpLicencia.formatDate(d, "Y-m-d"));

    if (!colaboradorId || !motivoId || fechas.length === 0) {
      Swal.fire({ icon: "warning", title: "Campos incompletos", text: "Completa todos los campos y selecciona al menos una fecha." });
      return;
    }

    const body = {
      colaboradorId,
      motivoId,
      fechas
    };

    fetch('/app/licencias/crear', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    .then(res => {
      if (!res.ok) {
        return res.text().then(text => {
          try {
            const errorObj = JSON.parse(text);
            throw new Error(errorObj.error || text || "No se pudo registrar la licencia");
          } catch {
            throw new Error(text || "No se pudo registrar la licencia");
          }
        });
      }
      Swal.fire({ icon: "success", title: "Licencia agregada", text: "Se guardó correctamente." });
      bootstrap.Modal.getInstance(modalLicencia).hide();
      this.reset();
      fpLicencia.clear();
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
});