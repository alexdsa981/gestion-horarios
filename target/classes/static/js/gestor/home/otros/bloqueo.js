function chequearBloqueoMes() {
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
  "| Mes seleccionado (0=enero):", mesSeleccionado
);
    const overlay = document.getElementById("bloqueoMesAnterior");
    const btnAgregar = document.getElementById("fab-agregar-bloque");
    const btnTurnoNoche = document.getElementById("fab-turno-noche"); // <-- nuevo

    const estaBloqueado = rolUsuario !== 2 && (
        anioSeleccionado < anioActual ||
        (anioSeleccionado === anioActual && mesSeleccionado < mesActual)
    );

    overlay.style.display = estaBloqueado ? "block" : "none";

    if (btnAgregar) {
        btnAgregar.disabled = estaBloqueado;
        btnAgregar.style.opacity = estaBloqueado ? "0.5" : "1";
        btnAgregar.style.pointerEvents = estaBloqueado ? "none" : "auto";
        btnAgregar.title = estaBloqueado ? "No se puede agregar bloques en meses anteriores" : "Agregar bloque";
    }
    if (btnTurnoNoche) {
        btnTurnoNoche.disabled = estaBloqueado;
        btnTurnoNoche.style.opacity = estaBloqueado ? "0.5" : "1";
        btnTurnoNoche.style.pointerEvents = estaBloqueado ? "none" : "auto";
        btnTurnoNoche.title = estaBloqueado ? "No se puede agregar turno noche en meses anteriores" : "Agregar turno noche";
    }
}

document.getElementById("selectorAno").addEventListener("change", chequearBloqueoMes);
document.getElementById("selectorMes").addEventListener("change", chequearBloqueoMes);

