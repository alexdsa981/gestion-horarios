document.addEventListener("DOMContentLoaded", function() {
  fetch("/app/colaboradores/select")
    .then(response => response.json())
    .then(data => {
      const select = document.getElementById("left-panel-colaborador-select");
      data.forEach(colaborador => {
        const option = document.createElement("option");
        option.value = colaborador.id;
        option.textContent = colaborador.nombreCompleto;
        select.appendChild(option);
      });
    })
    .catch(error => {
      console.error("Error al cargar colaboradores:", error);
    });
});