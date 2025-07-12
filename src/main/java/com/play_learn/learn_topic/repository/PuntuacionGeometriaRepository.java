package com.play_learn.learn_topic.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.play_learn.learn_topic.entity.Puntuacion;
import com.play_learn.learn_topic.entity.PuntuacionGeometria;

@Repository
public interface PuntuacionGeometriaRepository extends JpaRepository<PuntuacionGeometria, Long> {
    // Método opcional: para buscar puntuaciones de un mismo usuario
    List<PuntuacionGeometria> findByUsername(String username);
    
    // Método personalizado para buscar por username ordenado por fecha descendente
    List<PuntuacionGeometria> findByUsernameOrderByFechaDesc(String username);

}

