let lastCalendarUsed = null;
let lastCalendarUsedDiv = null;

function inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventosDia) {
    const calendar = new DayPilot.Calendar(calId, {
        viewType: "Resources",
        columns: columnas,
        startDate: fechaISO,
        events: eventosDia,
        cellDuration: 30,
        businessBeginsHour: businessBeginsHour,
        businessEndsHour: businessEndsHour,
        showNonBusiness: false,
        cellHeight: 8,
        rowHeaderWidth: 20,
        showHours: true,
        locale: "es-es",
        timeFormat: "Clock24Hours",
        eventClickHandling: "Enabled",
        eventMoveHandling: "Update",
        eventResizeHandling: "Update",
        eventDeleteHandling: "Disabled",
        eventMarginBottom: 0,
        eventMarginTop: 0,
        eventHeight: 11,


        onBeforeEventRender: function (args) {
            args.data.toolTip = args.data.text;
            args.data.fontColor = "#222";
            args.data.fontSize = "10px";
            args.data.borderColor = "#222";

            const startDate = new DayPilot.Date(args.data.start);
            const endDate = new DayPilot.Date(args.data.end);
            const start = startDate.toString("HH:mm");
            const end = endDate.toString("HH:mm");

            // Calcular duración en horas
            const durationMs = endDate.getTime() - startDate.getTime();
            const durationHrs = durationMs / (1000 * 60 * 60);

            let html = "";

            if (durationHrs < 5) {
                // dura menos de 5h
                html = `
                  <div style="display:flex; flex-direction:column; height:100%; justify-content:center;">
                    <div>
                      <div style="font-size:9px; color:#666;">
                        <b>[${start} - ${end}]</b>
                      </div>
                      <div style="font-size:11px; font-weight:bold; color:#222; margin-top:2px;">
                        ${args.data.text}
                      </div>
                      ${
                        (args.data.horaInicioAlmuerzo && args.data.horaInicioAlmuerzo !== "null" &&
                         args.data.horaFinAlmuerzo && args.data.horaFinAlmuerzo !== "null") ?
                        `<div style="font-size:8px; font-weight:500; color:#222; line-height:1.1; background:rgba(255,255,255,0.4);">
                            <div>🍽️:</div>
                            <div>${args.data.horaInicioAlmuerzo}</div>
                            <div>${args.data.horaFinAlmuerzo}</div>
                         </div>`
                        : ""
                      }
                    </div>
                  </div>
                `;
            } else {
                html = `
                  <div style="display:flex; flex-direction:column; height:100%; justify-content:space-between;">
                    <div>
                      <div style="font-size:9px; color:#666;">
                        <b>[${start}]</b>
                      </div>
                      <div style="font-size:11px; font-weight:bold; color:#222; margin-top:2px;">
                        ${args.data.text}
                      </div>
                      ${
                        (args.data.horaInicioAlmuerzo && args.data.horaInicioAlmuerzo !== "null" &&
                         args.data.horaFinAlmuerzo && args.data.horaFinAlmuerzo !== "null") ?
                        `<div style="font-size:8px; font-weight:500; color:#222; line-height:1.1; background:rgba(255,255,255,0.4);">
                            <div>🍽️:</div>
                            <div>${args.data.horaInicioAlmuerzo}</div>
                            <div>${args.data.horaFinAlmuerzo}</div>
                         </div>`
                        : ""
                      }
                    </div>
                    <div style="font-size:9px; color:#666; margin-top:auto;">
                      <b>[${end}]</b>
                    </div>
                  </div>
                `;
            }

            args.data.html = html;
        },



        onBeforeCellRender: function (args) {
            marcarHorasLaborales(args, this.businessBeginsHour, this.businessEndsHour);
        },
    });

    const handlers = crearHandlersEdicion(calendar, null);
    calendar.onEventResized = handlers.onEventResized;
    calendar.onEventMove = handlers.onEventMove;
    calendar.onEventMoved = handlers.onEventMoved;
    calendar.onEventRightClick = handlers.onEventRightClick;

    calendar.onEventClick = function(args) {
        lastCalendarUsed = calendar;
        mostrarModalEdicionBloque({ modo: "editar", evento: args.e.data });

        const currentDiv = document.getElementById(calId);

        if (lastCalendarUsedDiv && lastCalendarUsedDiv !== currentDiv) {
            lastCalendarUsedDiv.classList.remove("calendar-activo");
        }

        currentDiv.classList.add("calendar-activo");
        lastCalendarUsedDiv = currentDiv;
    };

    calendar.init();
    document.getElementById(calId).calendar = calendar;
    document.getElementById(calId).style.visibility = "visible";
}

