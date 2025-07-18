package com.play_learn.learn_topic.repository;

import com.play_learn.learn_topic.entity.Puntuacion;
import com.play_learn.learn_topic.entity.PuntuacionGeografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PuntuacionGeografiaRepository extends JpaRepository<PuntuacionGeografia, Long> {
    // Obtiene las puntuaciones de un usuario ordenadas de la más reciente a la antigua
    List<PuntuacionGeografia> findByUsernameOrderByFechaDesc(String username);
    
    List<PuntuacionGeografia> findByUsername(String username);
}
