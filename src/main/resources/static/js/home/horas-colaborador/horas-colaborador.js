document.getElementById('fab-horas-colaboradores').addEventListener('click', async function () {
    const modal = new bootstrap.Modal(document.getElementById('modalHorasColaboradores'));
    // --- Limpiar tabla antes de cargar nuevos datos ---
    const cuerpoTabla = document.querySelector('#modalHorasColaboradores tbody');
    cuerpoTabla.innerHTML = `<tr><td colspan="3" class="text-center text-secondary">Cargando...</td></tr>`;

    const anio = document.getElementById("selectorAno").value;
    const mes = parseInt(document.getElementById("selectorMes").value) + 1;

    const nombresMeses = ["Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"];
    const mesNombre = nombresMeses[parseInt(document.getElementById("selectorMes").value)];
    document.getElementById("modalHorasColaboradoresLabel").textContent = `Horas Totales - ${mesNombre} ${anio}`;


    // --- Llamar API ---
    try {
        const response = await fetch(`/app/colaboradores/reporte-horas/${agrupacionGlobalId}/${anio}/${mes}`);
        if (!response.ok) throw new Error('Error al obtener reporte de horas');
        const colaboradores = await response.json();

        if (!colaboradores.length) {
            cuerpoTabla.innerHTML = `<tr><td colspan="3" class="text-center text-danger">No hay datos para mostrar</td></tr>`;
        } else {
            cuerpoTabla.innerHTML = '';
            colaboradores.forEach(colaborador => {
                let claseHorasTotales = '';
                if (colaborador.horasTotales < colaborador.horasMensuales) {
                    claseHorasTotales = 'horas-menos';
                } else if (colaborador.horasTotales === colaborador.horasMensuales) {
                    claseHorasTotales = 'horas-verde';
                } else {
                    claseHorasTotales = 'horas-mas';
                }

                const fila = `
                    <tr>
                        <td>${colaborador.nombreCompleto}</td>
                        <td class="text-center ${claseHorasTotales}">${colaborador.horasTotales}</td>
                        <td class="text-center fondo-gris">${colaborador.horasMensuales}</td>
                    </tr>
                `;
                cuerpoTabla.insertAdjacentHTML('beforeend', fila);
            });
        }
    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Error al obtener reporte</td></tr>`;
    }

    modal.show();
});