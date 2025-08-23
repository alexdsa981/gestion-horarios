
// Guarda los datos del evento seleccionado y aplica un estilo destacado
let eventoSeleccionado = null;

function resaltarEventoSeleccionado(id) {
  // Quitar estilo de seleccionado anterior
  document.querySelectorAll('.evento-seleccionado').forEach(el => {
    el.classList.remove('evento-seleccionado');
  });
  document.querySelectorAll('.puntero-seleccionado').forEach(el => {
    el.remove();
  });
  setTimeout(() => {
    // Buscar cualquier div con data-id igual al id del evento
    let eventEls = Array.from(document.querySelectorAll(`[data-id='${id}']`));
    if (eventEls.length === 0) {
      // Buscar por coincidencia parcial (por si DayPilot cambia el id)
      eventEls = Array.from(document.querySelectorAll('[data-id]')).filter(el => el.getAttribute('data-id') == id);
    }
    console.log('Elementos de evento encontrados para resaltar:', eventEls);
    eventEls.forEach(el => {
      el.classList.add('evento-seleccionado');
      // Crear puntero visual
      const pointer = document.createElement('div');
      pointer.className = 'puntero-seleccionado';
      pointer.innerHTML = `<svg width="32" height="32" viewBox="0 0 32 32"><polygon points="16,0 32,32 16,24 0,32" fill="#ff9800" stroke="#fffde7" stroke-width="2"/></svg>`;
      pointer.style.position = 'absolute';
      pointer.style.left = '50%';
      pointer.style.top = '-30px';
      pointer.style.transform = 'translateX(-50%)';
      pointer.style.zIndex = '2000';
      pointer.style.pointerEvents = 'none';
      el.style.position = 'relative';
      el.appendChild(pointer);
    });
    if (eventEls.length === 0) {
      console.warn('No se encontró ningún elemento visual para el evento seleccionado con id:', id);
    }
  }, 100);
}

function mostrarLogSeleccion(id, datos) {
  // Log en consola
  console.log('%cEvento seleccionado:', 'color: orange; font-weight: bold;', datos);
  // Mensaje visual
  let logDiv = document.getElementById('evento-log-seleccionado');
  if (!logDiv) {
    logDiv = document.createElement('div');
    logDiv.id = 'evento-log-seleccionado';
    logDiv.style.position = 'fixed';
    logDiv.style.bottom = '20px';
    logDiv.style.right = '20px';
    logDiv.style.background = '#fff3cd';
    logDiv.style.color = '#856404';
    logDiv.style.border = '2px solid #ffeeba';
    logDiv.style.padding = '12px 18px';
    logDiv.style.borderRadius = '8px';
    logDiv.style.boxShadow = '0 2px 8px rgba(0,0,0,0.15)';
    logDiv.style.zIndex = 9999;
    document.body.appendChild(logDiv);
  }
  logDiv.innerHTML = `<b>Evento seleccionado:</b><br>ID: ${id}<br>${datos.text || ''}<br>${datos.start || ''} - ${datos.end || ''}`;
  setTimeout(() => { logDiv.style.display = 'none'; }, 3500);
  logDiv.style.display = 'block';
}


// Escucha el evento personalizado de selección
document.addEventListener('eventoSeleccionado', function(e) {
  eventoSeleccionado = { ...e.detail };
  resaltarEventoSeleccionado(eventoSeleccionado.id);
  mostrarLogSeleccion(eventoSeleccionado.id, eventoSeleccionado);
});



// Estilo visual destacado para el evento seleccionado y el puntero
const style = document.createElement('style');
style.innerHTML = `
  div[data-id].evento-seleccionado {
    box-shadow: 0 0 12px 4px #ff9800 !important, 0 0 24px 8px #ffd180 !important;
    border: 3px solid #ff9800 !important;
    outline: 2px solid #fffde7 !important;
    filter: brightness(1.15) drop-shadow(0 0 8px #ffd180) !important;
    z-index: 1000 !important;
    position: relative !important;
    transition: box-shadow 0.2s, border 0.2s;
  }
  .puntero-seleccionado {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    animation: punteroAnim 1.2s infinite alternate;
  }
  @keyframes punteroAnim {
    0% { transform: translateX(-50%) scale(1); opacity: 1; }
    100% { transform: translateX(-50%) scale(1.15); opacity: 0.85; }
  }
`;
document.head.appendChild(style);
