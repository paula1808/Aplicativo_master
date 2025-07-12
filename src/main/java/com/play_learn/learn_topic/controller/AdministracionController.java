package com.play_learn.learn_topic.controller;

import com.play_learn.learn_topic.entity.*;
import com.play_learn.learn_topic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/administracion")
public class AdministracionController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PuntuacionRepository puntuacionRepository;
    
    @Autowired
    private PuntuacionClasificacionRepository puntuacionClasificacionRepository;
    
    @Autowired
    private PuntuacionGeometriaRepository puntuacionGeometriaRepository;
    
    @Autowired
    private PuntuacionGeografiaRepository puntuacionGeografiaRepository;
    
    @GetMapping()
    public String panelAdministracion(Authentication authentication, Model model) {
        // Verificación básica de acceso
        if (authentication == null ||
            authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") ||
                                a.getAuthority().equals("ROLE_EDUCADOR"))) {
            throw new AccessDeniedException("Acceso denegado");
        }
        
        // Verificación específica para ADMINISTRADOR
        boolean mostrarAccionAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        
        model.addAttribute("mostrarAccionAdmin", mostrarAccionAdmin);
        
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        
        return "administracion";
    }

    @GetMapping("/buscar-puntuacion")
    public String buscarPuntuacion(@RequestParam("username") String username,
                                 Authentication authentication,
                                 Model model) {
        // Verificación de acceso
        if (authentication == null ||
            authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") ||
                                a.getAuthority().equals("ROLE_EDUCADOR"))) {
            throw new AccessDeniedException("Acceso denegado");
        }
        
        // Verificación específica para ADMINISTRADOR
        boolean mostrarAccionAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        
        model.addAttribute("mostrarAccionAdmin", mostrarAccionAdmin);
        
        // Consultas de puntuaciones
        List<Puntuacion> puntuacionesIngles = puntuacionRepository.findByUsuarioUsername(username);
        List<PuntuacionClasificacion> puntuacionesClasificacion = puntuacionClasificacionRepository.findByUsuario(username);
        List<PuntuacionGeometria> puntuacionesGeometria = puntuacionGeometriaRepository.findByUsername(username);
        List<PuntuacionGeografia> puntuacionesGeografia = puntuacionGeografiaRepository.findByUsername(username);
        
        model.addAttribute("puntuacionesIngles", puntuacionesIngles);
        model.addAttribute("puntuacionesClasificacion", puntuacionesClasificacion);
        model.addAttribute("puntuacionesGeometria", puntuacionesGeometria);
        model.addAttribute("puntuacionesGeografia", puntuacionesGeografia);
        model.addAttribute("username", username);
        
        return "administracion";
    }
    
    @GetMapping("/accion-admin")
    public String accionAdministrador(Authentication authentication, Model model) {
        // Verificación estricta solo para ADMINISTRADOR
        if (authentication == null ||
            authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            throw new AccessDeniedException("Acceso denegado");
        }
        
        // Agregar atributos si es necesario para la vista accion-administrador
        model.addAttribute("mensaje", "Panel exclusivo para administradores");
        
        return "admin/accion-administrador";
    }
}