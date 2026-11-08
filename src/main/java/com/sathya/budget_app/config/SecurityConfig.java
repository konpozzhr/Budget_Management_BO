package com.sathya.budget_app.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

/**
 * Session-cookie login for the whole API.
 *
 *  - POST /api/auth/login   (form fields: username, password) -> 200 {"username":...} | 401 {"message":...}
 *  - POST /api/auth/logout                                      -> 200
 *  - GET  /api/auth/me                                          -> 200 {"username":...} | 401
 *
 * Everything under /api/** and /h2-console/** requires a logged-in session.
 * The React build (index.html, /assets/**) stays public so the login page can load;
 * the API is what is protected.
 *
 * The username/password come from application.properties (app.auth.*).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        PathPatternRequestMatcher.Builder paths = PathPatternRequestMatcher.withDefaults();

        http
                .cors(Customizer.withDefaults())
                // CSRF token is off because this is a JSON API for a SPA: the session cookie is
                // SameSite=Lax (cross-site POST/PUT/DELETE don't carry it), JSON bodies force a
                // CORS preflight, and allowed origins are restricted (app.cors.allowed-origins).
                .csrf(csrf -> csrf.disable())
                // The H2 console renders inside frames from the same origin.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(paths.matcher(HttpMethod.POST, "/api/auth/login")).permitAll()
                        .requestMatchers(new OrRequestMatcher(
                                paths.matcher("/api/**"),
                                paths.matcher("/h2-console/**"))).authenticated()
                        // React app files + client-side routes (incl. /login) are public.
                        .anyRequest().permitAll())
                // A browser-less client (our fetch calls) wants a status code, not a redirect.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .formLogin(form -> form
                        // "/login" is the React route; it is only named here so Spring does not
                        // generate its own login page and shadow the React one.
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) ->
                                writeJson(response, HttpServletResponse.SC_OK,
                                        "{\"username\":\"" + escape(authentication.getName()) + "\"}"))
                        .failureHandler((request, response, exception) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "{\"message\":\"Invalid username or password\"}"))
                        .permitAll())
                .logout(logout -> logout
                        // POST only — with CSRF off, logoutUrl() alone would also accept GET.
                        .logoutRequestMatcher(paths.matcher(HttpMethod.POST, "/api/auth/logout"))
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.auth.username}") String username,
            @Value("${app.auth.password}") String password) {

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("app.auth.username and app.auth.password must be set");
        }
        return new InMemoryUserDetailsManager(
                User.withUsername(username)
                        .password(passwordEncoder.encode(password))
                        .roles("ADMIN")
                        .build());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // Needed so the browser sends/accepts the session cookie on cross-origin dev calls.
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private static void writeJson(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
