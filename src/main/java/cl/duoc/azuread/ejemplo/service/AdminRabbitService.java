package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.dto.BindingDTO;


public interface AdminRabbitService {
    void crearCola(String nombreCola);
    void crearExchange(String nombreExchange);
    void crearBinding(BindingDTO request);
    void eliminarCola(String nombreCola);
    void eliminarExchange(String nombreExchange);
}