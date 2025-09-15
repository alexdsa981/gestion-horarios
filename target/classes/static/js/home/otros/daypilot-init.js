function inicializarMiniCalendario(calId, fechaISO, columnas, eventosDia) {
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
            args.data.fontSize = "10px";
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
    document.getElementById(calId).style.visibility = "visible";
}