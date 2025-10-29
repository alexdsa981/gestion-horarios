const dp = new DayPilot.Month("dp", {
    locale: "es-es",
    cellHeight: 150,
    eventHeight: 35,
    eventDeleteHandling: "Disabled",
    eventMoveHandling: "Disabled",
    eventResizeHandling: "Disabled",
    eventClickHandling: "Disabled",
    eventRightClickHandling: "Disabled",

    onBeforeCellRender: (args) => {
        const cellDate = args.cell.start;
        const visibleMonth = dp.startDate.getMonth();
        const visibleYear = dp.startDate.getYear();

        if (cellDate.getMonth() !== visibleMonth || cellDate.getYear() !== visibleYear) {
            args.cell.properties.backColor = "#eee";
        } else {
            args.cell.properties.backColor = "#fff";
        }
    },


});
dp.init();





document.addEventListener('DOMContentLoaded', function() {
generarOpcionesAno();
generarOpcionesMes();
cargarEventosVacaciones();
});