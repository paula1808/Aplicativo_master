package com.play_learn.learn_topic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
public class MainController {
    @GetMapping("/")
    public String root() {
        return "auth/login"; // Retorna directamente la plantilla de login
    }
    @GetMapping("/home")
    public String home(Model model) {
        // Recupéralo desde el contexto de seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean mostrarAdministracion = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") ||
                               a.getAuthority().equals("ROLE_EDUCADOR"));
        model.addAttribute("mostrarAdministracion", mostrarAdministracion);
        return "home/home";
    }

	@GetMapping("/info")
	public String info(Model model) {
	    return "info/info";
	}
	
    @GetMapping
    public String accionAdministrador(Authentication authentication) {
        // Verificación de seguridad adicional
        if (authentication != null && 
            authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return "administracion/accion-admin";
        }
        throw new AccessDeniedException("Acceso denegado");
    }
	
//    @GetMapping("/administracion")
//    public String mostrarAdministracion() {
//        // Retorna el nombre de la vista que corresponde a la plantilla administracion.html
//        return "administracion";
//    }
}
