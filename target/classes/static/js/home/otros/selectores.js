function rellenarSelectHorasMinutos(selectHora, selectMinuto) {
    selectHora.innerHTML = "";
    selectMinuto.innerHTML = "";

    for (let h = 7; h <= 20; h++) {
        let opt = document.createElement("option");
        opt.value = h.toString().padStart(2, "0");
        opt.textContent = h.toString().padStart(2, "0");
        selectHora.appendChild(opt);
    }

    for (let m = 0; m < 60; m += 30) {
        let opt = document.createElement("option");
        opt.value = m.toString().padStart(2, "0");
        opt.textContent = m.toString().padStart(2, "0");
        selectMinuto.appendChild(opt);
    }
}


// Litepicker para #modal-fecha
const pickerModalFecha = new Litepicker({
  element: document.getElementById('modal-fecha'),
  singleMode: true,         // solo una fecha
  firstDay: 1,              // lunes
  format: 'YYYY-MM-DD',     // mantiene formato ISO
  lang: 'es-ES',            // opcional, para español
  autoApply: true,          // selecciona al hacer click
  dropdowns: {
    minYear: 2020,
    maxYear: null,
    months: true,
    years: true
  }
});

// Litepicker para #edit-fecha
const pickerEditFecha = new Litepicker({
  element: document.getElementById('edit-fecha'),
  singleMode: true,
  firstDay: 1,
  format: 'YYYY-MM-DD',
  lang: 'es-ES',
  autoApply: true,
  dropdowns: {
    minYear: 2020,
    maxYear: null,
    months: true,
    years: true
  }
});