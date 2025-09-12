document.addEventListener('click', async function (event) {
    const target = event.target.closest('.editar-usuario');
    if (!target) return;

    const usuarioId = target.getAttribute('data-usuario-id');
    const usuarioUsername = target.getAttribute('data-usuario-username');
    const usuarioNombre = target.getAttribute('data-usuario-nombre');
    const usuarioRol = target.getAttribute('data-usuario-rol');
    const isSpringUser = target.getAttribute('data-usuario-spring') === 'true';

    // Inputs
    const usernameInput = document.getElementById('usernameEdit');
    const passwordInput = document.getElementById('passwordEdit');
    const nombreInput = document.getElementById('nombreEdit');
    const rolSelect = document.getElementById('rolEdit');
    const agrupacionesContainer = document.getElementById('agrupacionesContainerEdit');
    const inputAgrupaciones = document.getElementById('agrupacionesSeleccionadasEdit');

    // Llenar valores
    usernameInput.value = usuarioUsername;
    nombreInput.value = usuarioNombre;

    function setReadOnlyWithStyle(input, isReadOnly) {
        input.readOnly = isReadOnly;
        input.style.backgroundColor = isReadOnly ? "#e9ecef" : "";
        input.style.cursor = isReadOnly ? "not-allowed" : "";
        input.style.opacity = "1";
    }

    setReadOnlyWithStyle(usernameInput, isSpringUser);
    setReadOnlyWithStyle(passwordInput, isSpringUser);
    setReadOnlyWithStyle(nombreInput, isSpringUser);

    Array.from(rolSelect.options).forEach(option => {
        option.selected = option.text === usuarioRol;
    });

    // Actualizar action del formulario
    const form = document.getElementById('editUserForm');
    form.setAttribute('action', `/app/usuarios/actualizar/${usuarioId}`);

    // --- NUEVO: cargar agrupaciones y seleccionadas ---
    // 1. Trae todas las agrupaciones activas
    const todasAgrupacionesResp = await fetch('/app/agrupacion/listar-activos');
    const todasAgrupaciones = await todasAgrupacionesResp.json();

    // 2. Trae las agrupaciones que tiene el usuario
    const usuarioAgrupacionesResp = await fetch(`/app/agrupacion/listar-activos/usuario/${usuarioId}`);
    const usuarioAgrupaciones = await usuarioAgrupacionesResp.json();
    const usuarioAgrupacionesIds = usuarioAgrupaciones.map(a => a.id);

    // 3. Renderiza las etiquetas y marca las que corresponden
    agrupacionesContainer.innerHTML = '';
    todasAgrupaciones.forEach(agrup => {
        const tag = document.createElement('span');
        tag.className = 'agrupacion-label';
        tag.textContent = agrup.nombre;
        tag.dataset.id = agrup.id;
        if (usuarioAgrupacionesIds.includes(agrup.id)) tag.classList.add('selected');
        agrupacionesContainer.appendChild(tag);
    });

    // 4. Actualiza el input hidden con las seleccionadas
    function actualizarSeleccionAgrupacionesEdit() {
        const seleccionadas = Array.from(agrupacionesContainer.querySelectorAll('.agrupacion-label.selected'))
            .map(tag => tag.dataset.id);
        inputAgrupaciones.value = seleccionadas.join(',');
    }
    actualizarSeleccionAgrupacionesEdit();

    // 5. Maneja clicks en agrupacionesContainerEdit
    agrupacionesContainer.onclick = function (e) {
        const label = e.target.closest('.agrupacion-label');
        if (label) {
            label.classList.toggle('selected');
            actualizarSeleccionAgrupacionesEdit();
        }
    };

    // Mostrar el modal manualmente
    const modalInstance = new bootstrap.Modal(document.getElementById('editUserModal'));
    modalInstance.show();
});

// Manejo del submit
document.getElementById('editUserForm').addEventListener('submit', async function (event) {
    event.preventDefault();

    const form = event.target;
    const actionUrl = form.getAttribute('action');
    const formData = new FormData(form);

    // Validación: al menos una agrupación seleccionada
    if (!formData.get('agrupacionesSeleccionadas')) {
        Swal.fire({
            icon: 'warning',
            title: 'Agrupaciones requeridas',
            text: 'Debes seleccionar al menos una agrupación.'
        });
        return;
    }

    try {
        const response = await fetch(actionUrl, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            const mensaje = await response.text();
            Swal.fire({
                icon: 'error',
                title: 'Error al actualizar',
                text: mensaje
            });
            return;
        }

        // Cierra el modal
        const modalInstance = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
        modalInstance.hide();

        Swal.fire({
            icon: 'success',
            title: 'Usuario actualizado',
            showConfirmButton: false,
            timer: 1500
        });

        actualizarTablaUsuarios();

    } catch (error) {
        console.error(error);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo actualizar el usuario'
        });
    }
});