// Variable global para uso general (si quieres sincronizar con otras vistas)
window.listaColaboradoresPorAgrupacion = [];

/**
 * Carga y renderiza la lista de colaboradores en el dropdown selector.
 * Permite cambiar color y desactivar colaboradores.
 */
async function cargarColaboradoresSelector() {
    const cuerpoTabla = document.getElementById('tablaColaboradoresBody');
    if (!cuerpoTabla) return;

    try {
        const response = await fetch("/app/colaboradores/agrupacion/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener colaboradores');

        const colaboradores = await response.json();
        window.listaColaboradoresPorAgrupacion = colaboradores;

        cuerpoTabla.innerHTML = '';

        colaboradores.forEach((colaborador, index) => {
            const estadoHTML = colaborador.isActive
                ? `<span class="badge bg-success estado-toggle" data-id="${colaborador.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary">Inactivo</span>`;

            const fila = `
                <tr>
                    <td>
                        <span class="d-flex align-items-center gap-2">
                            <span style="background:${colaborador.color};width:1.2em;height:1.2em;display:inline-block;border-radius:50%;border:1px solid #ccc"></span>
                            ${colaborador.nombreCompleto}
                        </span>
                    </td>
                    <td class="text-center">
                        <input type="color" name="color" value="${colaborador.color}" data-id="${colaborador.id}" style="border:none; background:transparent;">
                    </td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Evento para cambio de color
        cuerpoTabla.querySelectorAll('input[type="color"][name="color"]').forEach(input => {
            input.addEventListener('change', async function () {
                const colaboradorId = this.getAttribute('data-id');
                const nuevoColor = this.value;
                const dto = {
                    agrupacionId: agrupacionGlobalId,
                    colaboradorId: colaboradorId,
                    color: nuevoColor
                };
                try {
                    const res = await fetch('/app/colaboradores/color', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(dto)
                    });
                    if (!res.ok) throw new Error('Error al actualizar el color');
                    Swal.fire('Color actualizado', '', 'success');
                    renderizarCalendarioActual();
                } catch (error) {
                    console.error(error);
                    Swal.fire('Error', 'No se pudo actualizar el color.', 'error');
                }
            });
        });

        // Evento para desactivar colaborador
        cuerpoTabla.querySelectorAll('.estado-toggle').forEach(span => {
            span.addEventListener('click', async function () {
                const idColaborador = this.getAttribute('data-id');
                const result = await Swal.fire({
                    title: '¿Desactivar colaborador?',
                    text: "Esta acción desactivará al colaborador en el sistema.",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Sí, desactivar',
                    cancelButtonText: 'Cancelar'
                });
                if (result.isConfirmed) {
                    try {
                        const res = await fetch("/app/colaboradores/desactivar/" + agrupacionGlobalId + "/" + idColaborador, {
                            method: 'POST'
                        });
                        if (!res.ok) throw new Error('Error al desactivar');
                        await Swal.fire('Desactivado', 'El colaborador ha sido desactivado.', 'success');
                        cargarColaboradoresSelector();
                    } catch (error) {
                        console.error(error);
                        Swal.fire('Error', 'No se pudo desactivar al colaborador.', 'error');
                    }
                }
            });
        });

    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `
            <tr><td colspan="3" class="text-center text-danger">Error al cargar colaboradores</td></tr>
        `;
    }
    cargarSelectColaboradoresActivos("edit-colaborador");
    cargarSelectColaboradoresActivos("modal-colaborador-select");
}

// Cargar lista cuando abres el dropdown
document.getElementById('dropdownColaboradoresBtn')?.addEventListener('click', cargarColaboradoresSelector);