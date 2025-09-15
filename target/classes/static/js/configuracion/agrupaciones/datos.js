// Suponiendo agrupacionGlobalId está definido en el scope global si lo necesitas para filtrar
async function cargarAgrupaciones() {
    const cuerpoTabla = document.getElementById('tablaAgrupacionesBody');
    try {
        const response = await fetch("/app/agrupacion/listar");
        if (!response.ok) throw new Error('Error al obtener agrupaciones');
        const agrupaciones = await response.json();
        cuerpoTabla.innerHTML = '';

        agrupaciones.forEach((agrupacion, index) => {
            const editarBtn = `<button class="btn btn-sm btn-outline-secondary btn-editar-agrupacion" data-id="${agrupacion.id}" data-nombre="${agrupacion.nombre}"><i class="bi bi-pencil"></i></button>`;
            const estadoHTML = `
                <input type="checkbox" ${agrupacion.isActive ? 'checked' : ''} data-id="${agrupacion.id}" class="chk-estado" />
                <label>${agrupacion.isActive ? 'Visible' : 'No visible'}</label>
            `;
            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${agrupacion.nombre}</td>
                    <td class="text-center">${editarBtn}</td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Evento para cambiar estado
        cuerpoTabla.querySelectorAll('.chk-estado').forEach(chk => {
            chk.addEventListener('change', async function() {
                const agrupacionId = this.getAttribute('data-id');
                const nuevoEstado = this.checked;
                this.nextElementSibling.textContent = nuevoEstado ? 'Visible' : 'No visible';
                try {
                    const res = await fetch("/app/agrupacion/estado/" + agrupacionId, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(nuevoEstado)
                    });
                    if (!res.ok) throw new Error('No se pudo actualizar el estado');
                    cargarAgrupaciones();
                    await Swal.fire({
                        icon: 'success',
                        title: 'Estado actualizado',
                        text: 'La agrupación se ha actualizado correctamente.'
                    });
                } catch (error) {
                    await Swal.fire({
                        icon: 'error',
                        title: 'Error actualizando estado',
                        text: 'No se pudo actualizar el estado de la agrupación.'
                    });
                    this.checked = !nuevoEstado; // Revierte si hay error
                    this.nextElementSibling.textContent = !nuevoEstado ? 'Visible' : 'No visible';
                }
            });
        });

        // Evento abrir modal editar
        cuerpoTabla.querySelectorAll('.btn-editar-agrupacion').forEach(btn => {
            btn.addEventListener('click', function() {
                const agrupacionId = btn.getAttribute('data-id');
                const agrupacionNombre = btn.getAttribute('data-nombre');
                document.getElementById('inputEditarNombreAgrupacion').value = agrupacionNombre;
                document.getElementById('formEditarAgrupacion').setAttribute('data-id', agrupacionId);
                const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarAgrupacion'));
                modalEditar.show();
            });
        });
    } catch (error) {
        cuerpoTabla.innerHTML = `
            <tr><td colspan="4" class="text-center text-danger">Error al cargar las agrupaciones</td></tr>
        `;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    cargarAgrupaciones();

    // Agregar agrupación
    document.getElementById('formAgregarAgrupacion').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreAgrupacion').value.trim();
        if (!nombre) return;
        try {
            const res = await fetch('/app/agrupacion/crear', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (!res.ok) throw new Error('Error creando agrupación');
            await Swal.fire({ icon: 'success', title: 'Agrupación agregada', text: 'La agrupación fue agregada correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarAgrupacion')).hide();
            cargarAgrupaciones();
            this.reset();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo agregar la agrupación.' });
        }
    });

    // Editar agrupación
    document.getElementById('formEditarAgrupacion').addEventListener('submit', async function(e){
        e.preventDefault();
        const agrupacionId = this.getAttribute('data-id');
        const nombre = document.getElementById('inputEditarNombreAgrupacion').value.trim();
        if (!nombre || !agrupacionId) return;
        try {
            const res = await fetch(`/app/agrupacion/editar/${agrupacionId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nombre })
            });
            if (!res.ok) throw new Error('Error editando agrupación');
            await Swal.fire({ icon: 'success', title: 'Agrupación actualizada', text: 'La agrupación se actualizó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalEditarAgrupacion')).hide();
            cargarAgrupaciones();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar la agrupación.' });
        }
    });
});