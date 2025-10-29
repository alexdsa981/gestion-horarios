

function getFechaRango(ano, mes) {
    const desde = `${ano}-${(mes+1).toString().padStart(2, '0')}-01`;
    const ultimoDia = new Date(ano, mes + 1, 0).getDate();
    const hasta = `${ano}-${(mes+1).toString().padStart(2, '0')}-${ultimoDia.toString().padStart(2, '0')}`;
    return {desde, hasta};
}

// --- Renderizar la grilla de mini-calendarios por mes ---
async function renderizarMesGrid(desde, hasta, ano, mesIdx) {
    const grid = document.getElementById('mesGrid');
    grid.style.visibility = "hidden";
    mostrarSpinner();

    const diasMes = getDaysInMonth(ano, mesIdx);
    const bloques = await obtenerBloquesHorarios(agrupacionGlobalId, desde, hasta);

    const fragment = document.createDocumentFragment();

    // Espacios vacíos inicio (para alineación tipo calendario)
    const fechaObjPrimerDia = new Date(diasMes[0] + "T00:00");
    let espaciosVaciosInicio = (fechaObjPrimerDia.getDay() + 6) % 7;
    for (let i = 0; i < espaciosVaciosInicio; i++) {
        fragment.appendChild(crearCeldaVacia("Mes anterior"));
    }
    // Días del mes
    diasMes.forEach(fechaISO => {
        fragment.appendChild(crearCeldaDia(fechaISO, bloques));
    });

    // Espacios vacíos final
    const fechaObjUltimoDia = new Date(diasMes[diasMes.length - 1] + "T00:00");
    let espaciosVaciosFinal = (7 - fechaObjUltimoDia.getDay()) % 7;
    for (let i = 0; i < espaciosVaciosFinal; i++) {
        fragment.appendChild(crearCeldaVacia("Mes siguiente"));
    }

    grid.innerHTML = "";
    grid.appendChild(fragment);
    // Inicializa mini-calendarios editables en días válidos



        diasMes.forEach(fechaISO => {
            const calId = "mini-calendar-" + fechaISO;
            const eventosDia = bloques
                .filter(b => b.fecha === fechaISO)
                .map(b => ({
                    id: b.id,
                    resource: b.idSede,
                    start: `${b.fecha}T${b.horaInicio}`,
                    end: `${b.fecha}T${b.horaFin}`,
                    text: b.nombreColaborador, // solo el nombre, sin HTML
                    backColor: b.color,
                    idColaborador: b.idColaborador,
                    grupoAnidado: b.grupoAnidado,
                    horaInicioAlmuerzo: b.horaInicioAlmuerzo ? b.horaInicioAlmuerzo : null,
                    horaFinAlmuerzo: b.horaFinAlmuerzo ? b.horaFinAlmuerzo : null
                }));
            inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventosDia);
        });




    setTimeout(() => {
        forzarAnchoRowHeader();
        ocultarSpinner();
        grid.style.visibility = "visible";
    }, 0);
}

// --- Helpers visuales ---
function crearCeldaVacia(label) {
    const celdaVacia = document.createElement('div');
    celdaVacia.className = 'mini-celda celda-vacia';
    celdaVacia.style.background = "#e0e0e0";
    celdaVacia.style.display = "flex";
    celdaVacia.style.alignItems = "center";
    celdaVacia.style.justifyContent = "center";
    celdaVacia.style.color = "#888";
    celdaVacia.innerHTML = `<span style='font-size:10px'>${label}</span>`;
    return celdaVacia;
}

function crearCeldaDia(fechaISO, bloques) {
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
    calDiv.id = "mini-calendar-" + fechaISO;
    calDiv.style.width = "110px";
    calDiv.style.visibility = "hidden";
    celda.appendChild(calDiv);

    return celda;
}

// --- Inicialización global ---
function inicializarCalendario() {
    fetchRangoHorario(agrupacionGlobalId);
    generarOpcionesAno();
    generarOpcionesMes();

    const selectorAno = document.getElementById("selectorAno");
    const selectorMes = document.getElementById("selectorMes");

    const ano = Number(selectorAno.value);
    const mes = Number(selectorMes.value);
    const {desde, hasta} = getFechaRango(ano, mes);
    renderizarMesGrid(desde, hasta, ano, mes); // Aquí ya tienes sedes activas

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
}

// --- Refresco individual de calendario de día ---
window.refrescarMiniCalendario = async function(fechaISO) {
    const ano = Number(document.getElementById("selectorAno").value);
    const mes = Number(document.getElementById("selectorMes").value);
    const bloques = await obtenerBloquesHorarios(agrupacionGlobalId, fechaISO, fechaISO);

    const calId = "mini-calendar-" + fechaISO;
    const calDiv = document.getElementById(calId);
    if (!calDiv) return;

    calDiv.innerHTML = '';
    const eventosDia = bloques.filter(b => b.fecha === fechaISO).map(b => ({
        id: b.id,
        resource: b.idSede,
        start: `${b.fecha}T${b.horaInicio}`,
        end: `${b.fecha}T${b.horaFin}`,
        text: b.nombreColaborador,
        backColor: b.color,
        idColaborador: b.idColaborador,
        grupoAnidado: b.grupoAnidado,
        horaInicioAlmuerzo: b.horaInicioAlmuerzo ? b.horaInicioAlmuerzo : null,
        horaFinAlmuerzo: b.horaFinAlmuerzo ? b.horaFinAlmuerzo : null
    }));
    inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventosDia);

    setTimeout(() => {
        forzarAnchoRowHeader();
    }, 0);
};

// --- Refresco de todo el mes ---
window.recargarMesGrid = function() {
    const ano = Number(document.getElementById("selectorAno").value);
    const mes = Number(document.getElementById("selectorMes").value);
    const {desde, hasta} = getFechaRango(ano, mes);
    renderizarMesGrid(desde, hasta, ano, mes);
};