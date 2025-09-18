
function rellenarSelectHorasMinutos(selectHora, selectMinuto) {
    selectHora.innerHTML = "";
    selectMinuto.innerHTML = "";
    for (let h = 7; h <= 20; h++) {
        let opt = document.createElement("option");
        opt.value = h.toString().padStart(2, "0");
        opt.textContent = h.toString().padStart(2, "0");
        selectHora.appendChild(opt);
    }
    for (let m = 0; m < 60; m += 5) {
        let opt = document.createElement("option");
        opt.value = m.toString().padStart(2, "0");
        opt.textContent = m.toString().padStart(2, "0");
        selectMinuto.appendChild(opt);
    }
}
