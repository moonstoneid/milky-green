package com.moonstoneid.web3login.config;

import java.util.ArrayList;
import javax.servlet.Filter;

import com.moonstoneid.web3login.security.ApiKeyAuthenticationFilter;
import com.moonstoneid.web3login.security.Web3AuthenticationFilter;
import com.moonstoneid.web3login.security.Web3AuthenticationProvider;
import com.moonstoneid.web3login.AppConstants;
import com.moonstoneid.web3login.AppProperties;
import com.moonstoneid.web3login.service.SettingService;
import com.moonstoneid.web3login.service.UserService;
import com.moonstoneid.web3login.siwe.SiweMessageVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    public static class ResourceConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers("/css/**", "/img/**", "/js/**", "/favicon.ico")
                    .and()
                .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout()
                    .disable();
        }

    }

    @Configuration
    @Order(2)
    public static class ApiDocConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers("/api", "/api/api-spec", "/api/api-spec/*", "/api/swagger-ui",
                            "/api/swagger-ui/*")
                    .and()
                .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout()
                    .disable();
        }

    }

    @Configuration
    @Order(3)
    public static class ApiConfig extends WebSecurityConfigurerAdapter {

        private final AppProperties appProperties;

        public ApiConfig(AppProperties appProperties) {
            this.appProperties = appProperties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers("/api/**")
                    .and()
                .authorizeRequests()
                    .anyRequest().hasAuthority(AppConstants.API_KEY_ROLE)
                    .and()
                .csrf()
                    .disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout()
                    .disable()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        private Filter authenticationFilter() {
            String apiKey = appProperties.getApi().getKey();
            if (apiKey == null || apiKey.isEmpty()) {
                log.info("No API key was configured.");
                apiKey = RandomStringUtils.random(32, true, true);
                log.info("Using auto-generated API key: " + apiKey);
            }

            return new ApiKeyAuthenticationFilter(apiKey);
        }

    }

    @Configuration
    @Order(4)
    public static class OAuthConfig extends WebSecurityConfigurerAdapter {

        private final OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer;

        public OAuthConfig(OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer) {
            this.authorizationServerConfigurer = authorizationServerConfigurer;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

            http.requestMatcher(endpointsMatcher)
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/")
                    .failureUrl("/")
                    .and()
                .csrf()
                    .ignoringRequestMatchers(endpointsMatcher)
                    .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .apply(authorizationServerConfigurer);
        }

    }

    @Configuration
    @Order(5)
    public static class UserConfig extends WebSecurityConfigurerAdapter {

        private final SettingService settingService;
        private final UserService userService;

        private final SiweMessageVerifier messageVerifier;

        public UserConfig(SettingService settingService, UserService userService,
                          SiweMessageVerifier messageVerifier) {
            this.settingService = settingService;
            this.userService = userService;
            this.messageVerifier = messageVerifier;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**")
                .authorizeRequests()
                    .antMatchers("/", "/login", "/login-message", "/consent-message").permitAll()
                    .antMatchers("/**").authenticated()
                    .and()
                .formLogin()
                    .loginPage("/")
                    .successForwardUrl("/")
                    .failureForwardUrl("/")
                    .and()
                .logout()
                    .permitAll()
                    .logoutSuccessUrl("/")
                    .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        private Filter authenticationFilter() {
            Web3AuthenticationFilter filter = new Web3AuthenticationFilter("/login");
            filter.setAuthenticationManager(web3AuthenticationManager());
            filter.setAuthenticationFailureHandler(web3AuthenticationFailureHandler());
            return filter;
        }

        private AuthenticationFailureHandler web3AuthenticationFailureHandler() {
            SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
            handler.setDefaultFailureUrl("/?error");
            return handler;
        }

        private AuthenticationManager web3AuthenticationManager() {
            ArrayList<AuthenticationProvider> providers = new ArrayList<>();
            providers.add(web3AuthenticationProvider());
            return new ProviderManager(providers);
        }

        private Web3AuthenticationProvider web3AuthenticationProvider() {
            return new Web3AuthenticationProvider(settingService, userService, messageVerifier);
        }

    }

}
