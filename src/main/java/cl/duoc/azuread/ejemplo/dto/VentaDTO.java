package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class VentaDTO implements Serializable {

    private String cliente;
    private Double total;
    private List<ProductoDTO> productos;


}
