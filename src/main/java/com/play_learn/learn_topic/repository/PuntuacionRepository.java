package com.play_learn.learn_topic.repository;

import com.play_learn.learn_topic.entity.Dificultad;
import com.play_learn.learn_topic.entity.Puntuacion;
import com.play_learn.learn_topic.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    List<Puntuacion> findByUsuarioAndNivel(Usuario usuario, Dificultad nivel);
    List<Puntuacion> findByUsuarioOrderByFechaDesc(Usuario usuario);
    //administracion
    List<Puntuacion> findByUsuarioUsername(String username);

}