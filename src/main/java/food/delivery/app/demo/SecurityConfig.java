
package food.delivery.app.demo;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Signup and login are public
                .requestMatchers("/api/auth/**").permitAll()

                // Customers can view the food menu
                .requestMatchers(HttpMethod.GET, "/api/foods", "/api/foods/**").permitAll()

                // Food changes require ADMIN
                .requestMatchers(HttpMethod.POST, "/api/foods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/foods/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasRole("ADMIN")

                // Order operations temporarily stay public
                // We will secure admin order operations next
                .requestMatchers("/api/orders/**").permitAll()

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:5174",
            "https://food-delivery-app-theta-three.vercel.app",
            "https://food-delivery-app-eshwargoud.vercel.app",
            "https://food-delivery-app-git-main-eshwargoud.vercel.app"
        ));

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
