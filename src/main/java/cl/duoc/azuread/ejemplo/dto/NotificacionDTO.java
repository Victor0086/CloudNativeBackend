package cl.duoc.azuread.ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class NotificacionDTO {

    private Long id;
    private String usuario;
    private String fecha;


}
