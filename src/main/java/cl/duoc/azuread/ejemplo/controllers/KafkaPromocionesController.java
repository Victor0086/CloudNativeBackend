package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.azuread.ejemplo.service.KafkaPromocionesService;

/**
 * Controller para gestionar promociones con Kafka
 * Permite activación On Demand de promociones
 */
@RestController
@RequestMapping("/kafka-promociones")
public class KafkaPromocionesController {
    
    @Autowired
    private KafkaPromocionesService kafkaPromocionesService;
    
    /**
     * Activa una promoción On Demand para una categoría específica
     */
    @PostMapping("/activar-on-demand")
    public ResponseEntity<String> activarPromocionOnDemand(
            @RequestParam String categoria,
            @RequestParam Double descuento,
            @RequestParam(defaultValue = "7") int diasValidez) {
        
        try {
            kafkaPromocionesService.activarPromocionOnDemand(categoria, descuento, diasValidez);
            
            return ResponseEntity.ok(
                String.format("Promoción On Demand activada exitosamente:\n" +
                            "Categoría: %s\n" +
                            "Descuento: %.1f%%\n" + 
                            "Validez: %d días\n" +
                            "Estado: ACTIVA", 
                            categoria, descuento, diasValidez)
            );
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al activar promoción: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para verificar el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Kafka Promociones Service está funcionando correctamente");
    }
    
    /**
     * Endpoint para listar categorías disponibles
     */
    @GetMapping("/categorias")
    public ResponseEntity<String[]> listarCategorias() {
        String[] categorias = {
            "LAPTOP", "MOUSE", "TECLADO", "MONITOR", "CPU", 
            "RAM", "ALMACENAMIENTO", "GPU", "ACCESORIOS", "IMPRESORA"
        };
        return ResponseEntity.ok(categorias);
    }
}
