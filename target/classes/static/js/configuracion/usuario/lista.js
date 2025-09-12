async function actualizarTablaUsuarios() {
    const cuerpo = document.getElementById('cuerpoUsuarios');

    try {
        const response = await fetch('/app/usuarios/activos');
        if (!response.ok) throw new Error('No se pudo obtener la lista de usuarios.');

        const usuarios = await response.json();
        cuerpo.innerHTML = '';

        usuarios.forEach((usuario, index) => {
            const estadoHTML = usuario.isActive
                ? `<span class="badge bg-success estado-toggle desactivar-usuario" data-id="${usuario.id}" style="cursor:pointer;">Activo</span>`
                : `<span class="badge bg-secondary">Inactivo</span>`;

            const fila = `
                <tr>
                    <td>${index + 1}</td>
                    <td>${usuario.username}</td>
                    <td>${usuario.springUser ? 'Sí' : 'No'}</td>
                    <td>${usuario.nombre}</td>
                    <td>${usuario.rol}</td>
                    <td class="text-center">${estadoHTML}</td>
                    <td>
                        <span class="badge bg-primary editar-usuario"
                              style="cursor:pointer;"
                              title="Editar usuario"
                              data-usuario-id="${usuario.id}"
                              data-usuario-username="${usuario.username}"
                              data-usuario-nombre="${usuario.nombre}"
                              data-usuario-rol="${usuario.rol}"
                              data-usuario-spring="${usuario.springUser}">
                            ✏️
                        </span>

                    </td>
                </tr>
            `;

            cuerpo.insertAdjacentHTML('beforeend', fila);
        });

        // 🛠️ Mover los listeners AQUÍ dentro
        document.querySelectorAll('.desactivar-usuario').forEach(span => {
            span.addEventListener('click', function () {
                const id = span.getAttribute('data-id');

                Swal.fire({
                    title: '¿Deseas desactivar este usuario?',
                    text: 'Esto desactivará al usuario.',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Sí, desactivar',
                    cancelButtonText: 'Cancelar'
                }).then(async (result) => {
                    if (result.isConfirmed) {
                        try {
                            const response = await fetch(`/app/usuarios/desactivar/${id}`, {
                                method: 'POST'
                            });

                            if (!response.ok) throw new Error('Fallo la desactivación');

                            Swal.fire({
                                icon: 'success',
                                title: 'Usuario desactivado',
                                showConfirmButton: false,
                                timer: 1500
                            });

                            actualizarTablaUsuarios(); // refresca

                        } catch (error) {
                            console.error(error);
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo desactivar el usuario'
                            });
                        }
                    }
                });
            });
        });

    } catch (error) {
        console.error(error);
        cuerpo.innerHTML = `<tr><td colspan="7" class="text-danger text-center">Error al cargar usuarios</td></tr>`;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    actualizarTablaUsuarios();
});
