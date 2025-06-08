package cl.duoc.azuread.ejemplo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.repository.PromocionRepository;

@Service
public class PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    public List<Promocion> listar() {
        return promocionRepository.findAll();
    }

    public Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    public void eliminar(Long id) {
        promocionRepository.deleteById(id);
    }
}
