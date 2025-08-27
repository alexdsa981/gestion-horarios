document.getElementById("btn-agregar-bloque").addEventListener("click", () => {
  const colab = document.getElementById("left-panel-colaborador-select");
   const inicio = document.getElementById("horaInicioHora").value + ":" + document.getElementById("horaInicioMinuto").value;
   const fin = document.getElementById("horaFinHora").value + ":" + document.getElementById("horaFinMinuto").value;
  const sede = Number(document.getElementById("left-panel-sedes-select").value);
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