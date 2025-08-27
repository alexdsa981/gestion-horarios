function poblarSelectHorasYMinutos() {
    // IDs de los selects de hora y minuto
    const horaIds = [
        "horaInicioHora", "horaFinHora",
        "edit-horaInicioHora", "edit-horaFinHora"
    ];
    const minIds = [
        "horaInicioMinuto", "horaFinMinuto",
        "edit-horaInicioMinuto", "edit-horaFinMinuto"
    ];

    // Poblar horas (07 a 20)
    horaIds.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;
        select.innerHTML = "";
        for (let h = 7; h <= 20; h++) {
            const horaStr = h.toString().padStart(2, '0');
            const option = document.createElement("option");
            option.value = horaStr;
            option.text = horaStr;
            select.appendChild(option);
        }
    });

    // Poblar minutos (00 y 30)
    minIds.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;
        select.innerHTML = "";
        ["00", "30"].forEach(m => {
            const option = document.createElement("option");
            option.value = m;
            option.text = m;
            select.appendChild(option);
        });
    });

    // Selección por defecto
    if (document.getElementById("horaInicioHora")) document.getElementById("horaInicioHora").value = "08";
    if (document.getElementById("horaInicioMinuto")) document.getElementById("horaInicioMinuto").value = "00";
    if (document.getElementById("horaFinHora")) document.getElementById("horaFinHora").value = "10";
    if (document.getElementById("horaFinMinuto")) document.getElementById("horaFinMinuto").value = "00";
    if (document.getElementById("edit-horaInicioHora")) document.getElementById("edit-horaInicioHora").value = "08";
    if (document.getElementById("edit-horaInicioMinuto")) document.getElementById("edit-horaInicioMinuto").value = "00";
    if (document.getElementById("edit-horaFinHora")) document.getElementById("edit-horaFinHora").value = "10";
    if (document.getElementById("edit-horaFinMinuto")) document.getElementById("edit-horaFinMinuto").value = "00";
}
document.addEventListener("DOMContentLoaded", poblarSelectHorasYMinutos);



//Como obtener los valores
// Para inicio normal:
const inicio = document.getElementById("horaInicioHora").value + ":" + document.getElementById("horaInicioMinuto").value;
// Para fin normal:
const fin = document.getElementById("horaFinHora").value + ":" + document.getElementById("horaFinMinuto").value;
// Para inicio edición:
const editInicio = document.getElementById("edit-horaInicioHora").value + ":" + document.getElementById("edit-horaInicioMinuto").value;
// Para fin edición:
const editFin = document.getElementById("edit-horaFinHora").value + ":" + document.getElementById("edit-horaFinMinuto").value;