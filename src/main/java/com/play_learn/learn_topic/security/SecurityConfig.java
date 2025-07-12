package com.play_learn.learn_topic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir recursos públicos y páginas de login/registro
            		.requestMatchers("/css/**", "/js/**", "/login", "/registro", 
                            "/swagger-ui.html", "/swagger-ui/**", 
                            "/v3/api-docs/**", "/webjars/**").permitAll()
                
                .requestMatchers("/administracion/accion-admin").hasAuthority("ROLE_ADMINISTRADOR")
                
                // La plantilla de administración solo será accesible para educadores o admin
                .requestMatchers("/administracion").hasAnyRole("ADMINISTRADOR", "EDUCADOR")
                // Los juegos, así como las páginas home e info son accesibles para usuario, educador y admin
                .requestMatchers("/juegos/**", "/home", "/info").hasAnyRole("USUARIO", "EDUCADOR", "ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Deshabilitando CSRF para simplificar (aunque en producción es recomendable configurarlo correctamente)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
