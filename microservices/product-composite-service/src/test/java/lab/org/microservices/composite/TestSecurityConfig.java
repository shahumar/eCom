package lab.org.microservices.composite;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@Order(1)
public class TestSecurityConfig {

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable().authorizeExchange().anyExchange().permitAll();
        return http.build();
    }
}
