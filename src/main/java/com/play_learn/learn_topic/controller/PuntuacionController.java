package com.play_learn.learn_topic.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import com.play_learn.learn_topic.entity.Dificultad;
import com.play_learn.learn_topic.entity.Puntuacion;
import com.play_learn.learn_topic.service.JuegoInglesService;

@Controller
@RequestMapping("/puntuaciones")
public class PuntuacionController {

    private final JuegoInglesService juegoInglesService;

    public PuntuacionController(JuegoInglesService juegoInglesService) {
        this.juegoInglesService = juegoInglesService;
    }

    @GetMapping("/ingles")
    public String verPuntuacionesIngles(Model model) {
        // Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Revisar si el usuario realmente tiene una sesión activa
        if (username == null || username.equals("anonymousUser")) {
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }

        List<Puntuacion> historial = juegoInglesService.obtenerHistorial(username);

        // Filtrar solo puntuaciones del juego de inglés
        historial = historial.stream()
            .filter(p -> p.getNivel() == Dificultad.PRINCIPIANTE 
                      || p.getNivel() == Dificultad.INTERMEDIO 
                      || p.getNivel() == Dificultad.AVANZADO)
            .toList();

        model.addAttribute("puntuaciones", historial);
        model.addAttribute("username", username);
        return "juegos/puntuaciones/lista-ingles"; // Enlazamos la plantilla
    }
}
