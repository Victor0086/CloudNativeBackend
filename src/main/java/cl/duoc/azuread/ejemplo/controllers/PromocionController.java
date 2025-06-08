package cl.duoc.azuread.ejemplo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.service.PromocionService;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    public List<Promocion> list() {
        return promocionService.listar();
    }

    @PostMapping
    public Promocion save(@RequestBody Promocion promocion) {
        return promocionService.guardar(promocion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        promocionService.eliminar(id);
    }
}
