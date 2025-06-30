package cl.duoc.azuread.ejemplo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import cl.duoc.azuread.ejemplo.dto.ProductoDTO;
import cl.duoc.azuread.ejemplo.dto.VentaDTO;
import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.service.VentaService;
import cl.duoc.azuread.ejemplo.service.PromocionRabbitProducer;

import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDate;

@Component
public class RabbitMQConsumer {


    @Autowired
    private VentaService ventaService;

    @Autowired
    private PromocionRabbitProducer promocionRabbitProducer;

    


    //  Consumidor de mensajes JSON desde la cola "ventas"
    @RabbitListener(queues = "ventas")
    public void recibirVenta(VentaDTO venta) {
        System.out.println("[✓] Venta recibida desde RabbitMQ: " + venta.getCliente());

        for (ProductoDTO producto : venta.getProductos()) {
            System.out.println("Producto: " + producto.getNombreProducto());

            // Crear una promoción por cada producto
            Promocion promo = new Promocion();
            promo.setDescripcion("Descuento especial en accesorios para tu " + producto.getNombreProducto());
            promo.setDescuento(10.0); // 10%
            promo.setFechaInicio(new java.sql.Date(System.currentTimeMillis()));
            promo.setFechaFin(java.sql.Date.valueOf(LocalDate.now().plusDays(7))); // 7 días de validez

            // Enviar la promoción a RabbitMQ
            promocionRabbitProducer.enviarPromocion(promo);
        }

        // Guardar venta en Oracle
        ventaService.guardarVenta(venta);
    }



}
