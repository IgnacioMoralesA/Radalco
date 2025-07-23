package cl.ufro.dci.radalco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs (especialmente si no usas login)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/vehiculos/**", "/vehiculos/**").permitAll() // 🔓 permite estas rutas
                        .anyRequest().permitAll() // 🔓 permite todo en desarrollo
                )
                .httpBasic(httpBasic -> {}); // opcional: habilita autenticación básica si la necesitas

        return http.build();
    }
}
