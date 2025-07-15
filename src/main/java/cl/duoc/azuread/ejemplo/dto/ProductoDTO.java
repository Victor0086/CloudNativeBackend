package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductoDTO implements Serializable {

    private String nombreProducto;
    private Double precioUnitario;


}
