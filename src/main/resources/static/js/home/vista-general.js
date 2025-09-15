const columnas = listaSedesActivasPorAgrupacion.map(sede => ({
    name: sede.nombre,
    id: sede.id
}));

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

function getFechaRango(ano, mes) {
    // mes: 0-indexed
    const desde = `${ano}-${(mes+1).toString().padStart(2, '0')}-01`;
    const ultimoDia = new Date(ano, mes + 1, 0).getDate();
    const hasta = `${ano}-${(mes+1).toString().padStart(2, '0')}-${ultimoDia.toString().padStart(2, '0')}`;
    return {desde, hasta};
}

async function renderizarMesGrid(desde, hasta, ano, mesIdx) {
    document.getElementById('tituloMes').textContent = `Horarios de ${nombresMeses[mesIdx]} ${ano}`;
    const grid = document.getElementById('mesGrid');

    grid.style.visibility = "hidden";
    mostrarSpinner();

    const diasMes = getDaysInMonth(ano, mesIdx);
    const bloques = await obtenerBloquesHorarios(agrupacionGlobalId, desde, hasta);

    const fragment = document.createDocumentFragment();

    // --- Espacios vacíos al inicio ---
    const fechaObjPrimerDia = new Date(diasMes[0] + "T00:00");
    let diaSemana = fechaObjPrimerDia.getDay();
    let espaciosVaciosInicio = diaSemana === 0 ? 6 : diaSemana - 1;

    for (let i = 0; i < espaciosVaciosInicio; i++) {
        const celdaVacia = document.createElement('div');
        celdaVacia.className = 'mini-celda celda-vacia';
        celdaVacia.style.background = "#e0e0e0";
        celdaVacia.style.display = "flex";
        celdaVacia.style.alignItems = "center";
        celdaVacia.style.justifyContent = "center";
        celdaVacia.style.color = "#888";
        celdaVacia.innerHTML = "<span style='font-size:10px'>Mes anterior</span>";
        fragment.appendChild(celdaVacia);
    }

    // --- Días del mes ---
    diasMes.forEach(fechaISO => {
        const celda = document.createElement('div');
        celda.className = 'mini-celda';

        const fechaObj = new Date(fechaISO + "T00:00");
        const nombreDia = nombresDiasSemana[fechaObj.getDay()];
        const numeroDia = fechaObj.getDate();

        const diaSemanaLabel = document.createElement('div');
        diaSemanaLabel.className = 'dia-semana-label';
        diaSemanaLabel.textContent = `${nombreDia} ${numeroDia}`;
        diaSemanaLabel.style.fontWeight = "bold";
        diaSemanaLabel.style.fontSize = "11px";
        diaSemanaLabel.style.textAlign = "center";
        celda.appendChild(diaSemanaLabel);

        const fechaLabel = document.createElement('div');
        fechaLabel.className = 'fecha-label';
        fechaLabel.textContent = fechaISO.slice(-2);
        celda.appendChild(fechaLabel);

        const calDiv = document.createElement('div');
        const calId = "mini-calendar-" + fechaISO;
        calDiv.id = calId;
        calDiv.style.height = "238px";
        calDiv.style.width = "110px";
        calDiv.style.visibility = "hidden";
        celda.appendChild(calDiv);

        fragment.appendChild(celda);
    });

    // --- Espacios vacíos al final ---
    const fechaObjUltimoDia = new Date(diasMes[diasMes.length - 1] + "T00:00");
    let diaSemanaUltimo = fechaObjUltimoDia.getDay();
    let espaciosVaciosFinal = diaSemanaUltimo === 0 ? 0 : 7 - diaSemanaUltimo;
    if (espaciosVaciosFinal > 0 && espaciosVaciosFinal < 7) {
        for (let i = 0; i < espaciosVaciosFinal; i++) {
            const celdaVacia = document.createElement('div');
            celdaVacia.className = 'mini-celda celda-vacia';
            celdaVacia.style.background = "#e0e0e0";
            celdaVacia.style.display = "flex";
            celdaVacia.style.alignItems = "center";
            celdaVacia.style.justifyContent = "center";
            celdaVacia.style.color = "#888";
            celdaVacia.innerHTML = "<span style='font-size:10px'>Mes siguiente</span>";
            fragment.appendChild(celdaVacia);
        }
    }

    grid.innerHTML = "";
    grid.appendChild(fragment);

    // Inicializa mini-calendarios solo en días válidos
    diasMes.forEach(fechaISO => {
        const calId = "mini-calendar-" + fechaISO;
        const eventosDia = bloques.filter(b => b.fecha === fechaISO).map(b => ({
            id: b.id,
            resource: b.idSede,
            start: `${b.fecha}T${b.horaInicio}`,
            end: `${b.fecha}T${b.horaFin}`,
            text: b.nombreColaborador,
            backColor: b.color,
            idColaborador: b.idColaborador,
            grupoAnidado: b.grupoAnidado,
        }));
        inicializarMiniCalendario(calId, fechaISO, columnas, eventosDia);
    });

    setTimeout(() => {
        document.querySelectorAll('.calendar_default_rowheader').forEach(td => {
            const table = td.closest('table');
            if (table) {
                table.style.width = "20px";
                table.style.minWidth = "20px";
                table.style.maxWidth = "20px";
                table.style.tableLayout = "fixed";
            }
        });
        ocultarSpinner();
        grid.style.visibility = "visible";
    }, 0);
}

window.addEventListener("DOMContentLoaded", () => {
    generarOpcionesAno();
    generarOpcionesMes();

    const selectorAno = document.getElementById("selectorAno");
    const selectorMes = document.getElementById("selectorMes");

    // Inicializar con valores actuales
    const ano = Number(selectorAno.value);
    const mes = Number(selectorMes.value);
    const {desde, hasta} = getFechaRango(ano, mes);
    renderizarMesGrid(desde, hasta, ano, mes);

    selectorAno.addEventListener("change", () => {
        const ano = Number(selectorAno.value);
        const mes = Number(selectorMes.value);
        const {desde, hasta} = getFechaRango(ano, mes);
        renderizarMesGrid(desde, hasta, ano, mes);
    });
    selectorMes.addEventListener("change", () => {
        const ano = Number(selectorAno.value);
        const mes = Number(selectorMes.value);
        const {desde, hasta} = getFechaRango(ano, mes);
        renderizarMesGrid(desde, hasta, ano, mes);
    });
});