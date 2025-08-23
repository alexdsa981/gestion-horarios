document.getElementById("agregar").addEventListener("click", () => {
  const colab = document.getElementById("colaborador");
  const inicio = document.getElementById("horaInicio").value;
  const fin = document.getElementById("horaFin").value;
  const sede = document.getElementById("sede").value;

  const fecha = calendar.startDate.toString("yyyy-MM-dd");

  const startDate = new Date(`${fecha}T${inicio}:00`);
  const endDate = new Date(`${fecha}T${fin}:00`);

  if (startDate.getDay() === 0) {
    Swal.fire({
      icon: "warning",
      title: "No permitido en domingo",
      text: "No se pueden crear horarios los domingos.",
    });
    return;
  }

  if (endDate <= startDate) {
    Swal.fire({
      icon: "warning",
      title: "Horas no válidas",
      text: "La hora de fin debe ser mayor que la hora de inicio.",
    });
    return;
  }

  const color = colab.options[colab.selectedIndex].dataset.color;

  calendar.events.add({
    start: `${fecha}T${inicio}:00`,
    end: `${fecha}T${fin}:00`,
    id: DayPilot.guid(),
    resource: sede,
    text: colab.options[colab.selectedIndex].text,
    backColor: color,
  });

  console.log(`Evento creado de ${inicio} a ${fin}`);

});