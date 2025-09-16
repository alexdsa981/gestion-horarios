function inicializarMiniCalendarioEditable(calId, fechaISO, columnas, eventosDia) {
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

    // Handlers de edición (cada calendar tiene su propio estado)
    const handlers = crearHandlersEdicion(calendar);
    calendar.onEventResized = handlers.onEventResized;
    calendar.onEventMove = handlers.onEventMove;
    calendar.onEventMoved = handlers.onEventMoved;
    calendar.onEventRightClick = handlers.onEventRightClick;




    calendar.init();
    document.getElementById(calId).style.visibility = "visible";
}