document.addEventListener('DOMContentLoaded', function() {
    const nav = new DayPilot.Navigator("nav", {
        locale: "es-es",
        selectMode: "day",
        showMonths: 1,
        skipMonths: 1,
        startDate: DayPilot.Date.today(),
        onTimeRangeSelected: (args) => {
            calendar.startDate = args.start;
            calendar.update();

            // Mostrar la fecha seleccionada arriba del calendario
            const fecha = args.start.toString("dddd, d MMMM yyyy", "es-es");
            const selectedDateDiv = document.getElementById("selected-date");
            if (selectedDateDiv) {
                selectedDateDiv.textContent = fecha;
            }
        },
    });

    nav.init();

    // Al iniciar, muestra la fecha de hoy
    const selectedDateDiv = document.getElementById("selected-date");
    if (selectedDateDiv) {
        selectedDateDiv.textContent = DayPilot.Date.today().toString("dddd, d MMMM yyyy", "es-es");
    }
});