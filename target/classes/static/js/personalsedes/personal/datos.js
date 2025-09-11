
async function cargarColaboradores() {
    const cuerpoTabla = document.getElementById('tablaColaboradoresBody');

    try {
        const response = await fetch("/app/colaboradores/agrupacion/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener colaboradores');

        const colaboradores = await response.json();
        cuerpoTabla.innerHTML = '';

        colaboradores.forEach((colaborador, index) => {
            const estadoHTML = colaborador.isActive
                ? `<span class="badge bg-success estado-toggle" data-id="${colaborador.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary">Inactivo</span>`;

            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${colaborador.nombreCompleto}</td>
                    <td class="text-center">
                      <input type="color" name="color" value="${colaborador.color}" data-id="${colaborador.id}">
                    </td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Evento para cambio de color y envío de DTO
        cuerpoTabla.querySelectorAll('input[type="color"][name="color"]').forEach(input => {
            input.addEventListener('change', async function () {
                const colaboradorId = this.getAttribute('data-id');
                const nuevoColor = this.value;

                // Construir el DTO
                const dto = {
                    agrupacionId: agrupacionGlobalId,
                    colaboradorId: colaboradorId,
                    color: nuevoColor
                };

                try {
                    const res = await fetch('/app/colaboradores/color', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(dto)
                    });

                    if (!res.ok) throw new Error('Error al actualizar el color');
                    Swal.fire('Color actualizado', '', 'success');
                } catch (error) {
                    console.error(error);
                    Swal.fire('Error', 'No se pudo actualizar el color.', 'error');
                }
            });
        });

        // Agregar eventos a spans activos
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
                        cargarColaboradores(); // Recargar tabla
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
            <tr><td colspan="6" class="text-center text-danger">Error al cargar colaborador</td></tr>
        `;
    }
}

document.addEventListener('DOMContentLoaded', cargarColaboradores);