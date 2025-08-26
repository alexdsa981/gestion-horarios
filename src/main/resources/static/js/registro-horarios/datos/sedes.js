document.addEventListener("DOMContentLoaded", function() {
  fetch("/app/sedes/select")
    .then(response => response.json())
    .then(data => {
      const select = document.getElementById("left-panel-sedes-select");
      data.forEach(sede => {
        const option = document.createElement("option");
        option.value = sede.id;
        option.textContent = sede.nombre;
        select.appendChild(option);
      });
    })
    .catch(error => {
      console.error("Error al cargar sedes:", error);
    });
});