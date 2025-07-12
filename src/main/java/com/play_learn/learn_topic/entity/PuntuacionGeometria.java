package com.play_learn.learn_topic.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "puntuaciones_geometria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuntuacionGeometria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Nombre del usuario
    private int tiempo;      // Tiempo en segundos para completar el juego
    private boolean victoria; // Indica si se obtuvo la victoria con bonificación (true) o no (false)
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha; // Fecha y hora en que se registró la puntuación
}