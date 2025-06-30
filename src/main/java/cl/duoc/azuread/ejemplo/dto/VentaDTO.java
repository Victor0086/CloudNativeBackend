package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;
import java.util.List;

public class VentaDTO implements Serializable {

    private String cliente;
    private Double total;
    private List<ProductoDTO> productos;

    public VentaDTO() {
        
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoDTO> productos) {
        this.productos = productos;
    }
}
