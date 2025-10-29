document.getElementById("selectorAno").addEventListener("change", cargarEventosVacaciones);
document.getElementById("selectorMes").addEventListener("change", cargarEventosVacaciones);

function cargarEventosVacaciones() {
    const ano = document.getElementById("selectorAno").value;
    const mes = document.getElementById("selectorMes").value;

    const nuevaFecha = `${ano}-${String(Number(mes) + 1).padStart(2, '0')}-01`;
    dp.startDate = nuevaFecha;

    fetch(`/app/licencias/vista?agrupacionId=${agrupacionGlobalId}&anio=${ano}&mes=${mes}`)
        .then(res => res.ok ? res.json() : Promise.reject("No se pudo obtener las licencias"))
        .then(data => {
            const events = [];
            data.forEach(item => {
                events.push({
                    start: item.fecha,
                    end: item.fecha,
                    id: item.idFecha,
                    text: `${item.colaborador} (${item.tipoLicencia})`,
                    backColor: item.color || "#2196f3"
                });
            });
            dp.update({events});
        })
        .catch(err => {
            console.error("Error al cargar eventos:", err);
            dp.update({events: []});
        });
}