
function obtenerAcordionesAbiertos() {
    return Array.from(document.querySelectorAll('.accordion-collapse.show'))
        .map(el => el.id.replace('collapse', ''));
}

async function cargarDepartamentosYAccordion(abrirIds) {
    const contenedor = document.getElementById('departamentosAccordion');
    contenedor.innerHTML = '<div class="text-center text-secondary py-3">Cargando...</div>';

    let departamentos;
    try {
        departamentos = await obtenerDepartamentos();
    } catch {
        contenedor.innerHTML = '<div class="text-danger">Error al cargar áreas</div>';
        return;
    }

    if (!departamentos.length) {
        contenedor.innerHTML = '<div class="text-muted">No hay áreas registradas</div>';
        return;
    }

    contenedor.innerHTML = departamentos.map(dep => `
        <div class="accordion-item">
            <div class="accordion-header d-flex align-items-center justify-content-between px-3" id="heading${dep.id}" style="background:transparent;">
                <button class="accordion-button collapsed flex-grow-1" type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#collapse${dep.id}"
                        aria-expanded="false"
                        aria-controls="collapse${dep.id}">
                    <i class="bi bi-building me-2"></i> ${dep.nombre}
                </button>
                <div class="d-flex align-items-center gap-2 ms-2">
                    <button class="btn btn-sm btn-outline-secondary btn-editar-area"
                            data-id="${dep.id}" data-nombre="${dep.nombre}">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <div class="form-check form-check-inline m-0">
                        <input type="checkbox" class="form-check-input chk-estado-area" id="chkArea${dep.id}"
                            ${dep.isActive ? 'checked' : ''} data-id="${dep.id}">
                        <label class="form-check-label fw-bold ${dep.isActive ? 'text-dark' : 'text-secondary'}"
                            for="chkArea${dep.id}">
                            ${dep.isActive ? 'Activa' : 'Inactiva'}
                        </label>
                    </div>
                </div>
            </div>
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
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `).join('');

    // Evento para cargar agrupaciones al expandir un acordeón
    departamentos.forEach(dep => {
        const collapseEl = document.getElementById(`collapse${dep.id}`);
        collapseEl.addEventListener('show.bs.collapse', async () => {
            await cargarAgrupacionesPorDepartamento(dep.id);
        });
    });

    // --- Eventos de área/Departamento ---
    contenedor.querySelectorAll('.btn-editar-area').forEach(btn => {
        btn.addEventListener('click', function() {
            document.getElementById('inputEditarNombreArea').value = btn.getAttribute('data-nombre');
            document.getElementById('formEditarArea').setAttribute('data-id', btn.getAttribute('data-id'));
            const modalEditar = new bootstrap.Modal(document.getElementById('modalEditarArea'));
            modalEditar.show();
        });
    });

    contenedor.querySelectorAll('.chk-estado-area').forEach(chk => {
        chk.addEventListener('change', async function() {
            const departamentoId = this.getAttribute('data-id');
            const nuevoEstado = this.checked;
            const label = contenedor.querySelector(`label[for="chkArea${departamentoId}"]`);
            label.textContent = nuevoEstado ? 'Activa' : 'Inactiva';
            label.classList.toggle('text-dark', nuevoEstado);
            label.classList.toggle('text-secondary', !nuevoEstado);
            try {
                await actualizarEstadoDepartamento(departamentoId, nuevoEstado);
                await Swal.fire({
                    icon: 'success',
                    title: 'Estado actualizado',
                    text: 'El área se ha actualizado correctamente.'
                });
                const abiertos = obtenerAcordionesAbiertos();
                await cargarDepartamentosYAccordion(abiertos);
            } catch (error) {
                await Swal.fire({
                    icon: 'error',
                    title: 'Error actualizando estado',
                    text: 'No se pudo actualizar el estado del área.'
                });
                this.checked = !nuevoEstado;
                label.textContent = !nuevoEstado ? 'Inactiva' : 'Activa';
                label.classList.toggle('text-dark', !nuevoEstado);
                label.classList.toggle('text-secondary', nuevoEstado);
            }
        });
    });

    // Llenar select de departamento en el modal agregar agrupación
    const selectDep = document.getElementById('inputDepartamentoAgrupacion');
    if (selectDep) {
        selectDep.innerHTML = departamentos.map(dep =>
            `<option value="${dep.id}">${dep.nombre}</option>`
        ).join('');
    }

    // Restaurar los acordeones abiertos
    if (Array.isArray(abrirIds)) {
        abrirIds.forEach(function(id) {
            const collapseEl = document.getElementById(`collapse${id}`);
            if (collapseEl) {
                const collapseInstance = bootstrap.Collapse.getOrCreateInstance(collapseEl);
                collapseInstance.show();
            }
        });
    }
}


async function cargarAgrupacionesPorDepartamento(idDepartamento) {
    const tbody = document.getElementById(`tablaAgrupacionesBody${idDepartamento}`);
    const loading = document.getElementById(`loading${idDepartamento}`);
    tbody.innerHTML = '';
    loading.style.display = 'block';

    try {
        const agrupaciones = await obtenerAgrupaciones(idDepartamento);
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
                    await actualizarEstadoAgrupacion(agrupacionId, nuevoEstado); // <-- SOLO el booleano
                    await Swal.fire({
                        icon: 'success',
                        title: 'Estado actualizado',
                        text: 'La agrupación se ha actualizado correctamente.'
                    });
                    await cargarAgrupacionesPorDepartamento(idDepartamento);
                } catch (error) {
                    await Swal.fire({
                        icon: 'error',
                        title: 'Error actualizando estado',
                        text: 'No se pudo actualizar el estado de la agrupación.'
                    });
                    this.checked = !nuevoEstado;
                    this.nextElementSibling.textContent = !nuevoEstado ? 'Visible' : 'No visible';
                }
            });
        });

    } catch {
        loading.style.display = 'none';
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Error al cargar agrupaciones</td></tr>`;
    }
}