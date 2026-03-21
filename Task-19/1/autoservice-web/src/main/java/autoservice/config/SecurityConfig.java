package autoservice.config;

import autoservice.security.JwtAccessDeniedHandler;
import autoservice.security.JwtAuthenticationEntryPoint;
import autoservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ComponentScan("autoservice.security")
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Мастера
                        .requestMatchers(HttpMethod.GET, "/api/masters/**").hasRole("MASTER")
                        .requestMatchers(HttpMethod.POST, "/api/masters").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/masters/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/masters/**").hasRole("ADMIN")
                        // Рабочие места
                        .requestMatchers(HttpMethod.GET, "/api/workshop-places/**").hasRole("MASTER")
                        .requestMatchers(HttpMethod.POST, "/api/workshop-places").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/workshop-places/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/workshop-places/**").hasRole("ADMIN")
                        // Заказы
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("MASTER")
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER \n" +
                        "ROLE_MANAGER > ROLE_MASTER"
        );
        return roleHierarchy;
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}
