// Modal principal de licencias
function cargarLicenciasActivas() {
    const tbodyLicencias = document.querySelector('#modalLicencias tbody');
    const ano = document.getElementById("selectorAno").value;
    const mes = document.getElementById("selectorMes").value;

    fetch(`/app/licencias/activos?agrupacionId=${agrupacionGlobalId}&anio=${ano}&mes=${mes}`)
        .then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener las licencias"))
        .then(licencias => {
            tbodyLicencias.innerHTML = '';
            if (licencias.length === 0) {
                tbodyLicencias.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No hay licencias activas.</td></tr>';
                return;
            }
            licencias.forEach(l => {
                tbodyLicencias.insertAdjacentHTML('beforeend', `
                    <tr>
                        <td>${l.colaborador}</td>
                        <td>
                            <span class="celda-dias-licencia text-primary fw-bold" style="cursor:pointer;" data-id="${l.id}" title="Ver/editar fechas">
                                ${l.dias} días
                            </span>
                        </td>
                        <td>${l.tipoLicencia}</td>
                        <td class="text-center">
                            <button class="btn btn-danger btn-sm btn-eliminar-licencia" data-id="${l.id}">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                `);
            });

        tbodyLicencias.querySelectorAll('.btn-eliminar-licencia').forEach(btn => {
            btn.addEventListener('click', function () {
                const licenciaId = btn.getAttribute('data-id');
                // Obtén año y mes seleccionados
                const ano = document.getElementById("selectorAno").value;
                const mes = document.getElementById("selectorMes").value;

                Swal.fire({
                    icon: "warning",
                    title: "¿Eliminar fechas de licencia?",
                    text: "¿Estás seguro que deseas eliminar las fechas de este mes?",
                    showCancelButton: true,
                    confirmButtonText: "Sí, eliminar",
                    cancelButtonText: "Cancelar"
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch(`/app/licencias/${licenciaId}/desactivar?anio=${ano}&mes=${mes}`, {
                            method: 'POST'
                        })
                        .then(res => {
                            if (!res.ok) throw new Error("No se pudo eliminar las fechas");
                            Swal.fire({
                                icon: "success",
                                title: "Fechas eliminadas",
                                text: "Las fechas del mes fueron eliminadas correctamente."
                            });
                            cargarLicenciasActivas();
                            cargarEventosVacaciones();
                        })
                        .catch(err => {
                            Swal.fire({ icon: "error", title: "Error", text: err.message });
                        });
                    }
                });
            });
        });

            // Abrir modal de edición de fechas al hacer click en el número de días
            tbodyLicencias.querySelectorAll('.celda-dias-licencia').forEach(span => {
                span.addEventListener('click', function () {
                    const idLicencia = span.getAttribute('data-id');
                    abrirModalEditarFechasLicencia(idLicencia);
                });
            });
        })
        .catch(err => {
            tbodyLicencias.innerHTML = `<tr><td colspan="4" class="text-danger text-center">${err}</td></tr>`;
        });
}

function actualizarTituloLicenciasModal() {
    const ano = document.getElementById("selectorAno").value;
    const mes = document.getElementById("selectorMes").value;
    const titulo = document.getElementById("mes-año-lista-registros-licencias");
    titulo.textContent = `${nombresMeses[mes]} - ${ano}`;
}



// Listener para el botón flotante
document.getElementById('fab-modal-licencias').addEventListener('click', function () {
    actualizarTituloLicenciasModal();
    var modal = new bootstrap.Modal(document.getElementById('modalLicencias'));
    modal.show();
    cargarLicenciasActivas();
});
document.getElementById('btnAgregarLicencia').addEventListener('click', function () {
    var modal = new bootstrap.Modal(document.getElementById('modalAgregarLicencia'));
    modal.show();
});




