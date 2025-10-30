const dp = new DayPilot.Month("dp", {
    locale: "es-es",
    cellHeight: 150,
    eventHeight: 35,
    eventDeleteHandling: "Hold",
    eventMoveHandling: "Update",
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




onEventMove: async (args) => {
    const idFecha = args.e.id();
    const nuevaFecha = args.newStart.toString("yyyy-MM-dd");

    try {
        const res = await fetch(`/app/licencias/fecha/editar/${idFecha}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nuevaFecha })
        });
        if (!res.ok) {
            const text = await res.text();
            let msg = "No se pudo mover la fecha.";
            try {
                const errorObj = JSON.parse(text);
                msg = errorObj.error || msg;
            } catch {}
            throw new Error(msg);
        }
        Swal.fire("Actualizado", "La fecha fue movida.", "success");
        cargarEventosVacaciones();
    } catch (err) {
        Swal.fire("Error", err.message, "error");
        cargarEventosVacaciones();

    }
},








    onEventDelete: async (args) => {
        const idFecha = args.e.id();

        const result = await Swal.fire({
            title: "¿Seguro que deseas eliminar este evento?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "No, cancelar",
        });

        if (!result.isConfirmed) return;

        try {
            const res = await fetch(`/app/licencias/fechas/eliminar/${idFecha}`, {
                method: "DELETE"
            });
            if (!res.ok) throw new Error("No se pudo eliminar la fecha.");
            Swal.fire("Eliminado", "La fecha fue eliminada.", "success");
            cargarEventosVacaciones();
        } catch (err) {
            Swal.fire("Error", err.message, "error");
        }
    }


});
dp.init();





document.addEventListener('DOMContentLoaded', function() {
generarOpcionesAno();
generarOpcionesMes();
chequearBloqueoMesLicencias();
cargarEventosVacaciones();
});