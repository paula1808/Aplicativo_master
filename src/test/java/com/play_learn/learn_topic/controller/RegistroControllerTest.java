package com.play_learn.learn_topic.controller;

import com.play_learn.learn_topic.entity.Rol;
import com.play_learn.learn_topic.entity.Usuario;
import com.play_learn.learn_topic.repository.RolRepository;
import com.play_learn.learn_topic.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistroController.class)
public class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private RolRepository rolRepository;

    @Test
    void testRegistroExitoso() throws Exception {
        // Simular un rol existente (ejemplo: USUARIO)
        Rol rol = new Rol();
        rol.setNombre(Rol.RolNombre.USUARIO);

        Mockito.when(rolRepository.findByNombre(Rol.RolNombre.USUARIO))
                .thenReturn(Optional.of(rol));

        Mockito.when(usuarioService.registrarUsuario(Mockito.any()))
       .thenReturn(new Usuario());

        mockMvc.perform(post("/registro")
                .with(csrf()) // <--- esta línea es clave
                .param("nombre", "Paula")
                .param("email", "paula@example.com")
                .param("password", "12345")
                .param("confirmPassword", "12345")
                .param("userRole", "USUARIO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
