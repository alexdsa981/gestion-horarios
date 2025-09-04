async function cargarBloquesHorariosEnCalendario(calendar) {
  try {
    const response = await fetch("/app/bloque-horarios/listar-todo");
    if (!response.ok) throw new Error("No se pudieron obtener los bloques horarios");
    const bloques = await response.json();

    calendar.events.list = [];

    bloques.forEach(b => {
      calendar.events.add({
        id: b.id,
        start: `${b.fecha}T${b.horaInicio}`,
        end: `${b.fecha}T${b.horaFin}`,
        resource: b.idSede,
        text: b.nombreColaborador,
        backColor: b.color,
        idColaborador: b.idColaborador,
      });
    });

    if (calendar.update) calendar.update();

    console.log("Bloques horarios cargados:", bloques);
  } catch (error) {
    console.error("Error al cargar bloques horarios:", error);
    Swal.fire({
      icon: "error",
      title: "Error",
      text: error.message
    });
  }
}