let lastCalendarUsed = null;

function inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventosDia) {
    const calendar = new DayPilot.Calendar(calId, {
        viewType: "Resources",
        columns: columnas,
        startDate: fechaISO,
        events: eventosDia,
        cellDuration: 30,
        businessBeginsHour: 7,
        businessEndsHour: 20,
        showNonBusiness: false,
        cellHeight: 8,
        height: 100,
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
            args.data.fontColor = "#fff";
            args.data.fontSize = "10px";
            args.data.borderColor = "#222";
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
    };

    calendar.init();
    document.getElementById(calId).calendar = calendar;
    document.getElementById(calId).style.visibility = "visible";
}