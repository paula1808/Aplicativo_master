package com.play_learn.learn_topic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "puntuaciones_geografia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuntuacionGeografia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del usuario que jugó
    private String username;

    // Indica si la respuesta fue correcta (true) o incorrecta (false)
    private boolean victoria;

    // Fecha y hora en que se registró la partida (dato interesante para el historial)
    private LocalDateTime fecha;
}
