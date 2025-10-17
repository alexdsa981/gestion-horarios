  document.getElementById('fab-modal-licencias').addEventListener('click', function () {
    var modal = new bootstrap.Modal(document.getElementById('modalLicencias'));
    modal.show();
  });

  document.getElementById('btnAgregarLicencia').addEventListener('click', function () {
      var modal = new bootstrap.Modal(document.getElementById('modalAgregarLicencia'));
      modal.show();
  });