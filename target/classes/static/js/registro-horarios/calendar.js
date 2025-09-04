const columnas = listaSedesActivas.map(sede => ({
    name: sede.nombre,
    id: sede.id
}));

const calendar = new DayPilot.Calendar("calendar", {
  businessBeginsHour: 7,
  businessEndsHour: 20,
  viewType: "Resources",
  locale: "es-es",
  timeFormat: "Clock12Hours",
  startDate: DayPilot.Date.today(),
  cellDuration: 30,
  cellHeight: 24,
  snapToGrid: true,
  columns: columnas,

  onBeforeCellRender: function (args) {
    const hour = args.cell.start.getHours();
    const isWorkingHour = hour >= this.businessBeginsHour && hour < this.businessEndsHour;

    if (args.cell.start.dayOfWeek() === 6 && isWorkingHour) {
      args.cell.properties.business = true;
    } else if (args.cell.start.dayOfWeek() !== 0 && isWorkingHour) {
      args.cell.properties.business = true;
    } else {
      args.cell.properties.business = false;
      args.cell.disabled = true;
    }
  },

});
calendar.onEventResized = function (args) {
  document.getElementById("right-panel").style.display = "none";

  // --- Construye DTO para el backend ---
  const id = args.e.data.id;
  const idColaborador = args.e.data.idColaborador;
  const idSede = args.e.data.resource;
  const fecha = args.newStart.toString("yyyy-MM-dd");
  const horaInicio = args.newStart.toString("HH:mm");
  const horaFin = args.newEnd.toString("HH:mm");

  const dto = {
    idColaborador: Number(idColaborador),
    idSede: Number(idSede),
    fecha: fecha,
    horaInicio: horaInicio,
    horaFin: horaFin
  };

  console.log("Enviando DTO al backend (resize):", dto);

  fetch(`/app/bloque-horarios/editar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dto)
  })
  .then(response => {
    if (!response.ok) throw new Error("Error al guardar cambio de tamaño en el servidor");
    return response.json();
  })
  .then(data => {
    // Solo actualiza si el backend responde bien
    args.e.data.text = data.nombreColaborador;
    args.e.data.start = `${data.fecha}T${data.horaInicio}`;
    args.e.data.end = `${data.fecha}T${data.horaFin}`;
    args.e.data.resource = data.idSede;
    args.e.data.backColor = data.color;
    args.e.data.idColaborador = data.idColaborador;
    args.e.data.grupoAnidado = data.grupoAnidado;
    calendar.events.update(args.e);

    Swal.fire({
      icon: "success",
      title: "Horario ajustado",
      timer: 1000,
      showConfirmButton: false
    });
  })
  .catch(error => {
    console.error("ERROR EN EL CATCH (resize):", error);

    // Opcional: revertir visualmente el cambio si hubo error
    calendar.events.update({
      id: args.e.data.id,
      start: args.e.data.start,
      end: args.e.data.end,
      resource: args.e.data.resource,
      text: args.e.data.text,
      backColor: args.e.data.backColor,
      idColaborador: args.e.data.idColaborador,
      grupoAnidado: args.e.data.grupoAnidado,
    });

    Swal.fire({
      icon: "error",
      title: "Error",
      text: error.message
    });
  });
};





let lastEventState = {};

calendar.onEventMove = function (args) {
  document.getElementById("right-panel").style.display = "none";

  lastEventState = {
    id: args.e.id(),
    start: args.e.start().toString(),
    end: args.e.end().toString(),
    resource: args.e.resource(),
    text: args.e.text(),
    backColor: args.e.data.backColor,
  };
};

calendar.onEventMoved = function (args) {
  // Construir los límites usando los valores configurados
  const day = args.newStart.toString("yyyy-MM-dd");
  const workStart = new DayPilot.Date(`${day}T${String(calendar.businessBeginsHour).padStart(2, "0")}:00:00`);
  const workEnd = new DayPilot.Date(`${day}T${String(calendar.businessEndsHour).padStart(2, "0")}:00:00`);

  const isWorkingHour = args.newStart >= workStart && args.newEnd <= workEnd && args.newStart.dayOfWeek() !== 0; // No domingo

  if (!isWorkingHour) {
    calendar.events.update({
      id: lastEventState.id,
      start: lastEventState.start,
      end: lastEventState.end,
      resource: lastEventState.resource,
      text: lastEventState.text,
      backColor: lastEventState.backColor,
      idColaborador: args.e.data.idColaborador,
      grupoAnidado: args.e.data.grupoAnidado,
    });
    Swal.fire({
      icon: "warning",
      title: "Fuera de horario laboral",
      text: `No puedes mover el evento fuera del horario de ${calendar.businessBeginsHour}:00 a ${calendar.businessEndsHour}:00.`,
    });
    return;
  }

  // Nuevo flujo para persistir el cambio en backend
  const id = args.e.data.id;
  const idColaborador = args.e.data.idColaborador;
  const idSede = args.e.data.resource;
  const fecha = args.newStart.toString("yyyy-MM-dd");
  const horaInicio = args.newStart.toString("HH:mm");
  const horaFin = args.newEnd.toString("HH:mm");

  const dto = {
    idColaborador: Number(idColaborador),
    idSede: Number(idSede),
    fecha: fecha,
    horaInicio: horaInicio,
    horaFin: horaFin
  };

  console.log("Enviando DTO al mover:", dto);

  fetch(`/app/bloque-horarios/editar/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(dto)
  })
  .then(response => {
    if (!response.ok) throw new Error("Error al guardar movimiento en el servidor");
    return response.json();
  })
  .then(data => {
    // Solo actualiza visualmente si el backend responde OK
    args.e.data.text = data.nombreColaborador;
    args.e.data.start = `${data.fecha}T${data.horaInicio}`;
    args.e.data.end = `${data.fecha}T${data.horaFin}`;
    args.e.data.resource = data.idSede;
    args.e.data.backColor = data.color;
    args.e.data.idColaborador = data.idColaborador;
    args.e.data.grupoAnidado = data.grupoAnidado;
    calendar.events.update(args.e);

    Swal.fire({
      icon: "success",
      title: "Horario actualizado",
      timer: 1000,
      showConfirmButton: false
    });
  })
  .catch(error => {
    console.error("ERROR EN EL CATCH (mover):", error);

    // Revertir visualmente el movimiento si hay error
    calendar.events.update({
      id: lastEventState.id,
      start: lastEventState.start,
      end: lastEventState.end,
      resource: lastEventState.resource,
      text: lastEventState.text,
      backColor: lastEventState.backColor,
      idColaborador: lastEventState.idColaborador,
      grupoAnidado: lastEventState.grupoAnidado,
    });

    Swal.fire({
      icon: "error",
      title: "Error",
      text: error.message
    });
  });
};

calendar.init();
cargarBloquesHorariosEnCalendario(calendar);
