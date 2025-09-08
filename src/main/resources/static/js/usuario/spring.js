const buscador = document.getElementById('buscadorUsuario');
const cuerpoTabla = document.getElementById('tablaUsuariosBody');

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
                            data-nombre="${usuario.nombre}">
                            <i class="bi bi-plus-circle"></i>
                        </button>
                    </td>


                </tr>`;
            cuerpoTabla.insertAdjacentHTML('beforeend', fila);
        });

        cuerpoTabla.querySelectorAll('.btn-agregar-usuario').forEach(btn => {
            btn.addEventListener('click', async function () {
                const username = btn.getAttribute('data-usuario');
                const nombre = btn.getAttribute('data-nombre');

                const params = new URLSearchParams();
                params.append('username', username);
                params.append('nombre', nombre);
                params.append('rolUsuario', 1);

                try {
                    const response = await fetch('/app/usuarios/spring/nuevo', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        },
                        body: params
                    });

                    if (!response.ok) {
                        const errorText = await response.text();
                        throw new Error(errorText);
                    }
                    actualizarTablaUsuarios();
                } catch (error) {
                    console.error(error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'No se pudo agregar el usuario. Puede que ya exista o hubo un error interno.',
                    });
                }
            });
        });


    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error al cargar usuarios</td></tr>`;
    }
});
