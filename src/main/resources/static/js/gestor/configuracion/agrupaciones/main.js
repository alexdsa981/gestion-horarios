// Inicialización y eventos principales

document.addEventListener('DOMContentLoaded', function() {
    cargarDepartamentosYAccordion();

    // Agregar área (departamento)
    document.getElementById('formAgregarArea').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreArea').value.trim();
        if (!nombre) return;
        try {
            await crearDepartamento(nombre);
            await Swal.fire({ icon: 'success', title: 'Área agregada', text: 'El área fue agregada correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarArea')).hide();
            const abiertos = obtenerAcordionesAbiertos();
            await cargarDepartamentosYAccordion(abiertos);
            this.reset();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo agregar el área.' });
        }
    });

    // Editar área (departamento)
    document.getElementById('formEditarArea').addEventListener('submit', async function(e){
        e.preventDefault();
        const departamentoId = this.getAttribute('data-id');
        const nombre = document.getElementById('inputEditarNombreArea').value.trim();
        if (!nombre || !departamentoId) return;
        try {
            await editarDepartamento(departamentoId, nombre);
            await Swal.fire({ icon: 'success', title: 'Área actualizada', text: 'El área se actualizó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalEditarArea')).hide();
            const abiertos = obtenerAcordionesAbiertos();
            await cargarDepartamentosYAccordion(abiertos);
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar el área.' });
        }
    });

    // Agregar agrupación
    document.getElementById('formAgregarAgrupacion').addEventListener('submit', async function(e){
        e.preventDefault();
        const nombre = document.getElementById('inputNombreAgrupacion').value.trim();
        const idDepartamento = document.getElementById('inputDepartamentoAgrupacion').value;
        if (!nombre || !idDepartamento) return;
        try {
            await crearAgrupacion(nombre, idDepartamento);
            await Swal.fire({ icon: 'success', title: 'Agrupación agregada', text: 'La agrupación fue agregada correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalAgregarAgrupacion')).hide();
            // Mantener acordeones abiertos
            const abiertos = obtenerAcordionesAbiertos();
            await cargarDepartamentosYAccordion(abiertos);
            this.reset();
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo agregar la agrupación.' });
        }
    });

    // Editar agrupación
    document.getElementById('formEditarAgrupacion').addEventListener('submit', async function(e){
        e.preventDefault();
        const agrupacionId = this.getAttribute('data-id');
        const nombre = document.getElementById('inputEditarNombreAgrupacion').value.trim();
        if (!nombre || !agrupacionId) return;
        try {
            await editarAgrupacion(agrupacionId, nombre);
            await Swal.fire({ icon: 'success', title: 'Agrupación actualizada', text: 'La agrupación se actualizó correctamente.' });
            bootstrap.Modal.getInstance(document.getElementById('modalEditarAgrupacion')).hide();
            // Mantener acordeones abiertos
            const abiertos = obtenerAcordionesAbiertos();
            await cargarDepartamentosYAccordion(abiertos);
        } catch (error) {
            await Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo editar la agrupación.' });
        }
    });
});