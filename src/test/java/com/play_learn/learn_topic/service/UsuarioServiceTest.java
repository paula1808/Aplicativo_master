package com.play_learn.learn_topic.service;

import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioService = new UsuarioService();

        try {
            java.lang.reflect.Field field = UsuarioService.class.getDeclaredField("usuarioRepository");
            field.setAccessible(true);
            field.set(usuarioService, usuarioRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegistrarUsuarioNuevo() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("paula");
        usuario.setEmail("paula@test.com");
        usuario.setPassword("1234");

        when(usuarioRepository.findByUsername("paula")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("paula@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertNotNull(resultado);
        assertNotEquals("1234", resultado.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioYaExistenteUsername() {
        Usuario usuario = new Usuario();
        usuario.setUsername("paula");
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("1234");

        when(usuarioRepository.findByUsername("paula")).thenReturn(Optional.of(new Usuario()));

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.registrarUsuario(usuario);
        });

        assertEquals("El nombre de usuario ya existe.", exception.getMessage());
    }

    @Test
    void testRegistrarUsuarioYaExistenteEmail() {
        Usuario usuario = new Usuario();
        usuario.setUsername("nuevo");
        usuario.setEmail("paula@test.com");
        usuario.setPassword("1234");

        when(usuarioRepository.findByUsername("nuevo")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("paula@test.com")).thenReturn(Optional.of(new Usuario()));

        Exception exception = assertThrows(Exception.class, () -> {
            usuarioService.registrarUsuario(usuario);
        });

        assertEquals("El email ya está registrado.", exception.getMessage());
    }
}
