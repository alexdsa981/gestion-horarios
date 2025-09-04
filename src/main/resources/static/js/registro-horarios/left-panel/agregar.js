document.getElementById("btn-agregar-bloque").addEventListener("click", () => {
  const colab = document.getElementById("left-panel-colaborador-select");
  const inicio = document.getElementById("horaInicioHora").value + ":" + document.getElementById("horaInicioMinuto").value + ":00";
  const fin = document.getElementById("horaFinHora").value + ":" + document.getElementById("horaFinMinuto").value + ":00";
  const sede = Number(document.getElementById("left-panel-sedes-select").value);
  const fecha = calendar.startDate.toString("yyyy-MM-dd");

  const startDate = new Date(`${fecha}T${inicio}`);
  const endDate = new Date(`${fecha}T${fin}`);

  // Validaciones previas
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
  const idColaborador = Number(colab.value);

  // Si aún no tienes agrupación en el front, envía null
  const idAgrupacion = null; // O reemplaza por el valor correspondiente cuando lo tengas

  // Construir el objeto que espera el backend (Agrega_BH_DTO)
  const data = {
    fecha: fecha,
    horaInicio: inicio,
    horaFin: fin,
    idColaborador: idColaborador,
    idSede: sede,
    idAgrupacion: idAgrupacion
  };

  // 🚩 Imprime el data antes de enviar
  console.log("Datos enviados al backend:", data);

  // Enviar al backend
  fetch("/app/bloque-horarios/agregar", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  })
  .then(response => {
    if (response.ok) {
      return response.json(); // El objeto Mostrar_BH_DTO creado
    } else {
      throw new Error("Error al crear el bloque horario");
    }
  })
  .then(bloqueCreado => {
    // Usar datos planos devueltos por Mostrar_BH_DTO
    calendar.events.add({
      start: bloqueCreado.fecha + "T" + bloqueCreado.horaInicio,
      end: bloqueCreado.fecha + "T" + bloqueCreado.horaFin,
      id: bloqueCreado.id,
      resource: bloqueCreado.idSede,
      text: bloqueCreado.nombreColaborador,
      backColor: bloqueCreado.color,
      idColaborador: bloqueCreado.idColaborador,
      grupoAnidado: bloqueCreado.grupoAnidado,
    });

    Swal.fire({
      icon: "success",
      title: "Éxito",
      text: "Bloque horario creado correctamente."
    });

    console.log("Bloque horario creado:", bloqueCreado);
  })
  .catch(error => {
    Swal.fire({
      icon: "error",
      title: "Error",
      text: error.message
    });
  });
});