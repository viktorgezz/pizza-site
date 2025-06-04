package ru.viktorgezz.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    private final String URI_AFTER_SUCCESS_AUTH;
    private final String URI_REDIRECT;
    private final String URI_POST_LOGOUT_REDIRECT;
    private final String ISSUER_URL;
    private final String URL_ALLOWED_ORIGIN;
    private final String ID_CLIENT;
    private final int DURATION_MINUTE_LIVE_TOKEN;

    public AuthorizationServerConfig(
        @Value("${custom.uri.after-success-auth}") String uriAfterSuccessAuth,
        @Value("${custom.uri.redirect}") String uriRedirect,
        @Value("${custom.uri.post-logout-redirect}") String uriPostLogoutRedirect,
        @Value("${custom.url.issuer}") String issuerUrl,
        @Value("${custom.url.allowed-origin}") String urlAllowedOrigin,
        @Value("${custom.id-client}") String idClient,
        @Value("${custom.duration-minute-live-token}") int durationMinuteLiveToken
    ) {
        this.URI_AFTER_SUCCESS_AUTH = uriAfterSuccessAuth;
        this.URI_REDIRECT = uriRedirect;
        this.URI_POST_LOGOUT_REDIRECT = uriPostLogoutRedirect;
        this.ISSUER_URL = issuerUrl;
        this.URL_ALLOWED_ORIGIN = urlAllowedOrigin;
        this.ID_CLIENT = idClient;
        this.DURATION_MINUTE_LIVE_TOKEN = durationMinuteLiveToken;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",
                                "/.well-known/**").permitAll()
                        .anyRequest().authenticated()
                )
                .with(authorizationServerConfigurer, (authorizationServer) -> {
                    authorizationServer.oidc(Customizer.withDefaults());
                });

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                        .successHandler(customAuthenticationSuccessHandler())
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect(URI_AFTER_SUCCESS_AUTH);
        };
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(ISSUER_URL)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(URL_ALLOWED_ORIGIN));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(ID_CLIENT)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(URI_REDIRECT)
                .postLogoutRedirectUri(URI_POST_LOGOUT_REDIRECT)
                .scope("openid")
                .scope("profile")
                .scope("read")
                .scope("write")
                .clientSettings(
                        ClientSettings
                                .builder()
                                .requireAuthorizationConsent(false)
                                .requireProofKey(true)
                                .build())
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.ofMinutes(DURATION_MINUTE_LIVE_TOKEN))
                                .build())
                .build();

        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

        if (repository.findByClientId(ID_CLIENT) == null) {
            repository.save(registeredClient);
        }

        return repository;
    }
}
