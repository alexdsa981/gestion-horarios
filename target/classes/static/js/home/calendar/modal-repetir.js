const modal = document.getElementById("modalRepetir");

modal.addEventListener("hidden.bs.modal", function () {
  document.activeElement.blur();
});

document.addEventListener("DOMContentLoaded", function () {
  let fp = flatpickr("#multi-date-picker", {
    mode: "multiple",
    dateFormat: "Y-m-d",
    locale: "es",
    inline: true,
    defaultDate: [],
  });

  modal.addEventListener("show.bs.modal", function () {
    const form = document.getElementById("edit-form");
    const idBloque = form.dataset.eventId;
    const fechaOriginal = form.dataset.eventFecha;

    if (!idBloque) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se encontró el ID del bloque original."
      });
      return;
    }

    fetch(`/app/bloque-horarios/fechas-repeticion/${idBloque}`)
      .then(response => {
        if (!response.ok) throw new Error("No se pudo obtener fechas de repetición.");
        return response.json();
      })
      .then(fechas => {
        const fechasMarcadas = fechas.length > 0 ? fechas : [fechaOriginal];
        fp.setDate(fechasMarcadas, true);
      })
      .catch(error => {
        console.error("Error en fetch /app/bloque-horarios/fechas-repeticion:", error);
        Swal.fire({ icon: "error", title: "Error", text: error.message });
        fp.setDate([fechaOriginal], true);
      });
  });

  document.getElementById("btnRepetirFechas").addEventListener("click", function () {
    const fechas = fp.selectedDates.map((date) => fp.formatDate(date, "Y-m-d"));

    const form = document.getElementById("edit-form");
    const idBloque = form.dataset.eventId;
    if (!idBloque) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se encontró el ID del bloque original."
      });
      return;
    }

    // Filtra domingos si es necesario
    const fechasSinDomingos = fechas.filter(fecha => {
      const d = new Date(fecha);
      return d.getDay() !== 6; // 6 = domingo
    });

    if (fechasSinDomingos.length === 0) {
      Swal.fire({
        icon: "info",
        title: "Sin fechas válidas",
        text: "No se seleccionaron fechas válidas (todas son domingo)."
      });
      return;
    }

    const dto = {
      id: Number(idBloque),
      fechas: fechasSinDomingos
    };

    fetch('/app/bloque-horarios/repetir', {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(dto)
    })
    .then(response => {
      if (!response.ok) throw new Error("Error al registrar los bloques");
      return response.json();
    })
    .then(data => {
      Swal.fire({
        icon: "success",
        title: "Bloques guardados",
        text: `Se guardaron ${data.length} bloques en el sistema.`
      });

      const grupoActual = data.length > 0 ? data[0].grupoAnidado : null;
      const fechasSeleccionadas = new Set(data.map(b => b.fecha));

      // Agrega los nuevos bloques a cada mini-calendario
      data.forEach(bloque => {
        const calId = "mini-calendar-" + bloque.fecha;
        const calDiv = document.getElementById(calId);
        if (calDiv && calDiv.calendar) {
          // Evita duplicados
          const yaExiste = calDiv.calendar.events.list.some(ev => ev.id === bloque.id);
          if (!yaExiste) {
            calDiv.calendar.events.add({
              start: `${bloque.fecha}T${bloque.horaInicio}`,
              end: `${bloque.fecha}T${bloque.horaFin}`,
              id: bloque.id,
              resource: bloque.idSede,
              text: bloque.nombreColaborador,
              backColor: bloque.color,
              idColaborador: bloque.idColaborador,
              grupoAnidado: bloque.grupoAnidado,
            });
          }
        }
      });

      // Elimina visualmente los bloques del grupo anidado que ya no están en fechas seleccionadas
      document.querySelectorAll('[id^="mini-calendar-"]').forEach(calDiv => {
        if (!calDiv.calendar) return;
        const eventosAEliminar = calDiv.calendar.events.list.filter(ev =>
          ev.grupoAnidado === grupoActual &&
          !fechasSeleccionadas.has(String(ev.start).substring(0, 10))
        );
        eventosAEliminar.forEach(ev => {
          calDiv.calendar.events.remove(ev.id);
        });
      });

      if (typeof forzarAnchoRowHeader === "function") {
        forzarAnchoRowHeader();
      }
    })
    .catch(error => {
      console.error("Error en fetch /app/bloque-horarios/repetir:", error);
      Swal.fire({ icon: "error", title: "Error", text: error.message });
    });
  });
});