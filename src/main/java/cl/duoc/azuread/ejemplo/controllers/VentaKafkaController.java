package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.KafkaVentasProducerService;

@RestController
@RequestMapping("/ventas-kafka")
public class VentaKafkaController {

    private final KafkaVentasProducerService kafkaVentasProducer;

    public VentaKafkaController(KafkaVentasProducerService kafkaVentasProducer) {
        this.kafkaVentasProducer = kafkaVentasProducer;
    }

    @PostMapping
    public ResponseEntity<String> registrarVentaKafka(@RequestBody Venta venta) {
        kafkaVentasProducer.publicarVenta(venta);
        return ResponseEntity.ok("Venta enviada a Kafka\nID Venta: " + venta.getId());
    }
}