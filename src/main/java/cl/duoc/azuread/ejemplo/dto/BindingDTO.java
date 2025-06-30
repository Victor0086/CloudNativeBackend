package cl.duoc.azuread.ejemplo.dto;

public class BindingDTO {

    private String nombreCola;
    private String nombreExchange;
    private String routingKey;

    public String getNombreCola() {
        return nombreCola;
    }

    public void setNombreCola(String nombreCola) {
        this.nombreCola = nombreCola;
    }

    public String getNombreExchange() {
        return nombreExchange;
    }

    public void setNombreExchange(String nombreExchange) {
        this.nombreExchange = nombreExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
