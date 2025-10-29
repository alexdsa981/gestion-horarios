document.addEventListener('DOMContentLoaded', function () {
    const agrupacionesContainer = document.getElementById('agrupacionesContainer');
    const inputAgrupaciones = document.getElementById('agrupacionesSeleccionadas');
    let agrupaciones = [];

    // Fetch agrupaciones activas
    fetch('/app/agrupacion/listar-activos')
        .then(resp => resp.json())
        .then(data => {
            agrupaciones = data;
            agrupacionesContainer.innerHTML = '';
            data.forEach(agrup => {
                const tag = document.createElement('span');
                tag.className = 'agrupacion-label';
                tag.textContent = agrup.nombre;
                tag.dataset.id = agrup.id;
                agrupacionesContainer.appendChild(tag);
            });
        });

    // Selección de etiquetas
    agrupacionesContainer.addEventListener('click', function (e) {
        if (e.target.classList.contains('agrupacion-label')) {
            e.target.classList.toggle('selected');
            actualizarSeleccionAgrupaciones();
        }
    });

    function actualizarSeleccionAgrupaciones() {
        // Array de IDs seleccionados
        const seleccionadas = Array.from(agrupacionesContainer.querySelectorAll('.agrupacion-label.selected'))
            .map(tag => tag.dataset.id);
        inputAgrupaciones.value = seleccionadas.join(',');
    }
});