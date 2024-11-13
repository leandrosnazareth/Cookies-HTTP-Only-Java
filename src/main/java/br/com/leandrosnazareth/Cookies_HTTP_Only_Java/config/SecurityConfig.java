package br.com.leandrosnazareth.Cookies_HTTP_Only_Java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.leandrosnazareth.Cookies_HTTP_Only_Java.util.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/auth/login", "/auth/verify") // Ignora CSRF
                                                                                                        // para login e
                                                                                                        // verificação
                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem sessão
                                )
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/auth/login", "/login").permitAll() // Permite acesso
                                                                                                      // ao login
                                                .requestMatchers("/auth/verify").authenticated() // Protege o endpoint
                                                                                                 // de verificação
                                                .anyRequest().authenticated() // Requer autenticação para outros
                                                                              // endpoints
                                )
                                .formLogin(form -> form
                                                .loginPage("/login") // A página de login será acessada via /login
                                                .permitAll())
                                .addFilterBefore(new JwtAuthorizationFilter(),
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class).build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
