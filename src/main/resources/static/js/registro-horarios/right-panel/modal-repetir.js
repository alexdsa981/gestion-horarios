const modal = document.getElementById("modalRepetir");
modal.addEventListener("hidden.bs.modal", function () {
  document.activeElement.blur();
});
document.addEventListener("DOMContentLoaded", function () {
  const fp = flatpickr("#multi-date-picker", {
    mode: "multiple",
    dateFormat: "Y-m-d",
    locale: "es",
    inline: true,
    //defaultDate: ["2025-09-01", "2025-09-05"],
  });

  document.getElementById("btnRepetirFechas").addEventListener("click", function () {
    const fechas = fp.selectedDates.map((date) => fp.formatDate(date, "Y-m-d"));
    const colab = document.getElementById("edit-colaborador");

    const inicioHora = document.getElementById("edit-horaInicioHora").value;
    const inicioMinuto = document.getElementById("edit-horaInicioMinuto").value;
    const finHora = document.getElementById("edit-horaFinHora").value;
    const finMinuto = document.getElementById("edit-horaFinMinuto").value;

    const inicio = `${inicioHora}:${inicioMinuto}`;
    const fin = `${finHora}:${finMinuto}`;

    const sede = Number(document.getElementById("edit-sede").value);

    const color = colab.options[colab.selectedIndex].dataset.color;
    const doctor = colab.options[colab.selectedIndex].text;

    const [hIni, mIni] = inicio.split(":").map(Number);
    const [hFin, mFin] = fin.split(":").map(Number);
    const minIni = hIni * 60 + mIni;
    const minFin = hFin * 60 + mFin;
    if (minFin <= minIni) {
      Swal.fire({
        icon: "warning",
        title: "Horas no válidas",
        text: "La hora de fin debe ser mayor que la hora de inicio."
      });
      return;
    }

    let creados = 0;
    const logs = [];
    if (typeof calendar === 'undefined' || !calendar.events) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudo acceder al calendario."
      });
      return;
    }
    
    fechas.forEach(fecha => {
      const d = new Date(fecha);
      //aqui consifera domingo como 6
      if (d.getDay() === 6) return;
      const id = DayPilot.guid();
      calendar.events.add({
        start: `${fecha}T${inicio}:00`,
        end: `${fecha}T${fin}:00`,
        id,
        resource: sede,
        text: doctor,
        backColor: color,
      });
      creados++;
      logs.push(`ID: ${id} | Fecha: ${fecha} | ${inicio} - ${fin}`);
    });

    if (creados > 0) {
      Swal.fire({
        icon: "success",
        title: "Eventos creados",
        text: `Se crearon ${creados} eventos.`
      });
      console.log("Eventos creados:");
      logs.forEach(l => console.log(l));
    } else {
      Swal.fire({
        icon: "info",
        title: "Sin eventos",
        text: "No se crearon eventos (puede que todas las fechas sean domingo)."
      });
    }
  });
});
