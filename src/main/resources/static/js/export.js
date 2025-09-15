document.addEventListener("DOMContentLoaded", function () {
    const exportModal = document.getElementById('export-modal');
    exportModal.addEventListener('show.bs.modal', function () {
        const hoy = new Date();
        const año = hoy.getFullYear();
        const mes = hoy.getMonth(); // 0-indexed

        // Primer día del mes
        const primeroMes = new Date(año, mes, 1);
        // Último día del mes
        const ultimoMes = new Date(año, mes + 1, 0);

        // Formatear a yyyy-MM-dd para LocalDate en Java
        function formatoFecha(fecha) {
            const m = (fecha.getMonth() + 1).toString().padStart(2, '0');
            const d = fecha.getDate().toString().padStart(2, '0');
            return `${fecha.getFullYear()}-${m}-${d}`;
        }

        document.getElementById("export-fecha-desde").value = formatoFecha(primeroMes);
        document.getElementById("export-fecha-hasta").value = formatoFecha(ultimoMes);
    });

    document.getElementById("export-form").addEventListener("submit", function(e){
      e.preventDefault();
      const desde = document.getElementById("export-fecha-desde").value;
      const hasta = document.getElementById("export-fecha-hasta").value;
      // Descarga el archivo Excel
      window.location.href = `/app/exportar/horarios?desde=${desde}&hasta=${hasta}`;
    });
});