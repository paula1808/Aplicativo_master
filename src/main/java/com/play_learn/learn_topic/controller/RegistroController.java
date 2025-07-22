package com.play_learn.learn_topic.controller;

import com.play_learn.learn_topic.entity.Rol;
import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.RolRepository;
import com.play_learn.learn_topic.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                   BindingResult result,
                                   Model model,
                                   @RequestParam("confirmPassword") String confirmPassword,
                                   @RequestParam("userRole") String userRoleParam) {
        // Validar que las contraseñas coincidan
        if (!usuario.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", null, "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            // Convertir el valor recibido a mayúsculas y a tipo enumerado
            Rol.RolNombre rolEnum = Rol.RolNombre.valueOf(userRoleParam.toUpperCase());
            // Buscar en BD el rol correspondiente
            Rol rol = rolRepository.findByNombre(rolEnum)
                                   .orElseThrow(() -> new Exception("Rol no encontrado."));
            
            // Asignamos el rol al usuario (limpiamos roles anteriores por si acaso)
            usuario.getRoles().clear();
            usuario.addRol(rol);
            
            // Registrar el usuario (el servicio se encargará del cifrado, validaciones, etc.)
            usuarioService.registrarUsuario(usuario);
        } catch (Exception e) {
            model.addAttribute("registroError", e.getMessage());
            return "registro";
        }
        // Redirige al login (o a otra vista según tu lógica)
        return "redirect:/login";
    }
}
