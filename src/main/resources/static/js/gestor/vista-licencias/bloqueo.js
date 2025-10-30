function chequearBloqueoMesLicencias() {
    const hoy = new Date();
    const mesActual = hoy.getMonth();
    const anioActual = hoy.getFullYear();

    const anioSeleccionado = Number(document.getElementById("selectorAno").value);
    const mesSeleccionado = Number(document.getElementById("selectorMes").value);
    const rolUsuario = Number(document.getElementById("rolUsuario").value);

    console.log(
      "Año actual:", anioActual,
      "| Mes actual (0=enero):", mesActual,
      "| Año seleccionado:", anioSeleccionado,
      "| Mes seleccionado (0=enero):", mesSeleccionado,
      "| Rol Usuario:", rolUsuario
    );

    const btnLicencia = document.getElementById("fab-modal-licencias");

    const estaBloqueado = rolUsuario !== 2 && (
        anioSeleccionado < anioActual ||
        (anioSeleccionado === anioActual && mesSeleccionado < mesActual)
    );

    if (btnLicencia) {
        btnLicencia.disabled = estaBloqueado;
        btnLicencia.style.opacity = estaBloqueado ? "0.5" : "1";
        btnLicencia.style.pointerEvents = estaBloqueado ? "none" : "auto";
        btnLicencia.title = estaBloqueado ? "No se puede agregar licencias en meses anteriores" : "Agregar licencia";
    }

    const overlay = document.getElementById("bloqueoMesAnterior");
    if (overlay) {
        overlay.style.display = estaBloqueado ? "block" : "none";
    }
}

document.getElementById("selectorAno").addEventListener("change", chequearBloqueoMesLicencias);
document.getElementById("selectorMes").addEventListener("change", chequearBloqueoMesLicencias);

chequearBloqueoMesLicencias();