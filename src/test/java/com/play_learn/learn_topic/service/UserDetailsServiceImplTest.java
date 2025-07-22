package com.play_learn.learn_topic.service;

import com.play_learn.learn_topic.entity.Rol;
import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.play_learn.learn_topic.entity.Rol.RolNombre; // ✅ Import correcto

class UserDetailsServiceImplTest {

    private UsuarioRepository usuarioRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        userDetailsService = new UserDetailsServiceImpl(usuarioRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        Rol rol = new Rol(RolNombre.EDUCADOR);
        Usuario usuario = new Usuario();
        usuario.setUsername("paula");
        usuario.setPassword("123456");
        usuario.setRoles(Set.of(rol));

        when(usuarioRepository.findByUsername("paula")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("paula");

        assertNotNull(userDetails);
        assertEquals("paula", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_EDUCADOR")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("noexiste");
        });
    }
}
