const nombresMeses = [
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
];
const nombresDiasSemana = [
    "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"
];



function generarOpcionesAnoExportar() {
    const selectorAno = document.getElementById("selectorAnoExportar");
    selectorAno.innerHTML = "";
    const anoActual = new Date().getFullYear();
    for (let y = anoActual - 2; y <= anoActual + 2; y++) {
        selectorAno.innerHTML += `<option value="${y}" ${y === anoActual ? 'selected' : ''}>${y}</option>`;
    }
}

// Genera opciones para el selector de mes de exportar
function generarOpcionesMesExportar() {
    const selectorMes = document.getElementById("selectorMesExportar");
    selectorMes.innerHTML = "";
    const mesActual = new Date().getMonth();
    for (let m = 0; m < 12; m++) {
        selectorMes.innerHTML += `<option value="${m+1}" ${m === mesActual ? 'selected' : ''}>${nombresMeses[m]}</option>`;
    }
}




// --- Selectores de año y mes ---
function generarOpcionesAno() {
    const selectorAno = document.getElementById("selectorAno");
    selectorAno.innerHTML = "";
    const anoActual = new Date().getFullYear();
    for (let y = anoActual - 2; y <= anoActual + 2; y++) {
        selectorAno.innerHTML += `<option value="${y}" ${y === anoActual ? 'selected' : ''}>${y}</option>`;
    }
}

function generarOpcionesMes() {
    const selectorMes = document.getElementById("selectorMes");
    selectorMes.innerHTML = "";
    const mesActual = new Date().getMonth();
    for (let m = 0; m < 12; m++) {
        selectorMes.innerHTML += `<option value="${m}" ${m === mesActual ? 'selected' : ''}>${nombresMeses[m]}</option>`;
    }
}


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

