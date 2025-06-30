package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class DefaultController {

	@GetMapping("/mensaje")
	public String mensajeGet() {

		System.out.println("Backend llamado");
		return "{\"mensaje\": \"Hola desde el backend\"}";
	}

	@PostMapping("/mensaje")
	public String mensaje() {

		System.out.println("Backend llamado");
		return "{\"mensaje\": \"Hola desde el backend\"}";
	}

	
	
    
}
