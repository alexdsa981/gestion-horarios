document.addEventListener("DOMContentLoaded", function () {
  // En vez de sobrescribir, despachamos un evento personalizado
  calendar.onEventClick = function (args) {
    const event = args.e.data;
    // Notificar a otros módulos
    document.dispatchEvent(new CustomEvent('eventoSeleccionado', { detail: event }));

    document.getElementById("right-panel").style.display = "block";
    document.getElementById("edit-colaborador").value = getColaboradorIdByText(event.text);
    const start = new DayPilot.Date(event.start);
    const end = new DayPilot.Date(event.end);

    const [startHour, startMinute] = start.toString("HH:mm").split(":");
    const [endHour, endMinute] = end.toString("HH:mm").split(":");

    document.getElementById("edit-horaInicioHora").value = startHour;
    document.getElementById("edit-horaInicioMinuto").value = startMinute;
    document.getElementById("edit-horaFinHora").value = endHour;
    document.getElementById("edit-horaFinMinuto").value = endMinute;

    document.getElementById("edit-sede").value = event.resource;
    document.getElementById("edit-form").dataset.eventId = event.id;
  };

  function getColaboradorIdByText(text) {
    const select = document.getElementById("edit-colaborador");
    for (let i = 0; i < select.options.length; i++) {
      if (select.options[i].text === text) {
        return select.options[i].value;
      }
    }
    return "";
  }

// Guardar cambios
document.getElementById("edit-form").addEventListener("submit", function (e) {
  e.preventDefault();
  const id = this.dataset.eventId;
  const event = calendar.events.find(id);
  if (event) {
    const colabSelect = document.getElementById("edit-colaborador");
    const fecha = new DayPilot.Date(event.data.start).toString("yyyy-MM-dd");

    // Obtener horas y minutos de los selects
    const horaInicio = document.getElementById("edit-horaInicioHora").value;
    const minutoInicio = document.getElementById("edit-horaInicioMinuto").value;
    const horaFin = document.getElementById("edit-horaFinHora").value;
    const minutoFin = document.getElementById("edit-horaFinMinuto").value;

    // Combinar en formato HH:mm
    const horaInicioStr = `${horaInicio}:${minutoInicio}`;
    const horaFinStr = `${horaFin}:${minutoFin}`;

    const hIni = parseInt(horaInicio, 10);
    const mIni = parseInt(minutoInicio, 10);
    const hFin = parseInt(horaFin, 10);
    const mFin = parseInt(minutoFin, 10);

    const inicio = hIni * 60 + mIni;
    const fin = hFin * 60 + mFin;
    if (inicio >= fin) {
      Swal.fire({
        icon: "warning",
        title: "Horas no válidas",
        text: "La hora de fin debe ser mayor que la hora de inicio.",
      });
      return;
    }

    const newStart = `${fecha}T${horaInicioStr}:00`;
    const newEnd = `${fecha}T${horaFinStr}:00`;
    event.data.text = colabSelect.options[colabSelect.selectedIndex].text;
    event.data.start = newStart;
    event.data.end = newEnd;
    event.data.resource = Number(document.getElementById("edit-sede").value);
    event.data.backColor = colabSelect.options[colabSelect.selectedIndex].dataset.color;
    calendar.events.update(event);
  }
  document.getElementById("right-panel").style.display = "none";
});

  document.getElementById("edit-cancel").addEventListener("click", function () {
    document.getElementById("right-panel").style.display = "none";
  });
});
