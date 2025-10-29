document.addEventListener('DOMContentLoaded', function () {
    const buscador = document.getElementById('buscadorUsuario');
    const cuerpoTabla = document.getElementById('tablaUsuariosBody');
    const usernameAdd = document.getElementById('usernameAdd');
    const nombreAdd = document.getElementById('nombreAdd');
    // Si quieres almacenar otros datos, crea inputs hidden en el modal de agregar usuario
    const hiddenFields = { 
        // ejemplo: idAdd: document.getElementById('idAdd')
    };

    buscador.addEventListener('input', async function () {
        const texto = buscador.value.trim();

        if (texto.length < 2) {
            cuerpoTabla.innerHTML = '';
            return;
        }

        try {
            const response = await fetch(`/app/usuarios/spring/buscar?nombre=${encodeURIComponent(texto)}`);
            if (!response.ok) throw new Error('Error al buscar usuarios');

            const usuarios = await response.json();
            cuerpoTabla.innerHTML = '';

            usuarios.forEach((usuario, index) => {
                const fila = `
                    <tr>
                        <td class="text-center">${index + 1}</td>
                        <td>${usuario.usuario}</td>
                        <td>${usuario.nombre}</td>
                        <td class="text-center">
                            <button class="btn btn-sm btn-success btn-agregar-usuario" title="Agregar"
                                data-usuario="${usuario.usuario}"
                                data-nombre="${usuario.nombre}"
                                data-id="${usuario.id || ''}">
                                <i class="bi bi-plus-circle"></i>
                            </button>
                        </td>
                    </tr>`;
                cuerpoTabla.insertAdjacentHTML('beforeend', fila);
            });

            cuerpoTabla.querySelectorAll('.btn-agregar-usuario').forEach(btn => {
                btn.addEventListener('click', function () {
                    const username = btn.getAttribute('data-usuario');
                    const nombre = btn.getAttribute('data-nombre');
                    // Si tienes más datos, obténlos aquí
                    const id = btn.getAttribute('data-id');

                    // Poner los datos en el modal de agregar usuario
                    usernameAdd.value = username;
                    nombreAdd.value = nombre;
                    // Si tienes más campos ocultos, setéalos
                    // hiddenFields.idAdd.value = id;

                    // Cierra el modal de búsqueda
                    const modalBuscarUsuario = bootstrap.Modal.getInstance(document.getElementById('modalBuscarUsuario'));
                    modalBuscarUsuario.hide();
                });
            });

        } catch (error) {
            console.error(error);
            cuerpoTabla.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error al cargar usuarios</td></tr>`;
        }
    });
});