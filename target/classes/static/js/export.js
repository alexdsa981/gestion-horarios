
// Llamar a las funciones al abrir el modal o al cargar la página
function cargarSedesExportar() {
    fetch("/app/sedes/listar/activos")
        .then(response => response.json())
        .then(data => {
            const selectorSede = document.getElementById("selectorSedeExportar");
            selectorSede.innerHTML = '<option value="">Seleccione una sede</option>';
            data.forEach(sede => {
                selectorSede.innerHTML += `<option value="${sede.id}">${sede.nombre}</option>`;
            });
        })
        .catch(err => {
            const selectorSede = document.getElementById("selectorSedeExportar");
            selectorSede.innerHTML = '<option value="">No se pudieron cargar las sedes</option>';
        });
}

// Llama a los inicializadores de los selectores normales (no exportar)
document.addEventListener("DOMContentLoaded", function() {
    generarOpcionesAnoExportar();
    generarOpcionesMesExportar();
    cargarSedesExportar();
});

document.addEventListener("DOMContentLoaded", function () {
    const exportModal = document.getElementById('export-modal');
    document.getElementById("export-form").addEventListener("submit", function(e){
        e.preventDefault();
        const ano = document.getElementById("selectorAnoExportar").value;
        const mes = document.getElementById("selectorMesExportar").value;
        const sede = document.getElementById("selectorSedeExportar").value;

        if (!ano || !mes || !sede) {
            alert("Debe seleccionar año, mes y sede.");
            return;
        }

        // Descarga el archivo Excel usando los nuevos parámetros
        window.location.href = `/app/exportar/horarios?ano=${ano}&mes=${mes}&sede=${sede}`;
    });
});