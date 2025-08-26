package com.ipor.horariostua.config.websocket;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WSNotificacionesService {

    private final SimpMessagingTemplate messagingTemplate;

    public WSNotificacionesService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /*
    public void notificarActualizacionTabla() {
        messagingTemplate.convertAndSend("/topic/actualizar-tabla", "refrescar");
    }
    */
}
