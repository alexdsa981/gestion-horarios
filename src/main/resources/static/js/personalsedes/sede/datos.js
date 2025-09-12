async function cargarSedes() {
    const cuerpoTabla = document.getElementById('tablaSedesBody');
    try {
        const response = await fetch("/app/sedes/listar/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener sedes');
        const sedes = await response.json();
        cuerpoTabla.innerHTML = '';
        sedes.forEach((sede, index) => {
            const fila = `
                <tr>
                    <td class="text-center">${index + 1}</td>
                    <td>${sede.nombre}</td>
                    <td class="text-center">
                        <input type="checkbox" ${sede.isActive ? 'checked' : ''} data-id="${sede.id}" class="chk-estado" />
                        <label>${sede.isActive ? 'Visible' : 'No visible'}</label>
                    </td>
                </tr>
            `;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        // Opcional: evento para cambiar estado (requiere endpoint en backend)
        cuerpoTabla.querySelectorAll('.chk-estado').forEach(chk => {
            chk.addEventListener('change', async function() {
                const sedeId = this.getAttribute('data-id');
                const nuevoEstado = this.checked;
                // Actualiza el label al cambiar el estado
                this.nextElementSibling.textContent = nuevoEstado ? 'Visible' : 'No visible';
                try {
                    const res = await fetch("/app/sedes/estado/" + agrupacionGlobalId + "/" + sedeId, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify(nuevoEstado)
                    });
                    if (!res.ok) throw new Error('No se pudo actualizar el estado');
                    cargarSedes();
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
                    this.checked = !nuevoEstado; // Revierte si hay error
                    this.nextElementSibling.textContent = !nuevoEstado ? 'Visible' : 'No visible';
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