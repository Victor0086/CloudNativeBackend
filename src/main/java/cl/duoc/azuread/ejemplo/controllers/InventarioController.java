package cl.duoc.azuread.ejemplo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.azuread.ejemplo.model.Inventario;
import cl.duoc.azuread.ejemplo.service.InventarioService;

@RestController
@RequestMapping("/inventario")
public class InventarioController {
    
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }


    @GetMapping
    public ResponseEntity<List<Inventario>> listar() {
        List<Inventario> inventario = inventarioService.listarTodos();
        return ResponseEntity.ok(inventario);
    }


    @PostMapping
    public ResponseEntity<String> create(@RequestBody Inventario inventario) {
        inventarioService.guardar(inventario);
        return ResponseEntity.ok("Producto creado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Elemento eliminado con éxito");
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.buscarPorId(id);
        if (existente != null) {
            existente.setNombreProducto(inventario.getNombreProducto());
            existente.setStock(inventario.getStock());
            inventarioService.guardar(existente);
            return ResponseEntity.ok("Producto actualizado correctamente");
        }
        return ResponseEntity.status(404).body("Producto no encontrado");
    }


}
