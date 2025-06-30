package cl.duoc.azuread.ejemplo.service;

public interface MensajeService {
    void guardarMensaje(String contenido);
    void enviarMensaje(String mensaje);
    void enviarMensajeVentas(Object venta);
    void guardarMensajeError(String contenido);
    void enviarMensajePromocion(Object promo);
}
