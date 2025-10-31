window.listaColaboradoresPorAgrupacion = [];


window.colaboradorSeleccionadoId = null;

async function cargarColaboradoresSelector() {
    const cuerpoTabla = document.getElementById('tablaColaboradoresBody');
    const btnQuitarFiltro = document.getElementById('btnQuitarFiltroColaborador');
    if (!cuerpoTabla) return;

    try {
        const response = await fetch("/app/colaboradores/agrupacion/" + agrupacionGlobalId);
        if (!response.ok) throw new Error('Error al obtener colaboradores');

        const colaboradores = await response.json();
        window.listaColaboradoresPorAgrupacion = colaboradores;

        cuerpoTabla.innerHTML = '';

        colaboradores.forEach((colaborador, index) => {
            const fila = document.createElement('tr');
            fila.innerHTML = `
                <td>
                    <span class="d-flex align-items-center gap-2">
                        <span style="background:${colaborador.color};width:1.2em;height:1.2em;display:inline-block;border-radius:50%;border:1px solid #ccc"></span>
                        ${colaborador.nombreCompleto}
                    </span>
                </td>
            `;
            fila.style.cursor = "pointer";
            fila.addEventListener('click', () => {
                window.colaboradorSeleccionadoId = colaborador.id;
                filtrarPorColaborador(colaborador.id);
                actualizarResaltadoColaborador();
                btnQuitarFiltro.style.display = "block";
            });
            if (colaborador.id === window.colaboradorSeleccionadoId) {
                fila.classList.add("fila-colaborador-seleccionada");
            }
            cuerpoTabla.appendChild(fila);
        });

        btnQuitarFiltro.style.display = window.colaboradorSeleccionadoId ? "block" : "none";
        btnQuitarFiltro.onclick = function() {
            window.colaboradorSeleccionadoId = null;
            renderizarCalendarioActual();
            actualizarResaltadoColaborador();
            this.style.display = "none";
        };

    } catch (error) {
        console.error(error);
        cuerpoTabla.innerHTML = `
            <tr><td colspan="3" class="text-center text-danger">Error al cargar colaboradores</td></tr>
        `;
        if (btnQuitarFiltro) btnQuitarFiltro.style.display = "none";
    }
}

// Para cambiar el color de la fila seleccionada
function actualizarResaltadoColaborador() {
    const filas = document.querySelectorAll("#tablaColaboradoresBody tr");
    filas.forEach((fila, idx) => {
        const colaborador = window.listaColaboradoresPorAgrupacion[idx];
        if (colaborador && colaborador.id === window.colaboradorSeleccionadoId) {
            fila.classList.add("fila-colaborador-seleccionada");
        } else {
            fila.classList.remove("fila-colaborador-seleccionada");
        }
    });
}


async function filtrarPorColaborador(colaboradorId) {
    const ano = Number(document.getElementById("selectorAno").value);
    const mes = Number(document.getElementById("selectorMes").value);
    const { desde, hasta } = getFechaRango(ano, mes);

    const filtro = {
        colaboradorId,
        agrupacionId: agrupacionGlobalId,
        inicioMes: desde,
        finMes: hasta
    };

    const response = await fetch("/app/bloque-horarios/bloques-colaborador", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(filtro)
    });
    if (!response.ok) {
        alert("Error al cargar bloques del colaborador");
        return;
    }
    const data = await response.json();
    //console.log(data);
    window.columnas = data.sedes.map(sede => ({ name: sede.nombre, id: sede.id }));
    window.listaSedesActivasPorAgrupacion = data.sedes;

    renderizarMesGrid(desde, hasta, ano, mes, window.columnas, data.bloques);
}