calendar.onEventRightClick = function(args) {
  Swal.fire({
    title: "¿Eliminar este bloque?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  }).then((result) => {
    if (result.isConfirmed) {
      // Llamada al backend para eliminar
      fetch(`/app/bloque-horarios/eliminar/${args.e.id()}`, {
        method: "DELETE"
      })
      .then(response => {
        if (!response.ok) throw new Error("No se pudo eliminar en el servidor");
        // Si fue exitoso, eliminar visualmente
        calendar.events.remove(args.e.id());
        Swal.fire({
          title: "Eliminado",
          icon: "success",
          timer: 1200,
          showConfirmButton: false
        });
      })
      .catch(error => {
        Swal.fire({
          title: "Error",
          text: error.message,
          icon: "error"
        });
      });
    }
  });
};