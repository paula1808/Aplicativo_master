package com.play_learn.learn_topic.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(Usuario usuario) throws Exception {
        // Validar que el username y email no estén ya registrados (esto es opcional)
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new Exception("El nombre de usuario ya existe.");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new Exception("El email ya está registrado.");
        }
        // Cifrar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}
