package com.play_learn.learn_topic.entity;

import java.util.List; // Este es el import esencial

public class PreguntaIngles {
	private String oracion;
    private String respuestaCorrecta;
    private List<String> opciones;
    private Dificultad dificultad; // <- Añade este campo

    // Constructor modificado
    public PreguntaIngles(String oracion, String respuestaCorrecta, 
                         List<String> opciones, Dificultad dificultad) {
        this.oracion = oracion;
        this.respuestaCorrecta = respuestaCorrecta;
        this.opciones = opciones;
        this.dificultad = dificultad; // <- Nuevo parámetro
    }

    // Añade este getter
    public Dificultad getDificultad() {
        return dificultad;
    }

    // Getters y Setters (usa @Data de Lombok si lo tienes)
    public String getOracion() { return oracion; }
    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public List<String> getOpciones() { return opciones; }
}