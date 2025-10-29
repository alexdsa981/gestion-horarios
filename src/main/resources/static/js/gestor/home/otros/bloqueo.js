function chequearBloqueoMes() {
    const hoy = new Date();
    const mesActual = hoy.getMonth();
    const anioActual = hoy.getFullYear();

    const anioSeleccionado = Number(document.getElementById("selectorAno").value);
    const mesSeleccionado = Number(document.getElementById("selectorMes").value); // 0-based

    console.log("Año seleccionado:", anioSeleccionado, "Mes seleccionado:", mesSeleccionado);
    console.log("Año actual:", anioActual, "Mes actual:", mesActual);

    const overlay = document.getElementById("bloqueoMesAnterior");
    const btnAgregar = document.getElementById("fab-agregar-bloque");

    const estaBloqueado = anioSeleccionado < anioActual || (anioSeleccionado === anioActual && mesSeleccionado < mesActual);

    // Muestra/oculta overlay
    overlay.style.display = estaBloqueado ? "block" : "none";

    // Deshabilita/habilita el botón
    if (btnAgregar) {
        btnAgregar.disabled = estaBloqueado;
        btnAgregar.style.opacity = estaBloqueado ? "0.5" : "1";
        btnAgregar.style.pointerEvents = estaBloqueado ? "none" : "auto";
        btnAgregar.title = estaBloqueado ? "No se puede agregar bloques en meses anteriores" : "Agregar bloque";
    }
}

document.getElementById("selectorAno").addEventListener("change", chequearBloqueoMes);
document.getElementById("selectorMes").addEventListener("change", chequearBloqueoMes);
