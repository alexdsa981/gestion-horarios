document.addEventListener('DOMContentLoaded', function () {
    const btnBuscarUsuario = document.getElementById('btnBuscarUsuario');
    const modalBuscarUsuario = new bootstrap.Modal(document.getElementById('modalBuscarUsuario'), {
        backdrop: false // Ya lo tienes en el modal, esto es redundante pero seguro.
    });

    btnBuscarUsuario.addEventListener('click', function () {
        // Abre el modal de buscar usuario SIN cerrar el de agregar usuario
        modalBuscarUsuario.show();

        // Opcional: poner el modalBuscarUsuario encima del modalAgregarUsuario
        document.getElementById('modalBuscarUsuario').style.zIndex = 1060; // Bootstrap modals usan z-index 1050 por defecto
    });

    // Opcional: cuando se cierra el modalBuscarUsuario, devuelve el z-index
    document.getElementById('modalBuscarUsuario').addEventListener('hidden.bs.modal', function () {
        document.getElementById('modalBuscarUsuario').style.zIndex = 1050;
    });
});




document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('addUserForm');

    // Función para limpiar el formulario
    function limpiarFormulario() {
        document.getElementById('usernameAdd').value = '';
        document.getElementById('nombreAdd').value = '';
        document.getElementById('rolAdd').selectedIndex = 0; // Primer elemento (deshabilitado)

        // Limpiar agrupaciones seleccionadas
        document.getElementById('agrupacionesSeleccionadas').value = '';
        // Quitar la clase 'selected' de todas las etiquetas
        const agrupaciones = document.querySelectorAll('#agrupacionesContainer .agrupacion-label.selected');
        agrupaciones.forEach(label => label.classList.remove('selected'));
    }

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Evita el submit clásico

        // Obtén los valores del formulario
        const username = document.getElementById('usernameAdd').value.trim();
        const nombre = document.getElementById('nombreAdd').value.trim();
        const rolId = document.getElementById('rolAdd').value;
        const agrupacionesCSV = document.getElementById('agrupacionesSeleccionadas').value.trim();

        // Validaciones
        if (!username) {
            Swal.fire({
                icon: 'warning',
                title: 'Usuario requerido',
                text: 'Debes seleccionar un usuario.'
            });
            return;
        }
        if (!nombre) {
            Swal.fire({
                icon: 'warning',
                title: 'Nombre requerido',
                text: 'El nombre no puede estar vacío.'
            });
            return;
        }
        if (!rolId) {
            Swal.fire({
                icon: 'warning',
                title: 'Rol requerido',
                text: 'Debes asignar un rol al usuario.'
            });
            return;
        }
        if (!agrupacionesCSV) {
            Swal.fire({
                icon: 'warning',
                title: 'Agrupaciones requeridas',
                text: 'Debes seleccionar al menos una agrupación.'
            });
            return;
        }

        // Construye el objeto para enviar
        const datos = {
            username: username,
            nombre: nombre,
            rolUsuario: rolId,
            agrupacionesSeleccionadas: agrupacionesCSV
        };

        // Enviar por fetch (AJAX)
        fetch('/app/usuarios/spring/nuevo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        })
        .then(response => response.text())
        .then(msg => {
            Swal.fire({
                icon: 'success',
                title: 'Operación exitosa',
                text: msg
            });
            bootstrap.Modal.getInstance(document.getElementById('addUserModal')).hide();
            limpiarFormulario(); // <--- Limpia el formulario aquí
            actualizarTablaUsuarios();
        })
        .catch(err => {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Error al crear/actualizar usuario'
            });
            console.error(err);
        });
    });
});