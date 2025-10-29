async function obtenerDepartamentos() {
    const resp = await fetch('/app/departamento/listar');
    if (!resp.ok) throw new Error('Error al cargar áreas');
    return resp.json();
}

async function crearDepartamento(nombre) {
    const res = await fetch('/app/departamento/crear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre })
    });
    if (!res.ok) throw new Error('Error creando área');
    return res.json();
}

async function editarDepartamento(idDepartamento, nombre) {
    const res = await fetch(`/app/departamento/editar/${idDepartamento}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre })
    });
    if (!res.ok) throw new Error('Error editando área');
    return res.json();
}

async function actualizarEstadoDepartamento(idDepartamento, nuevoEstado) {
    const res = await fetch(`/app/departamento/estado/${idDepartamento}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoEstado) // SOLO el booleano!
    });
    if (!res.ok) throw new Error('No se pudo actualizar el estado');
}

async function obtenerAgrupaciones(idDepartamento) {
    const resp = await fetch(`/app/agrupacion/listar/${idDepartamento}`);
    if (!resp.ok) throw new Error('Error al cargar agrupaciones');
    return resp.json();
}

async function crearAgrupacion(nombre, idDepartamento) {
    const res = await fetch('/app/agrupacion/crear', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            nombre,
            departamento: { id: idDepartamento }
        })
    });
    if (!res.ok) throw new Error('Error creando agrupación');
    return res.json();
}

async function editarAgrupacion(agrupacionId, nombre) {
    const res = await fetch(`/app/agrupacion/editar/${agrupacionId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre })
    });
    if (!res.ok) throw new Error('Error editando agrupación');
    return res.json();
}

async function actualizarEstadoAgrupacion(agrupacionId, nuevoEstado) {
    const res = await fetch("/app/agrupacion/estado/" + agrupacionId, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoEstado)
    });
    if (!res.ok) throw new Error('No se pudo actualizar el estado');
}