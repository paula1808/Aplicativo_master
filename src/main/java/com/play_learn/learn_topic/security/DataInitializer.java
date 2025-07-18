package com.play_learn.learn_topic.security;

import com.play_learn.learn_topic.entity.Rol;
import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.RolRepository;
import com.play_learn.learn_topic.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Lombok: genera constructor con campos finales
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear roles si no existen
        crearRoles();

        // 2. Crear usuarios de prueba
        crearUsuarioAdmin();
        crearUsuarioEducador();
        crearUsuarioNormal();
    }

    private void crearRoles() {
        if (rolRepository.findByNombre(Rol.RolNombre.ADMINISTRADOR).isEmpty()) {
            rolRepository.save(new Rol(Rol.RolNombre.ADMINISTRADOR));
        }
        if (rolRepository.findByNombre(Rol.RolNombre.EDUCADOR).isEmpty()) {
            rolRepository.save(new Rol(Rol.RolNombre.EDUCADOR));
        }
        if (rolRepository.findByNombre(Rol.RolNombre.USUARIO).isEmpty()) {
            rolRepository.save(new Rol(Rol.RolNombre.USUARIO));
        }
    }

    private void crearUsuarioAdmin() {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña encriptada
            admin.setEmail("admin@escuela.com");
            
            Rol rolAdmin = rolRepository.findByNombre(Rol.RolNombre.ADMINISTRADOR)
                .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
            admin.getRoles().add(rolAdmin);
            
            usuarioRepository.save(admin);
        }
    }

    private void crearUsuarioEducador() {
        if (usuarioRepository.findByUsername("educador").isEmpty()) {
            Usuario educador = new Usuario();
            educador.setUsername("educador");
            educador.setPassword(passwordEncoder.encode("educador123"));
            educador.setEmail("educador@escuela.com");
            
            Rol rolEducador = rolRepository.findByNombre(Rol.RolNombre.EDUCADOR)
                .orElseThrow(() -> new RuntimeException("Rol EDUCADOR no encontrado"));
            educador.getRoles().add(rolEducador);
            
            usuarioRepository.save(educador);
        }
    }

    private void crearUsuarioNormal() {
        if (usuarioRepository.findByUsername("usuario").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("usuario");
            usuario.setPassword(passwordEncoder.encode("usuario123"));
            usuario.setEmail("usuario@escuela.com");
            
            Rol rolUsuario = rolRepository.findByNombre(Rol.RolNombre.USUARIO)
                .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
            usuario.getRoles().add(rolUsuario);
            
            usuarioRepository.save(usuario);
        }
    }
}