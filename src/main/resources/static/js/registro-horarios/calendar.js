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
      // Sábado: solo horas dentro de horario laboral
      args.cell.properties.business = true;
    } else if (args.cell.start.dayOfWeek() !== 0 && isWorkingHour) {
      // Lunes a viernes: horario laboral normal
      args.cell.properties.business = true;
    } else {
      args.cell.properties.business = false;
      args.cell.disabled = true;
    }
  },

  onEventResized: (args) => {
    document.getElementById("right-panel").style.display = "none";
    console.log("Colaborador reprogramado", args);
  },
});

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
    });
    Swal.fire({
      icon: "warning",
      title: "Fuera de horario laboral",
      text: `No puedes mover el evento fuera del horario de ${calendar.businessBeginsHour}:00 a ${calendar.businessEndsHour}:00.`,
    });
    return;
  }
  console.log("Colaborador movido", args);
};

calendar.init();
