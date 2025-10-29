document.addEventListener('mousedown', function(e) {
    const panel = document.getElementById("right-panel");
    if (panel.style.display === "none") return;
    if (panel.contains(e.target)) return;
    cerrarModalEdicionBloque();
});


function mostrarModalEdicionBloque({ modo = "editar", evento = null, fechaISO = null }) {
    const start = new DayPilot.Date(evento.start);
    const fechaStr = start.toString("yyyy-MM-dd");
    document.getElementById("info-fecha").textContent = fechaStr;
    document.getElementById("info-hora-inicio").textContent = start.toString("HH:mm");
    const end = new DayPilot.Date(evento.end);
    document.getElementById("info-hora-fin").textContent = end.toString("HH:mm");
    document.getElementById("info-colaborador").textContent = evento.nombreColaborador || evento.text || "";
    document.getElementById("info-sede").textContent = evento.nombreSede || evento.resource || evento.idSede || "";

    if (evento.horaInicioAlmuerzo && evento.horaFinAlmuerzo) {
        document.getElementById("info-almuerzo-switch").textContent = "Sí";
        document.getElementById("info-almuerzo-horas").style.display = '';
        document.getElementById("info-almuerzo-inicio").textContent = evento.horaInicioAlmuerzo;
        document.getElementById("info-almuerzo-fin").textContent = evento.horaFinAlmuerzo;
    } else {
        document.getElementById("info-almuerzo-switch").textContent = "No";
        document.getElementById("info-almuerzo-horas").style.display = 'none';
        document.getElementById("info-almuerzo-inicio").textContent = "";
        document.getElementById("info-almuerzo-fin").textContent = "";
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

// --- panel movible con el mouse ---
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