package cl.duoc.azuread.ejemplo.service;

public interface MensajeService {
    void guardarMensaje(String contenido);
    void enviarMensaje(String mensaje);
    void guardarMensajeError(String contenido);
}
