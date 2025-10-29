document.addEventListener("DOMContentLoaded", function() {
    const modoSwitch = document.getElementById('modoSwitch');
    if (modoSwitch) {
        modoSwitch.addEventListener('change', function() {
            if (this.checked) {
                window.location.href = "/home";
            } else {
                window.location.href = "/colaborador/home";
            }
        });
    }
});



document.addEventListener("DOMContentLoaded", function() {
    const modoSwitch = document.getElementById('modoSwitchLicencia');
    if (modoSwitch) {
        modoSwitch.addEventListener('change', function() {
            if (this.checked) {
                window.location.href = "/licencias";
            } else {
                window.location.href = "/colaborador/licencias";
            }
        });
    }
});