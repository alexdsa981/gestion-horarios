// Rellena el selector de año (por defecto actual, muestra últimos 5 años)
function llenarSelectorAnioVacaciones() {
    const selector = document.getElementById("selectorAnioVacaciones");
    if (!selector) return;
    const actual = new Date().getFullYear();
    selector.innerHTML = "";
    for (let y = actual; y >= actual - 5; y--) {
        const opt = document.createElement("option");
        opt.value = y;
        opt.textContent = y;
        selector.appendChild(opt);
    }
    selector.value = actual;
}

// Llama a esta función cuando quieras abrir el modal de un colaborador
async function mostrarVacacionesColaborador(colaboradorId, nombreColab) {
    llenarSelectorAnioVacaciones();
    // Setea el nombre del colaborador en el espacio dedicado
    document.getElementById("vacacionesColaboradorNombre").textContent = nombreColab;

    document.getElementById("selectorAnioVacaciones").onchange = function() {
        cargarFechasVacacionesColaborador(colaboradorId);
    };

    cargarFechasVacacionesColaborador(colaboradorId);

    const modal = new bootstrap.Modal(document.getElementById('modalVacacionesColaborador'));
    modal.show();
}

async function cargarFechasVacacionesColaborador(colaboradorId) {
    const anio = Number(document.getElementById("selectorAnioVacaciones").value);
    const cont = document.getElementById("vacacionesColaboradorContenido");
    cont.innerHTML = '<div class="text-secondary"><i class="bi bi-clock"></i> Cargando...</div>';

    try {
        const response = await fetch(`/app/licencias/colaborador/fechas-vacaciones?colaboradorId=${colaboradorId}&agrupacionId=${agrupacionGlobalId}&anio=${anio}`);
        if (!response.ok) throw new Error("No se pudieron cargar las vacaciones.");
        const data = await response.json();

        cont.innerHTML = `
            <div>
                <strong>Vacaciones/licencias en ${anio}:</strong>
                ${data.fechas.length > 0
                    ? agruparFechasPorMesTabla(data.fechas)
                    : '<div class="text-muted">No hay fechas</div>'}
            </div>
        `;
    } catch (error) {
        cont.innerHTML = '<div class="text-danger">Error al cargar vacaciones</div>';
    }
}

function agruparFechasPorMesTabla(fechas) {
    const meses = [
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    ];
    const agrupado = {};
    fechas.forEach(f => {
        // ¡Ojo! Siempre parsea como UTC
        const fechaObj = new Date(f.fecha + "T00:00:00Z");
        const mes = fechaObj.getUTCMonth(); // 0-indexed
        if (!agrupado[mes]) agrupado[mes] = [];
        agrupado[mes].push({
            ...f,
            dia: fechaObj.getUTCDate().toString().padStart(2, '0'),
            fecha: f.fecha // sigue igual
        });
    });

    return Object.entries(agrupado)
        .sort((a, b) => a[0] - b[0])
        .map(([mes, lista]) => `
            <div class="mb-3">
                <div class="fw-bold text-primary mb-2" style="font-size:1.1em;">
                    <i class="bi bi-calendar3"></i> ${meses[parseInt(mes, 10)]}
                </div>
                <div class="table-responsive">
                    <table class="table table-sm table-bordered align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th style="width:110px;">Fecha</th>
                                <th style="width:110px;">Día</th>
                                <th>Motivo</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${lista.sort((a, b) => a.dia - b.dia).map(f =>
                                `<tr>
                                    <td>${f.fecha}</td>
                                    <td>${obtenerDiaSemana(f.fecha)}</td>
                                    <td>
                                        <span class="badge bg-info text-dark">${f.motivo}</span>
                                    </td>
                                </tr>`
                            ).join('')}
                        </tbody>
                    </table>
                </div>
            </div>
        `).join('');
}
function obtenerDiaSemana(fechaISO) {
    const dias = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];
    const fechaObj = new Date(fechaISO + "T00:00:00Z");
    return dias[fechaObj.getUTCDay()];
}