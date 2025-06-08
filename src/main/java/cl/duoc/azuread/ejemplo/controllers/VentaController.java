package cl.duoc.azuread.ejemplo.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.VentaService;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Venta> listarVentas() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        Venta venta = service.buscarPorId(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }
    @GetMapping("/cliente/{email}")
    public List<Venta> listarPorCliente(@PathVariable String email) {
        return service.buscarPorCliente(email);
    }


    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta) {
        return ResponseEntity.ok(service.crearVenta(venta));
    }
}
