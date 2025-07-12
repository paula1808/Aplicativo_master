package com.play_learn.learn_topic.service;

import com.play_learn.learn_topic.entity.Dificultad;
import com.play_learn.learn_topic.entity.PreguntaIngles;
import com.play_learn.learn_topic.entity.Puntuacion;
import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.PuntuacionRepository;
import com.play_learn.learn_topic.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JuegoInglesService {
    
    private final List<PreguntaIngles> preguntas = Arrays.asList(
    		// PRINCIPIANTE - Presente Simple
    		new PreguntaIngles("She ___ three languages", "speaks", 
    		                  Arrays.asList("speak", "speaks", "speaking"), 
    		                  Dificultad.PRINCIPIANTE),

    		new PreguntaIngles("They ___ soccer every weekend", "play", 
    		                  Arrays.asList("plays", "play", "playing"), 
    		                  Dificultad.PRINCIPIANTE),

    		new PreguntaIngles("The sun ___ in the east", "rises", 
    		                  Arrays.asList("rise", "rises", "rising"), 
    		                  Dificultad.PRINCIPIANTE),

    		new PreguntaIngles("Cats ___ milk", "like", 
    		                  Arrays.asList("likes", "like", "liking"), 
    		                  Dificultad.PRINCIPIANTE),

    		new PreguntaIngles("We ___ TV after dinner", "watch", 
    		                  Arrays.asList("watches", "watch", "watching"), 
    		                  Dificultad.PRINCIPIANTE),
        
    		// INTERMEDIO - Presente Continuo
    		new PreguntaIngles("Look! It ___ outside", "is snowing", 
    		                  Arrays.asList("snows", "is snowing", "snowing"), 
    		                  Dificultad.INTERMEDIO),

    		new PreguntaIngles("Right now, I ___ for my exam", "am studying", 
    		                  Arrays.asList("study", "am studying", "studying"), 
    		                  Dificultad.INTERMEDIO),

    		// INTERMEDIO - Pasado Simple
    		new PreguntaIngles("She ___ a delicious cake yesterday", "baked", 
    		                  Arrays.asList("bake", "baked", "baking"), 
    		                  Dificultad.INTERMEDIO),

    		new PreguntaIngles("They ___ to Paris last summer", "went", 
    		                  Arrays.asList("go", "went", "gone"), 
    		                  Dificultad.INTERMEDIO),

    		new PreguntaIngles("The movie ___ at 8 PM", "started", 
    		                  Arrays.asList("starts", "started", "starting"), 
    		                  Dificultad.INTERMEDIO),
        
    		// AVANZADO - Present Perfect
    		new PreguntaIngles("I ___ never ___ sushi before", "have tried", 
    		                  Arrays.asList("have tried", "has tried", "tried"), 
    		                  Dificultad.AVANZADO),

    		new PreguntaIngles("She ___ already ___ her homework", "has finished", 
    		                  Arrays.asList("has finished", "have finished", "finished"), 
    		                  Dificultad.AVANZADO),

    		// AVANZADO - Condicionales
    		new PreguntaIngles("If I ___ rich, I would travel the world", "were", 
    		                  Arrays.asList("am", "was", "were"), 
    		                  Dificultad.AVANZADO),

    		new PreguntaIngles("She would pass the exam if she ___ harder", "studied", 
    		                  Arrays.asList("studies", "studied", "would study"), 
    		                  Dificultad.AVANZADO),

    		// AVANZADO - Phrasal Verbs
    		new PreguntaIngles("Please ___ the lights before leaving", "turn off", 
    		                  Arrays.asList("turn off", "turn on", "turn up"), 
    		                  Dificultad.AVANZADO)
    );
    
    private Map<Dificultad, List<PreguntaIngles>> preguntasPorNivel;
    private Map<Dificultad, Set<Integer>> preguntasMostradas; // Guarda índices de preguntas mostradas
    private int correctas = 0;
    private int totalIntentos = 0;
    

    @PostConstruct
    public void init() {
        preguntasPorNivel = preguntas.stream()
            .collect(Collectors.groupingBy(PreguntaIngles::getDificultad));
        
        preguntasMostradas = Arrays.stream(Dificultad.values())
            .collect(Collectors.toMap(
                nivel -> nivel,
                nivel -> new HashSet<>()
            ));
    }

    public PreguntaIngles getPreguntaPorNivel(Dificultad nivel) {
        List<PreguntaIngles> disponibles = preguntasPorNivel.get(nivel);
        Set<Integer> mostradas = preguntasMostradas.get(nivel);
        
        if (mostradas.size() == disponibles.size()) {
            return null; // Indica que se completó el nivel
        }
        
        PreguntaIngles pregunta;
        do {
            int index = new Random().nextInt(disponibles.size());
            pregunta = disponibles.get(index);
        } while (mostradas.contains(preguntas.indexOf(pregunta)));
        
        mostradas.add(preguntas.indexOf(pregunta));
        totalIntentos++;
        return pregunta;
    }

    public void registrarRespuesta(boolean esCorrecta) {
        if (esCorrecta) correctas++;
    }

    public String getPuntuacion() {
        return correctas + "/" + totalIntentos;
    }

    public void reiniciarNivel(Dificultad nivel) {
        preguntasMostradas.get(nivel).clear();
        correctas = 0;
        totalIntentos = 0;
    }
    
    
    public int getPreguntasMostradasCount(Dificultad nivel) {
        return (int) preguntasPorNivel.get(nivel).stream()
               .filter(p -> preguntasMostradas.get(nivel).contains(preguntas.indexOf(p)))
               .count();
    }

    public int getTotalPreguntasNivel(Dificultad nivel) {
        return preguntasPorNivel.get(nivel).size();
    }
    
    private final PuntuacionRepository puntuacionRepository;   //importantes para la puntuaciòn el la bd
    private final UsuarioRepository usuarioRepository;

    public JuegoInglesService(PuntuacionRepository puntuacionRepository, UsuarioRepository usuarioRepository) {
        this.puntuacionRepository = puntuacionRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    public void guardarPuntuacion(String username, Dificultad nivel, int puntos) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Puntuacion puntuacion = new Puntuacion();
        puntuacion.setUsuario(usuario);
        puntuacion.setNivel(nivel);
        puntuacion.setPuntos(puntos);

        System.out.println("Guardando puntuación: " + puntuacion.getPuntos()); // Prueba con un log
        puntuacionRepository.save(puntuacion);
    }

    public List<Puntuacion> obtenerHistorial(String username) {
        return usuarioRepository.findByUsername(username)
            .map(usuario -> puntuacionRepository.findByUsuarioOrderByFechaDesc(usuario))
            .orElse(List.of()); // Devuelve lista vacía en lugar de lanzar excepción
    }
    


    // Método original (opcional)
    public PreguntaIngles getPreguntaAleatoria() {
        return preguntas.get(new Random().nextInt(preguntas.size()));
    }

    public boolean verificarRespuesta(String oracion, String respuesta) {
        return preguntas.stream()
            .anyMatch(p -> p.getOracion().equals(oracion) && 
                   p.getRespuestaCorrecta().equalsIgnoreCase(respuesta));
    }
}