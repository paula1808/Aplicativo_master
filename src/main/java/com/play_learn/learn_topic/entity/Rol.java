package com.play_learn.learn_topic.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
public class Rol {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RolNombre nombre;

    // Getters y setters
    public enum RolNombre {
        USUARIO, EDUCADOR, ADMINISTRADOR
    }
 // Constructor necesario
    public Rol(RolNombre nombre) {
        this.nombre = nombre;
    }
}