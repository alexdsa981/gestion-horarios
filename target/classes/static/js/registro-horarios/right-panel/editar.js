document.addEventListener("DOMContentLoaded", function () {
  // Mostrar panel de edición
  calendar.onEventClick = function (args) {
    const event = args.e.data;
    document.getElementById("right-panel").style.display = "block";
    document.getElementById("edit-colaborador").value = event.idColaborador || getColaboradorIdByText(event.text);

    const start = new DayPilot.Date(event.start);
    const end = new DayPilot.Date(event.end);

    const [startHour, startMinute] = start.toString("HH:mm").split(":");
    const [endHour, endMinute] = end.toString("HH:mm").split(":");

    document.getElementById("edit-horaInicioHora").value = startHour;
    document.getElementById("edit-horaInicioMinuto").value = startMinute;
    document.getElementById("edit-horaFinHora").value = endHour;
    document.getElementById("edit-horaFinMinuto").value = endMinute;

    document.getElementById("edit-sede").value = event.resource || event.idSede;
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
  console.log("¡El submit fue disparado!");
  const id = Number(this.dataset.eventId);
  console.log("ID evento a editar:", id);
  console.log("Buscando evento con id (número):", id);
  let event = calendar.events.find(id);
  if (!event) {
    console.log("No encontrado como número, intento como string...");
    event = calendar.events.find(String(id));
  }
  console.log("Evento encontrado después de ambos intentos:", event);

  if (!event) return;

  const colabSelect = document.getElementById("edit-colaborador");
  const fecha = new DayPilot.Date(event.data.start).toString("yyyy-MM-dd");

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

  const dto = {
    idColaborador: Number(colabSelect.value),
    idSede: Number(document.getElementById("edit-sede").value),
    fecha: fecha,
    horaInicio: `${horaInicio}:${minutoInicio}`,
    horaFin: `${horaFin}:${minutoFin}`
  };
  console.log("Enviando DTO al backend:", dto);
  console.log("URL fetch:", `/app/bloque-horarios/editar/${id}`);

  fetch(`/app/bloque-horarios/editar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dto)
  })
  .then(response => {
    console.log("Respuesta fetch:", response);
    if (!response.ok) throw new Error("Error al editar en el servidor");
    return response.json();
  })
  .then(data => {
      console.log("Respuesta JSON:", data);
      event.data.text = data.nombreColaborador;
      event.data.start = `${data.fecha}T${data.horaInicio}`;
      event.data.end = `${data.fecha}T${data.horaFin}`;
      event.data.resource = data.idSede;
      event.data.backColor = data.color;
      calendar.events.update(event);

      Swal.fire({ icon: "success", title: "Editado correctamente", timer: 1200, showConfirmButton: false });
      document.getElementById("right-panel").style.display = "none";
  })
  .catch(error => {
    console.error("ERROR EN EL CATCH:", error);
    Swal.fire({ icon: "error", title: "Error", text: error.message });
  });
});

  document.getElementById("edit-cancel").addEventListener("click", function () {
    document.getElementById("right-panel").style.display = "none";
  });
});