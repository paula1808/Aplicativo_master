package com.play_learn.learn_topic.repository;

import com.play_learn.learn_topic.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(Rol.RolNombre nombre);
    
}