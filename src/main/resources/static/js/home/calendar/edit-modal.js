// Cerrar el panel si se hace click fuera
document.addEventListener('mousedown', function(e) {
    const panel = document.getElementById("right-panel");

    const calendarSelectors = ['.litepicker', '.litepicker-container', '.datepicker-dropdown'];
    let clickInsideCalendar = false;
    let calendarElements = [];

    calendarSelectors.forEach(sel => {
        const elems = Array.from(document.querySelectorAll(sel));
        calendarElements = calendarElements.concat(elems);
    });

    if (panel.contains(e.target)) {
        return;
    }
    for (const cal of calendarElements) {
        if (cal.contains(e.target)) {
            clickInsideCalendar = true;
            break;
        }
    }
    if (panel.style.display !== "none" && !clickInsideCalendar) {
        cerrarModalEdicionBloque();
    }
});

// --- Helper para buscar colaborador por nombre en el select ---
function getColaboradorIdByText(text) {
    const select = document.getElementById("edit-colaborador");
    for (let i = 0; i < select.options.length; i++) {
        if (select.options[i].text === text) {
            return select.options[i].value;
        }
    }
    return "";
}

// --- Mostrar panel de edición y rellenar selects/campos ---
function mostrarModalEdicionBloque({ modo = "editar", evento = null, fechaISO = null }) {
    // Referencias a selects
    const start = new DayPilot.Date(evento.start);
    const fechaStr = start.toString("yyyy-MM-dd");
    document.getElementById("edit-fecha").value = fechaStr;
    const horaInicioHora = document.getElementById("edit-horaInicioHora");
    const horaInicioMin = document.getElementById("edit-horaInicioMinuto");
    const horaFinHora = document.getElementById("edit-horaFinHora");
    const horaFinMin = document.getElementById("edit-horaFinMinuto");
    rellenarSelectHorasMinutos(horaInicioHora, horaInicioMin);
    rellenarSelectHorasMinutos(horaFinHora, horaFinMin);

    if (modo === "editar" && evento) {
        // Rellenar datos del evento
        const start = new DayPilot.Date(evento.start);
        const end = new DayPilot.Date(evento.end);
        const [startHour, startMinute] = start.toString("HH:mm").split(":");
        const [endHour, endMinute] = end.toString("HH:mm").split(":");
        document.getElementById("edit-colaborador").value = evento.idColaborador || getColaboradorIdByText(evento.text);
        document.getElementById("edit-sede").value = evento.resource || evento.idSede;
        horaInicioHora.value = startHour;
        horaInicioMin.value = startMinute;
        horaFinHora.value = endHour;
        horaFinMin.value = endMinute;
        document.getElementById("edit-form").dataset.eventId = evento.id;
    } else {
        // Limpiar para agregar
        document.getElementById("edit-colaborador").value = "";
        document.getElementById("edit-sede").value = "";
        horaInicioHora.value = "07";
        horaInicioMin.value = "00";
        horaFinHora.value = "08";
        horaFinMin.value = "00";
        delete document.getElementById("edit-form").dataset.eventId;
    }

    // Mostrar panel y llevarlo al frente
    const panel = document.getElementById("right-panel");
    panel.style.display = "block";
    panel.style.zIndex = "9999";
    setTimeout(() => { panel.focus(); }, 200);
}



function cerrarModalEdicionBloque() {
        document.getElementById("right-panel").style.display = "none";
    if (lastCalendarUsedDiv) {
        lastCalendarUsedDiv.classList.remove("calendar-activo");
        lastCalendarUsedDiv = null;
    }
}



// --- Hacer el panel movible con el mouse ---
(function() {
    const panel = document.getElementById("right-panel");
    const handle = document.getElementById("right-panel-drag");
    if (!panel || !handle) return;
    panel.style.position = "fixed";
    panel.style.top = panel.style.top || "80px";
    panel.style.left = panel.style.left || "60%";

    let isDown = false, offset = {x:0, y:0};

    handle.addEventListener('mousedown', function(e) {
        e.preventDefault(); // Evita selección de texto
        isDown = true;
        offset = {
            x: e.clientX - panel.offsetLeft,
            y: e.clientY - panel.offsetTop
        };
        document.body.style.userSelect = "none";
    });

    document.addEventListener('mouseup', function() {
        isDown = false;
        document.body.style.userSelect = "";
    });

    document.addEventListener('mousemove', function(e) {
        if (!isDown) return;
        // Limita a dentro de la ventana (opcional)
        let x = e.clientX - offset.x;
        let y = e.clientY - offset.y;
        x = Math.max(0, Math.min(x, window.innerWidth - panel.offsetWidth));
        y = Math.max(0, Math.min(y, window.innerHeight - panel.offsetHeight));
        panel.style.left = x + 'px';
        panel.style.top = y + 'px';
    });
})();