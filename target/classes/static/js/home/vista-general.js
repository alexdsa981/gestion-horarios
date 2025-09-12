
// Recursos demo
const columnas = [
    { name: "Vesalio", id: "central" },

    //{ name: "San Isidro - Clinica", id: "norte" },
    //{ name: "Sur", id: "sur" }
];

// Bloques demo
const bloques =
    [
        // Ejemplo de horarios compartidos y jornadas largas en "central" el 15
        { fecha: "2025-09-15", recurso: "central", start: "07:00", end: "17:00", color: "#4e91e2", text: "Juan Pérez" },
        { fecha: "2025-09-15", recurso: "central", start: "08:00", end: "16:00", color: "#e2784e", text: "María González" },
        { fecha: "2025-09-15", recurso: "central", start: "09:00", end: "19:00", color: "#4ee2ab", text: "José Paredes" },

        // Más entradas en "central" ese día
        { fecha: "2025-09-15", recurso: "central", start: "12:00", end: "22:00", color: "#e24e9c", text: "Patricia Mendoza" },

        // Ejemplo de horarios compartidos y jornadas largas en "norte"
        { fecha: "2025-09-15", recurso: "norte", start: "07:00", end: "17:00", color: "#4e91e2", text: "Ana Torres" },
        { fecha: "2025-09-15", recurso: "norte", start: "08:00", end: "18:00", color: "#e2784e", text: "Carlos Ramírez" },
        { fecha: "2025-09-15", recurso: "norte", start: "10:00", end: "20:00", color: "#4ee2ab", text: "Andrea Tapia" },

        // Más entradas en "sur" con jornadas largas y compartidas
        { fecha: "2025-09-15", recurso: "sur", start: "07:00", end: "17:00", color: "#4e91e2", text: "Luis Domínguez" },
        { fecha: "2025-09-15", recurso: "sur", start: "09:00", end: "19:00", color: "#e2784e", text: "María López" },
        { fecha: "2025-09-15", recurso: "sur", start: "12:00", end: "22:00", color: "#4ee2ab", text: "Patricia Torres" },

        // Otro día, más jornadas largas y compartidas
        { fecha: "2025-09-17", recurso: "central", start: "07:00", end: "17:00", color: "#4e91e2", text: "Fernando Gómez" },
        { fecha: "2025-09-17", recurso: "central", start: "08:00", end: "18:00", color: "#e2784e", text: "Sofía Martínez" },
        { fecha: "2025-09-17", recurso: "central", start: "10:00", end: "20:00", color: "#4ee2ab", text: "Roberto Herrera" },

        { fecha: "2025-09-17", recurso: "norte", start: "07:00", end: "17:00", color: "#4e91e2", text: "Valeria Suárez" },
        { fecha: "2025-09-17", recurso: "norte", start: "08:00", end: "16:00", color: "#e2784e", text: "Javier Medina" },

        { fecha: "2025-09-17", recurso: "sur", start: "09:00", end: "19:00", color: "#4ee2ab", text: "Claudia Vargas" },
        { fecha: "2025-09-17", recurso: "sur", start: "12:00", end: "22:00", color: "#e24e9c", text: "Miguel Castro" },

        // Otro día
        { fecha: "2025-09-19", recurso: "central", start: "07:00", end: "17:00", color: "#4e91e2", text: "Gabriela Ruiz" },
        { fecha: "2025-09-19", recurso: "central", start: "08:00", end: "16:00", color: "#e2784e", text: "Diego Fernández" },

        { fecha: "2025-09-19", recurso: "norte", start: "09:00", end: "19:00", color: "#4ee2ab", text: "Camila Ortiz" },
        { fecha: "2025-09-19", recurso: "norte", start: "12:00", end: "22:00", color: "#e24e9c", text: "Martín Castillo" },

        { fecha: "2025-09-19", recurso: "sur", start: "07:00", end: "17:00", color: "#4e91e2", text: "Esteban Molina" },
        { fecha: "2025-09-19", recurso: "sur", start: "08:00", end: "18:00", color: "#e2784e", text: "Paula Romero" }
    ];

// Genera días del mes actual
function getDaysInMonth(year, month) {
    let days = [];
    let total = new Date(year, month + 1, 0).getDate();
    for (let d = 1; d <= total; d++) {
        days.push(`${year}-${(month + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}`);
    }
    return days;
}

// Renderiza la cuadrícula
const hoy = new Date();
const year = hoy.getFullYear();
const month = hoy.getMonth();
const diasMes = getDaysInMonth(year, month);

const grid = document.getElementById('mesGrid');
diasMes.forEach(fechaISO => {
    const celda = document.createElement('div');
    celda.className = 'mini-celda';

    // Label de fecha
    const fechaLabel = document.createElement('div');
    fechaLabel.className = 'fecha-label';
    fechaLabel.textContent = new Date(fechaISO).getDate();
    celda.appendChild(fechaLabel);

    // Mini calendar container
    const calDiv = document.createElement('div');
    const calId = "mini-calendar-" + fechaISO;
    calDiv.id = calId;
    calDiv.style.height = "237px";
    calDiv.style.width = "110px";
    celda.appendChild(calDiv);

    grid.appendChild(celda);

    // Eventos para ese día
    const eventosDia = bloques.filter(b => b.fecha === fechaISO).map(b => ({
        id: b.text + b.start,
        resource: b.recurso,
        start: fechaISO + "T" + b.start + ":00",
        end: fechaISO + "T" + b.end + ":00",
        text: b.text,
        backColor: b.color
    }));

    setTimeout(() => {
        const calendar = new DayPilot.Calendar(calId, {
            viewType: "Resources",
            columns: columnas,
            startDate: fechaISO,
            events: eventosDia,
            cellDuration: 30,
            businessBeginsHour: 7,
            businessEndsHour: 20,
            cellHeight: 8,
            height: 100,
            showHours: true,
            locale: "es-es",
            timeFormat: "Clock24Hours",
            eventClickHandling: "Disabled",
            eventMoveHandling: "Disabled",
            eventResizeHandling: "Disabled",
            eventDeleteHandling: "Disabled",
            eventMarginBottom: 0,
            eventMarginTop: 0,
            eventHeight: 11,
            onBeforeEventRender: function (args) {
                args.data.toolTip = args.data.text;
                args.data.fontColor = "#fff";
                args.data.fontSize = "8px";
                args.data.borderColor = "#222";
            },
            onBeforeCellRender: function (args) {
                const hour = args.cell.start.getHours();
                const isWorkingHour = hour >= this.businessBeginsHour && hour < this.businessEndsHour;

                if (args.cell.start.dayOfWeek() === 6 && isWorkingHour) {
                    args.cell.properties.business = true;
                } else if (args.cell.start.dayOfWeek() === 0 && isWorkingHour) {
                    args.cell.properties.business = true;
                }
            },

        });
        calendar.init();
    }, 0);
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
}, 0);


