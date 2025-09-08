// Mostrar el modal al hacer clic en el icono de editar
document.addEventListener('click', function (event) {
    const target = event.target.closest('.editar-usuario');
    if (!target) return;

    const usuarioId = target.getAttribute('data-usuario-id');
    const usuarioUsername = target.getAttribute('data-usuario-username');
    const usuarioNombre = target.getAttribute('data-usuario-nombre');
    const usuarioRol = target.getAttribute('data-usuario-rol');
    const isSpringUser = target.getAttribute('data-usuario-spring') === 'true';

    const usernameInput = document.getElementById('usernameEdit');
    const passwordInput = document.getElementById('passwordEdit');
    const nombreInput = document.getElementById('nombreEdit');
    const rolSelect = document.getElementById('rolEdit');

    // Llenar valores
    usernameInput.value = usuarioUsername;
    nombreInput.value = usuarioNombre;

    // Función para bloquear visualmente los inputs
    function setReadOnlyWithStyle(input, isReadOnly) {
        input.readOnly = isReadOnly;
        input.style.backgroundColor = isReadOnly ? "#e9ecef" : "";
        input.style.cursor = isReadOnly ? "not-allowed" : "";
        input.style.opacity = "1";
    }

    setReadOnlyWithStyle(usernameInput, isSpringUser);
    setReadOnlyWithStyle(passwordInput, isSpringUser);
    setReadOnlyWithStyle(nombreInput, isSpringUser);

    // Seleccionar rol
    Array.from(rolSelect.options).forEach(option => {
        option.selected = option.text === usuarioRol;
    });

    // Actualizar action del formulario
    const form = document.getElementById('editUserForm');
    form.setAttribute('action', `/app/usuarios/actualizar/${usuarioId}`);

    // Mostrar el modal manualmente
    const modalInstance = new bootstrap.Modal(document.getElementById('editUserModal'));
    modalInstance.show();
});

// Manejo del submit
document.getElementById('editUserForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // Evita el submit tradicional

    const form = event.target;
    const actionUrl = form.getAttribute('action');
    const formData = new FormData(form);

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
