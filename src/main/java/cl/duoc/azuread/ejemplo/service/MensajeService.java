package cl.duoc.azuread.ejemplo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.model.Mensaje;
import cl.duoc.azuread.ejemplo.repository.MensajeRepository;

@Service
public class MensajeService {

       @Autowired
    private MensajeRepository mensajeRepository;

    public void guardarMensaje(String contenido) {
        Mensaje msg = new Mensaje();
        msg.setContenido(contenido);
        msg.setFechaRecepcion(LocalDateTime.now());
        mensajeRepository.save(msg);
        System.out.println("[✓] Guardado en Oracle: " + contenido);
    }
    
}
