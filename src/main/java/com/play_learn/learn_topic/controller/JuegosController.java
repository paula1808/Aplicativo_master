package com.play_learn.learn_topic.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.play_learn.learn_topic.entity.Dificultad;
import com.play_learn.learn_topic.entity.PreguntaIngles;
import com.play_learn.learn_topic.entity.PuntuacionClasificacion;
import com.play_learn.learn_topic.entity.PuntuacionGeografia;
import com.play_learn.learn_topic.entity.PuntuacionGeometria;
import com.play_learn.learn_topic.repository.PuntuacionClasificacionRepository;
import com.play_learn.learn_topic.repository.PuntuacionGeografiaRepository;
import com.play_learn.learn_topic.repository.PuntuacionGeometriaRepository;
import com.play_learn.learn_topic.service.JuegoInglesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Controller
@RequestMapping("/juegos")
public class JuegosController {

    @Autowired
    private JuegoInglesService juegoInglesService;

    // Juego de inglés CON NIVELES
    @GetMapping("/ingles")
    public String mostrarJuegoIngles(
        @RequestParam(required = false, defaultValue = "PRINCIPIANTE") Dificultad nivel,
        Model model) {

        // Obtener el usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username); // Agregar el username al modelo

        PreguntaIngles pregunta = juegoInglesService.getPreguntaPorNivel(nivel);

        if (pregunta == null) {
            model.addAttribute("puntuacion", juegoInglesService.getPuntuacion());
            model.addAttribute("nivel", nivel);
            juegoInglesService.reiniciarNivel(nivel);
            return "juegos/fin-nivel-ingles";
        }

        // Añade estas líneas aquí ▼
        model.addAttribute("preguntasMostradas", juegoInglesService.getPreguntasMostradasCount(nivel));
        model.addAttribute("totalPreguntasNivel", juegoInglesService.getTotalPreguntasNivel(nivel));
        model.addAttribute("pregunta", pregunta);
        model.addAttribute("niveles", Dificultad.values());

        System.out.println("Usuario autenticado: " + username);
        return "juegos/ingles";
    }

    @PostMapping("/ingles/verificar")
    public String verificarRespuestaIngles(
        @RequestParam String oracion,
        @RequestParam String respuesta,
        @RequestParam Dificultad nivel,
        RedirectAttributes redirectAttrs) {
        
        boolean esCorrecta = juegoInglesService.verificarRespuesta(oracion, respuesta);
        juegoInglesService.registrarRespuesta(esCorrecta);
        
        // Guarda la puntuación en la BD (¡asegúrate de estar autenticado!)
        String username = "admin"; // Aquí deberías obtener el username desde Spring Security
        juegoInglesService.guardarPuntuacion(username, nivel, esCorrecta ? 10 : 0);
        
        redirectAttrs.addFlashAttribute("resultado", esCorrecta ? "¡Correcto!" : "Incorrecto");
        redirectAttrs.addAttribute("nivel", nivel);
        return "redirect:/juegos/ingles";
    }
    

 // 🔹 Vista principal de Juegos de Matemáticas
    @GetMapping("/home-matematicas")
    public String homeMatematicas() {
        return "juegos/home-matematicas";
    }

    // 🔹 Juegos específicos de Matemáticas
    @GetMapping("/matematicas/operaciones")
    public String juegoOperaciones() {
        return "juegos/matematicas/operaciones";
    }

    @GetMapping("/matematicas/clasificacion")
    public String juegoClasificacion(Model model) {
        List<Integer> numeros = List.of(27, 98, 1023);
        List<String> imagenes = List.of("/img/numeros/27.jpg", "/img/numeros/98.jpg", "/img/numeros/1023.jpg");

        model.addAttribute("numeros", numeros);
        model.addAttribute("imagenes", imagenes);

        return "juegos/matematicas/clasificacion"; // Enlace correcto a la vista
    }

    @GetMapping("/matematicas/nueva-ronda")
    public String nuevaRonda(@RequestParam(defaultValue = "1") int ronda, 
                             @ModelAttribute("resultados") List<Integer> resultados, 
                             Model model) {
        List<List<Integer>> numerosPorRonda = List.of(
            List.of(27, 98, 1023),
            List.of(311, 9755, 532),
            List.of(907, 115, 7821),
            List.of(1024, 8080, 2025),
            List.of(3306, 1098, 241)
        );

        List<List<String>> imagenesPorRonda = List.of(
            List.of("/img/numeros/27.jpg", "/img/numeros/98.jpg", "/img/numeros/1023.jpg"),
            List.of("/img/numeros/311.png", "/img/numeros/9755.jpg", "/img/numeros/532.jpg"),
            List.of("/img/numeros/907.jpg", "/img/numeros/115.jpg", "/img/numeros/7821.jpg"),
            List.of("/img/numeros/1024.jpg", "/img/numeros/8080.jpg", "/img/numeros/2025.jpg"),
            List.of("/img/numeros/3306.jpg", "/img/numeros/1098.jpg", "/img/numeros/241.jpg")
        );

        if (ronda > 5) {
            return "redirect:/puntuaciones/clasificacion"; // Redirige a la pantalla final
        }

        // ✅ Agrega la ronda actual al modelo
        model.addAttribute("numeros", numerosPorRonda.get(ronda - 1));
        model.addAttribute("imagenes", imagenesPorRonda.get(ronda - 1));
        model.addAttribute("rondaActual", ronda); // ✅ Ahora disponible en la vista

        return "juegos/matematicas/clasificacion";
    }
    
    @Autowired
    private PuntuacionClasificacionRepository puntuacionRepository;

    	
    @PostMapping("/matematicas/guardar-puntuacion")
    @Operation(summary = "Guardar puntuación en el juego de matemáticas", 
    description = "Registra la puntuación obtenida por el usuario en la ronda actual")
	@ApiResponse(responseCode = "200", description = "Puntuación guardada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida, falta la información requerida", content = @Content)
	@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content)
	@ApiResponse(responseCode = "500", description = "Error interno al guardar la puntuación", content = @Content)
    public ResponseEntity<?> guardarPuntuacion(@RequestBody Map<String, Integer> datos) {
        int ronda = datos.get("ronda");
        int puntuacion = datos.get("puntuacion");

        // Obtén el username del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        PuntuacionClasificacion puntuacionNueva = new PuntuacionClasificacion();
        puntuacionNueva.setUsuario(username); // Ahora se asigna el username real
        puntuacionNueva.setRonda(ronda);
        puntuacionNueva.setPuntuacion(puntuacion);

        puntuacionRepository.save(puntuacionNueva);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/puntuaciones/clasificacion")
    public String mostrarResultados(Model model) {
        List<PuntuacionClasificacion> resultados = puntuacionRepository.findAll(); // 📌 Recupera todas las puntuaciones de la BD
        model.addAttribute("resultados", resultados);

        return "juegos/puntuaciones/lista-clasificacion";
    }

    @PostMapping("/puntuaciones/eliminar")
    public String borrarResultados() {
        puntuacionRepository.deleteAll();
        return "redirect:/juegos/puntuaciones/clasificacion";
    }
    
    //Juego de geometria
    
    @Autowired
    private PuntuacionGeometriaRepository puntuacionGeometriaRepository;
    
    @GetMapping("/matematicas/geometria")
    public String juegoGeometria(Model model) {
        try {
            // Lista de imágenes disponibles
            List<String> imagenes = Arrays.asList(
                "/img/figuras/rectangulo.jpg",
                "/img/figuras/Triángulo.jpg",
                "/img/figuras/pentagono.jpg"
            );
            Random random = new Random();
            String figuraCompleta = imagenes.get(random.nextInt(imagenes.size()));
            
            // Determina la respuesta correcta según la imagen seleccionada
            String respuestaCorrecta = "";
            if (figuraCompleta.contains("rectangulo"))
                respuestaCorrecta = "Rectángulo"; // O "Cuadrado" si así lo deseas
            else if (figuraCompleta.contains("Triangulo"))
                respuestaCorrecta = "Triángulo";
            else if (figuraCompleta.contains("pentagono"))
                respuestaCorrecta = "Pentágono";
            
            // Precalcular posiciones para la grilla (3x3)
            List<Map<String, Integer>> posiciones = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                int xPos = ((i - 1) % 3) * 150;
                int yPos = ((int) Math.floor((i - 1) / 3)) * 150;
                posiciones.add(Map.of(
                    "xPos", xPos,
                    "yPos", yPos,
                    "index", i
                ));
            }
            
            // Agregar atributos al modelo
            model.addAttribute("figuraCompleta", figuraCompleta);
            model.addAttribute("respuestaCorrecta", respuestaCorrecta);
            model.addAttribute("posiciones", posiciones);
            model.addAttribute("totalCasillas", 9); // 3x3 grid

            return "juegos/matematicas/geometria";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el juego de geometría");
            return "error";
        }
    }



    @PostMapping("/matematicas/guardar-puntuacion-geometria")
    @Operation(summary = "Guardar puntuación en el juego de geometría", 
    description = "Registra el tiempo y el resultado (victoria/derrota) del usuario en el juego de geometría")
	@ApiResponse(responseCode = "200", description = "Puntuación guardada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida, falta la información requerida", content = @Content)
	@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content)
	@ApiResponse(responseCode = "500", description = "Error interno al guardar la puntuación", content = @Content)
    public ResponseEntity<?> guardarPuntuacionGeometria(@RequestBody Map<String, Object> datos) {
        try {
            int tiempo = Integer.parseInt(datos.get("tiempo").toString());
            boolean victoria = Boolean.parseBoolean(datos.get("victoria").toString());
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            PuntuacionGeometria puntuacion = new PuntuacionGeometria();
            puntuacion.setUsername(username);
            puntuacion.setTiempo(tiempo);
            puntuacion.setVictoria(victoria);
            puntuacion.setFecha(LocalDateTime.now());
            
            puntuacionGeometriaRepository.save(puntuacion);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/puntuaciones/lista-geometria")
    public String mostrarPuntuacionesUsuario(Model model) {
	        String username = SecurityContextHolder.getContext().getAuthentication().getName();
	        List<PuntuacionGeometria> puntuaciones = puntuacionGeometriaRepository.findByUsernameOrderByFechaDesc(username);
	        model.addAttribute("puntuaciones", puntuaciones);
	        return "juegos/puntuaciones/lista-geometria";
	    }

    @PostMapping("/puntuaciones/eliminar-geometria")
	    public String eliminarPuntuacionesGeometria() {
	        puntuacionGeometriaRepository.deleteAll();
	        return "redirect:/juegos/puntuaciones/lista-geometria";
	    }
    
    //Juego de geografia
    
    @Autowired
    private PuntuacionGeografiaRepository puntuacionGeografiaRepository;

    // Endpoint para mostrar el juego de geografía
    @GetMapping("/geografia")
    public String juegoGeografia(Model model) {
        try {
            // Lista estática de imágenes y respuestas correctas
            List<String> imagenes = List.of(
                "/img/geografia/andina.png",
                "/img/geografia/caribe.png",
                "/img/geografia/pacifico.png",
                "/img/geografia/orinoquia.png",
                "/img/geografia/amazonia.png"
            );
            List<String> respuestas = List.of(
                "Andina",     
                "Caribe",     
                "Pacífico",
                "Orinoquía",
                "Amazonía"
            );

            // Selección aleatoria
            Random random = new Random();
            int index = random.nextInt(imagenes.size());
            String imagenDepartamento = imagenes.get(index);
            String respuestaCorrecta = respuestas.get(index);

            model.addAttribute("imagenDepartamento", imagenDepartamento);
            model.addAttribute("respuestaCorrecta", respuestaCorrecta);
            return "juegos/geografia";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el juego de geografía");
            return "error";
        }
    }

    // Endpoint para guardar la puntuación del juego de geografía
    @PostMapping("/guardar-puntuacion-geografia")
    @Operation(summary = "Guardar puntuación en el juego de geografía", 
               description = "Registra si el usuario ganó o perdió en el juego de geografía")
    @ApiResponse(responseCode = "200", description = "Puntuación guardada correctamente")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, falta el campo 'victoria'", content = @Content)
    @ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content)
    @ApiResponse(responseCode = "500", description = "Error interno al guardar la puntuación", content = @Content)    
    public ResponseEntity<?> guardarPuntuacionGeografia(@RequestBody Map<String, Object> datos) {
        try {
            // Verifica que el campo "victoria" existe
            if (!datos.containsKey("victoria")) {
                return ResponseEntity.badRequest().body("{\"error\": \"Falta el campo 'victoria'\"}");
            }

            boolean victoria = Boolean.parseBoolean(datos.get("victoria").toString());
            
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Validar que el username no sea null o vacío
            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Usuario no autenticado\"}");
            }

            PuntuacionGeografia puntuacion = new PuntuacionGeografia();
            puntuacion.setUsername(username);
            puntuacion.setVictoria(victoria);
            puntuacion.setFecha(LocalDateTime.now());
            
            // Guardar en base de datos
            puntuacionGeografiaRepository.save(puntuacion);
            
            // Log para depuración
            System.out.println("Puntuación guardada: " + puntuacion);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("{\"error\": \"Error al guardar la puntuación: " + e.getMessage() + "\"}");
        }
    }
    
    // (Opcional) Endpoint para listar los resultados del juego de geografía por usuario
    @GetMapping("/puntuaciones/geografia")
    @Operation(summary = "Guardar puntuación en el juego de geografía", 
    description = "Registra si el usuario ganó o perdió en el juego de geografía")
	@ApiResponse(responseCode = "200", description = "Puntuación guardada correctamente")
	@ApiResponse(responseCode = "400", description = "Solicitud inválida, falta el campo 'victoria'", content = @Content)
	@ApiResponse(responseCode = "401", description = "Usuario no autenticado", content = @Content)
	@ApiResponse(responseCode = "500", description = "Error interno al guardar la puntuación", content = @Content)
    public String mostrarPuntuaciones(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PuntuacionGeografia> puntuaciones = puntuacionGeografiaRepository.findByUsernameOrderByFechaDesc(username);
        model.addAttribute("puntuaciones", puntuaciones);
        return "juegos/puntuaciones/lista-geografia";
    }

    // (Opcional) Endpoint para eliminar todas las puntuaciones de geografía
    @PostMapping("/puntuaciones/eliminar-geografia")
    public String eliminarPuntuacionesGeografia() {
        puntuacionGeografiaRepository.deleteAll();
        return "redirect:/juegos/puntuaciones/geografia";
    }



}