package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;

public class ProductoDTO implements Serializable {

    private String nombreProducto;
    private Double precioUnitario;

    public ProductoDTO() {
        
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
