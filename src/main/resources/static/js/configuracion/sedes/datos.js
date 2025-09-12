async function cargarSedes() {
    const cuerpoTabla = document.getElementById('tablaSedesBody');
    try {
        const response = await fetch("/app/sedes/listar");
        if (!response.ok) throw new Error('Error al obtener sedes');
        const sedes = await response.json();
        cuerpoTabla.innerHTML = '';

        // Pintamos las filas con badges
        sedes.forEach((sede, index) => {
            const estadoHTML = sede.isActive
                ? `<span class="badge bg-success estado-toggle" data-id="${sede.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary estado-toggle" data-id="${sede.id}" style="cursor:pointer;">Inactivo</span>`;

            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${sede.nombre}</td>
                    <td class="text-center">
                        ${estadoHTML}
                    </td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Eventos de click para alternar el estado
        cuerpoTabla.querySelectorAll('.estado-toggle').forEach(span => {
            span.addEventListener('click', async function() {
                const sedeId = this.getAttribute('data-id');
                const nuevoEstado = this.textContent.trim() === 'Activo' ? false : true;

                try {
                    const res = await fetch(`/app/sedes/estado/${sedeId}`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(nuevoEstado)
                    });
                    if (!res.ok) throw new Error('No se pudo actualizar el estado');

                    // Actualizar visualmente el badge
                    if (nuevoEstado) {
                        this.textContent = 'Activo';
                        this.classList.remove('bg-secondary');
                        this.classList.add('bg-success');
                    } else {
                        this.textContent = 'Inactivo';
                        this.classList.remove('bg-success');
                        this.classList.add('bg-secondary');
                    }

                    await Swal.fire({
                        icon: 'success',
                        title: 'Estado actualizado',
                        text: 'La sede se ha actualizado correctamente.'
                    });
                } catch (error) {
                    await Swal.fire({
                        icon: 'error',
                        title: 'Error actualizando estado',
                        text: 'No se pudo actualizar el estado de la sede.'
                    });
                }
            });
        });

    } catch (error) {
        cuerpoTabla.innerHTML = `
            <tr><td colspan="3" class="text-center text-danger">Error al cargar las sedes</td></tr>
        `;
    }
}

document.addEventListener('DOMContentLoaded', cargarSedes);
