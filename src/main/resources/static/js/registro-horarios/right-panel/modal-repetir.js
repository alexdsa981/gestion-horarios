const modal = document.getElementById("modalRepetir");

modal.addEventListener("hidden.bs.modal", function () {
  document.activeElement.blur();
});

document.addEventListener("DOMContentLoaded", function () {
  // flatpickr debe ser accesible para ambos handlers
  let fp = flatpickr("#multi-date-picker", {
    mode: "multiple",
    dateFormat: "Y-m-d",
    locale: "es",
    inline: true,
    defaultDate: [],
  });

  // Cuando se abre el modal, obtiene las fechas del backend y las marca
  modal.addEventListener("show.bs.modal", function () {
    const form = document.getElementById("edit-form");
    const idBloque = form.dataset.eventId;
    const fechaOriginal = form.dataset.eventFecha; // <-- Asegúrate que el form tenga este atributo

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
        // Si no hay fechas, usa la fecha del evento
        const fechasMarcadas = fechas.length > 0 ? fechas : [fechaOriginal];
        fp.setDate(fechasMarcadas, true); // true = trigger change event
      })
      .catch(error => {
        console.error("Error en fetch /app/bloque-horarios/fechas-repeticion:", error);
        Swal.fire({
          icon: "error",
          title: "Error",
          text: error.message
        });
        fp.setDate([fechaOriginal], true);
      });
  });

  // Tu botón para guardar las fechas seleccionadas
  document.getElementById("btnRepetirFechas").addEventListener("click", function () {
    const fechas = fp.selectedDates.map((date) => fp.formatDate(date, "Y-m-d"));

    // Obtén el id del bloque original del atributo data-event-id
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

    // Filtra domingos si lo necesitas (aquí ejemplo: no domingos)
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

    // Construye el DTO
    const dto = {
      id: Number(idBloque),
      fechas: fechasSinDomingos
    };

    // Llama al backend
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
      if (typeof calendar === 'undefined' || !calendar.events) {
        console.warn("No se pudo acceder al calendario para mostrar los eventos.");
        return;
      }

      // Limpia eventos del grupo antes de agregar los nuevos
      const grupoActual = data.length > 0 ? data[0].grupoAnidado : null;
      if (grupoActual) {
        calendar.events.list
          .filter(ev => ev.grupoAnidado === grupoActual)
          .forEach(ev => {
            calendar.events.remove(ev.id);
          });
      }

      // Agrega los eventos nuevos
      data.forEach(bloque => {
        calendar.events.add({
          start: `${bloque.fecha}T${bloque.horaInicio}`,
          end: `${bloque.fecha}T${bloque.horaFin}`,
          id: bloque.id,
          resource: bloque.idSede,
          text: bloque.nombreColaborador,
          backColor: bloque.color,
          idColaborador: bloque.idColaborador,
          grupoAnidado: bloque.grupoAnidado,
        });
      });
    })
    .catch(error => {
      console.error("Error en fetch /app/bloque-horarios/repetir:", error);
      Swal.fire({
        icon: "error",
        title: "Error",
        text: error.message
      });
    });
  });
});