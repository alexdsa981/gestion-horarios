const buscador = document.getElementById('buscadorEmpleado');
const cuerpoTabla = document.getElementById('tablaEmpleadosBody');

buscador.addEventListener('input', async function () {
    const texto = buscador.value.trim();

    if (texto.length < 2) {
        cuerpoTabla.innerHTML = '';
        return;
    }

    try {
        const response = await fetch(`/app/empleado/buscar/${texto}`);
        if (!response.ok) throw new Error('Error al buscar personal');

        const empleados = await response.json();
        cuerpoTabla.innerHTML = '';

        empleados.forEach((empleado, index) => {
            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${empleado.nombreCompleto}</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-success btn-agregar" title="Agregar"
                            data-index="${index}">
                            <i class="bi bi-plus-circle"></i>
                        </button>
                    </td>
                </tr>`;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Evento para agregar colaborador usando todos los datos del empleado seleccionado
        cuerpoTabla.querySelectorAll('.btn-agregar').forEach(btn => {
            btn.addEventListener('click', async function () {
                const index = btn.getAttribute('data-index');
                const empleado = empleados[index]; // Recupera el DTO completo recibido

                // El DTO a enviar al backend
                const dto = {
                    empleado: empleado.empleado,
                    nombreCompleto: empleado.nombreCompleto,
                    documento: empleado.documento,
                    fechaNacimiento: empleado.fechaNacimiento,
                    tipoCmp: empleado.tipoCmp,
                    tipoColegio: empleado.tipoColegio,
                    cmp: empleado.cmp,
                    idEspecialidad: empleado.idEspecialidad,
                    especialidadNombre: empleado.especialidadNombre,
                    estadoEmpleado: empleado.estadoEmpleado,
                    idAgrupacion: agrupacionGlobalId
                };

                try {
                    const response = await fetch('/app/colaboradores/guardar', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(dto)
                    });

                    if (!response.ok) throw new Error('Error al guardar colaborador');

                    await cargarColaboradores();

                    Swal.fire({
                        icon: 'success',
                        title: 'Colaborador agregado',
                        text: 'El colaborador fue agregado o actualizado correctamente.',
                        timer: 2000,
                        showConfirmButton: false
                    });

                } catch (error) {
                    console.error(error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Hubo un problema al agregar el colaborador.',
                    });
                }
            });
        });

    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error al cargar colaboradores</td></tr>`;
    }
});