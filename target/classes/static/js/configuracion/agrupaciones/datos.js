// Carga departamentos y genera el acordeón
async function cargarDepartamentosYAccordion() {
    const contenedor = document.getElementById('departamentosAccordion');
    contenedor.innerHTML = '<div class="text-center text-secondary py-3">Cargando...</div>';

    // 1. Obtener departamentos
    const resp = await fetch('/app/departamento/listar');
    if (!resp.ok) {
        contenedor.innerHTML = '<div class="text-danger">Error al cargar departamentos</div>';
        return;
    }
    const departamentos = await resp.json();
    if (!departamentos.length) {
        contenedor.innerHTML = '<div class="text-muted">No hay departamentos registrados</div>';
        return;
    }

    // 2. Renderizar acordeones vacíos
    contenedor.innerHTML = departamentos.map((dep, idx) => `
        <div class="accordion-item">
            <h2 class="accordion-header" id="heading${dep.id}">
                <button class="accordion-button collapsed" type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapse${dep.id}"
                        aria-expanded="false"
                        aria-controls="collapse${dep.id}">
                    <i class="bi bi-building me-2"></i> ${dep.nombre}
                </button>
            </h2>
            <div id="collapse${dep.id}" class="accordion-collapse collapse"
                 aria-labelledby="heading${dep.id}" data-bs-parent="#departamentosAccordion">
                <div class="accordion-body p-0">
                    <div class="agrupaciones-loading text-center py-2 text-muted" id="loading${dep.id}" style="display:none;">Cargando agrupaciones...</div>
                    <table class="table table-sm table-bordered align-middle mb-0" style="min-width:350px;">
                        <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Agrupación</th>
                            <th>Editar</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody id="tablaAgrupacionesBody${dep.id}">
                        <!-- Agrupaciones aquí -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `).join('');

    // 3. Evento para cargar agrupaciones al expandir un acordeón
    departamentos.forEach(dep => {
        const collapseEl = document.getElementById(`collapse${dep.id}`);
        collapseEl.addEventListener('show.bs.collapse', async () => {
            await cargarAgrupacionesPorDepartamento(dep.id);
        });
    });

    // Llenar select de departamento en el modal agregar
    const selectDep = document.getElementById('inputDepartamentoAgrupacion');
    if (selectDep) {
        selectDep.innerHTML = departamentos.map(dep =>
            `<option value="${dep.id}">${dep.nombre}</option>`
        ).join('');
    }
}

// Carga agrupaciones de un departamento específico
async function cargarAgrupacionesPorDepartamento(idDepartamento) {
    const tbody = document.getElementById(`tablaAgrupacionesBody${idDepartamento}`);
    const loading = document.getElementById(`loading${idDepartamento}`);
    tbody.innerHTML = '';
    loading.style.display = 'block';

    try {
        const resp = await fetch(`/app/agrupacion/listar/${idDepartamento}`);
        if (!resp.ok) throw new Error();
        const agrupaciones = await resp.json();
        loading.style.display = 'none';

        if (!agrupaciones.length) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">No hay agrupaciones</td></tr>`;
            return;
        }

        agrupaciones.forEach((agrupacion, idx) => {
            const editarBtn = `<button class="btn btn-sm btn-outline-secondary btn-editar-agrupacion" data-id="${agrupacion.id}" data-nombre="${agrupacion.nombre}" data-iddepartamento="${agrupacion.idDepartamento}"><i class="bi bi-pencil"></i></button>`;
            const estadoHTML = `
                <input type="checkbox" ${agrupacion.isActive ? 'checked' : ''} data-id="${agrupacion.id}" class="chk-estado" />
                <label>${agrupacion.isActive ? 'Visible' : 'No visible'}</label>
            `;
            const fila = `
                <tr>
                    <td class="text-center">${idx + 1}</td>
                    <td>${agrupacion.nombre}</td>
                    <td class="text-center">${editarBtn}</td>
                    <td class="text-center">${estadoHTML}</td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', fila);
        });

        // Eventos editar y estado:
        tbody.querySelectorAll('.btn-editar-agrupacion').forEach(btn => {
            btn.addEventListener('click', function() {
                document.getElementById('inputEditarNombreAgrupacion').value = btn.getAttribute('data-nombre');
                document.getElementById('formEditarAgrupacion').setAttribute('data-id', btn.getAttribute('data-id'));
                const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarAgrupacion'));
                modalEditar.show();
            });
        });

        tbody.querySelectorAll('.chk-estado').forEach(chk => {
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
                    await Swal.fire({
                        icon: 'success',
                        title: 'Estado actualizado',
                        text: 'La agrupación se ha actualizado correctamente.'
                    });
                    cargarAgrupacionesPorDepartamento(idDepartamento);
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
    } catch {
        loading.style.display = 'none';
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Error al cargar agrupaciones</td></tr>`;
    }
}

// Agrega agrupación
document.addEventListener('DOMContentLoaded', () => {
    cargarDepartamentosYAccordion();

    // Agregar agrupación
    document.getElementById('formAgregarAgrupacion').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreAgrupacion').value.trim();
        const idDepartamento = document.getElementById('inputDepartamentoAgrupacion').value;
        if (!nombre || !idDepartamento) return;
        try {
            const res = await fetch('/app/agrupacion/crear', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nombre,
                    departamento: { id: idDepartamento }
                })
            });
            if (!res.ok) throw new Error('Error creando agrupación');
            await Swal.fire({ icon: 'success', title: 'Agrupación agregada', text: 'La agrupación fue agregada correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarAgrupacion')).hide();
            cargarDepartamentosYAccordion();
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
            cargarDepartamentosYAccordion();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar la agrupación.' });
        }
    });
});