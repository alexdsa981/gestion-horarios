// Añadir botón de editar a cada fila y abrir el modal de edición con el nombre actual

async function cargarSedes() {
    const cuerpoTabla = document.getElementById('tablaSedesBody');
    try {
        const response = await fetch("/app/sedes/listar");
        if (!response.ok) throw new Error('Error al obtener sedes');
        const sedes = await response.json();
        cuerpoTabla.innerHTML = '';

        sedes.forEach((sede, index) => {
            const editarBtn = `<button class="btn btn-sm btn-outline-secondary btn-editar-sede" data-id="${sede.id}" data-nombre="${sede.nombre}"><i class="bi bi-pencil"></i></button>`;
            const estadoHTML = sede.isActive
                ? `<span class="badge bg-success estado-toggle" data-id="${sede.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary estado-toggle" data-id="${sede.id}" style="cursor:pointer;">Inactivo</span>`;

            // MODIFICA ESTE BLOQUE:
            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${sede.nombre}</td>
                    <td class="text-center">${editarBtn}</td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });
        // Alternar estado
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

        // Evento abrir modal editar
        cuerpoTabla.querySelectorAll('.btn-editar-sede').forEach(btn => {
            btn.addEventListener('click', function() {
                const sedeId = btn.getAttribute('data-id');
                const sedeNombre = btn.getAttribute('data-nombre');
                document.getElementById('inputEditarNombreSede').value = sedeNombre;
                document.getElementById('formEditarSede').setAttribute('data-id', sedeId);
                const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarSede'));
                modalEditar.show();
            });
        });

    } catch (error) {
        cuerpoTabla.innerHTML = `
            <tr><td colspan="3" class="text-center text-danger">Error al cargar las sedes</td></tr>
        `;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    cargarSedes();

    // Agregar sede
    document.getElementById('formAgregarSede').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreSede').value.trim();
        if (!nombre) return;
        try {
            const res = await fetch('/app/sedes/crear', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (!res.ok) throw new Error('Error creando sede');
            await Swal.fire({ icon: 'success', title: 'Sede agregada', text: 'La sede fue agregada correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarSede')).hide();
            cargarSedes();
            this.reset();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo agregar la sede.' });
        }
    });

    // Editar sede
    document.getElementById('formEditarSede').addEventListener('submit', async function(e){
        e.preventDefault();
        const sedeId = this.getAttribute('data-id');
        const nombre = document.getElementById('inputEditarNombreSede').value.trim();
        if (!nombre || !sedeId) return;
        try {
            const res = await fetch(`/app/sedes/editar/${sedeId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (!res.ok) throw new Error('Error editando sede');
            await Swal.fire({ icon: 'success', title: 'Sede actualizada', text: 'La sede se actualizó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalEditarSede')).hide();
            cargarSedes();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar la sede.' });
        }
    });
});