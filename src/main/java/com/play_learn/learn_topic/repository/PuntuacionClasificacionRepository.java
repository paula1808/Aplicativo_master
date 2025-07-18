package com.play_learn.learn_topic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.play_learn.learn_topic.entity.PuntuacionClasificacion;

@Repository
public interface PuntuacionClasificacionRepository extends JpaRepository<PuntuacionClasificacion, Long> {
	   //Este es el que funciona (administracion)
    List<PuntuacionClasificacion> findByUsuario(String usuario);
}
    


