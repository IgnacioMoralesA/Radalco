package cl.ufro.dci.radalco.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/vehiculos/**").permitAll() // Permite POST sin autenticación
                        .requestMatchers("/vehiculos/**").permitAll()
                        //.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return http.build();
    }
}