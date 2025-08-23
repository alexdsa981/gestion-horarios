calendar.onEventRightClick = function(args) {
  Swal.fire({
    title: "¿Eliminar este bloque?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  }).then((result) => {
    if (result.isConfirmed) {
      calendar.events.remove(args.e.id());
      Swal.fire({
        title: "Eliminado",
        icon: "success",
        timer: 1200,
        showConfirmButton: false
      });
    }
  });
};