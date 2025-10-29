
async function cargarTiposLicencias() {
    const cuerpoTabla = document.getElementById('tablaTiposLicenciasBody');
    try {
        const response = await fetch("/app/tipo-licencias");
        if (!response.ok) throw new Error('Error al obtener tipos de licencia');
        const tipos = await response.json();
        cuerpoTabla.innerHTML = '';

        tipos.forEach((tipo, index) => {
            const editarBtn = `<button class="btn btn-sm btn-outline-secondary btn-editar-tipo" data-id="${tipo.id}" data-nombre="${tipo.nombre}"><i class="bi bi-pencil"></i></button>`;
            const estadoHTML = tipo.isActive
                ? `<span class="badge bg-success estado-toggle" data-id="${tipo.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary estado-toggle" data-id="${tipo.id}" style="cursor:pointer;">Inactivo</span>`;

            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${tipo.nombre}</td>
                    <td class="text-center">${editarBtn}</td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Alternar estado
        cuerpoTabla.querySelectorAll('.estado-toggle').forEach(span => {
            span.addEventListener('click', async function() {
                const tipoId = this.getAttribute('data-id');
                const esActivo = this.textContent.trim() === 'Activo';
                let url = `/app/tipo-licencias/${tipoId}/${esActivo ? 'desactivar' : 'activar'}`;
                try {
                    const res = await fetch(url, {
                        method: 'POST'
                    });
                    if (!res.ok) throw new Error('No se pudo actualizar el estado');
                    // Actualizar visualmente el badge
                    if (esActivo) {
                        this.textContent = 'Inactivo';
                        this.classList.remove('bg-success');
                        this.classList.add('bg-secondary');
                    } else {
                        this.textContent = 'Activo';
                        this.classList.remove('bg-secondary');
                        this.classList.add('bg-success');
                    }
                    await Swal.fire({
                        icon: 'success',
                        title: 'Estado actualizado',
                        text: 'El tipo de licencia se ha actualizado correctamente.'
                    });
                } catch (error) {
                    await Swal.fire({
                        icon: 'error',
                        title: 'Error actualizando estado',
                        text: 'No se pudo actualizar el estado del tipo de licencia.'
                    });
                }
            });
        });

        // Evento abrir modal editar
        cuerpoTabla.querySelectorAll('.btn-editar-tipo').forEach(btn => {
            btn.addEventListener('click', function() {
                const tipoId = btn.getAttribute('data-id');
                const tipoNombre = btn.getAttribute('data-nombre');
                document.getElementById('inputEditarNombreTipoLicencia').value = tipoNombre;
                document.getElementById('formEditarTipoLicencia').setAttribute('data-id', tipoId);
                const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarTipoLicencia'));
                modalEditar.show();
            });
        });

    } catch (error) {
        cuerpoTabla.innerHTML = `
            <tr><td colspan="4" class="text-center text-danger">Error al cargar los tipos de licencia</td></tr>
        `;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    cargarTiposLicencias();

    // Agregar tipo de licencia
    document.getElementById('formAgregarTipoLicencia').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreTipoLicencia').value.trim();
        if (!nombre) return;
        try {
            const res = await fetch('/app/tipo-licencias/crear', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })


            });
            if (!res.ok) throw new Error('Error creando tipo de licencia');
            await Swal.fire({ icon: 'success', title: 'Tipo de licencia agregado', text: 'Se agregó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarTipoLicencia')).hide();
            cargarTiposLicencias();
            this.reset();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo agregar el tipo de licencia.' });
        }
    });

    // Editar tipo de licencia
    document.getElementById('formEditarTipoLicencia').addEventListener('submit', async function(e){
        e.preventDefault();
        const tipoId = this.getAttribute('data-id');
        const nombre = document.getElementById('inputEditarNombreTipoLicencia').value.trim();
        if (!nombre || !tipoId) return;
        try {
            const res = await fetch(`/app/tipo-licencias/${tipoId}/nombre`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (!res.ok) throw new Error('Error editando tipo de licencia');
            await Swal.fire({ icon: 'success', title: 'Tipo de licencia actualizado', text: 'Se actualizó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalEditarTipoLicencia')).hide();
            cargarTiposLicencias();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar el tipo de licencia.' });
        }
    });
});