package com.moonstoneid.web3login.config;

import java.util.ArrayList;
import javax.servlet.Filter;

import com.moonstoneid.web3login.security.ApiKeyAuthenticationFilter;
import com.moonstoneid.web3login.security.Web3AuthenticationFilter;
import com.moonstoneid.web3login.security.Web3AuthenticationProvider;
import com.moonstoneid.web3login.service.JpaUserDetailsService;
import com.moonstoneid.web3login.AppConstants;
import com.moonstoneid.web3login.AppProperties;
import com.moonstoneid.web3login.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
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
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout().disable();
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
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout().disable();
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
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .logout()
                    .disable()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }

        public Filter authenticationFilter() {
            ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(appProperties.api.key);
            filter.init();
            return filter;
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
                    .failureUrl("/")
                    .and()
                .csrf()
                    .ignoringRequestMatchers(endpointsMatcher)
                    .and()
                .apply(authorizationServerConfigurer);
        }

    }

    @Configuration
    @Order(5)
    public static class UserConfig extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;

        public UserConfig(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
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
            Web3AuthenticationProvider provider = new Web3AuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            return provider;
        }

    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new JpaUserDetailsService(userRepository);
    }

}
