package io.fiap.revenda.vendas.driven.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)
            .exceptionHandling(Customizer.withDefaults())
            .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()))
            .authorizeExchange(authorizeExchangeSpec ->
                authorizeExchangeSpec.pathMatchers("/veiculos")
                    .authenticated()
                    .anyExchange()
                    .permitAll())
            .build();
    }
}
