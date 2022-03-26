package org.mn.web3login.config;

import java.util.ArrayList;
import javax.servlet.Filter;

import org.mn.web3login.security.MyAuthenticationFilter;
import org.mn.web3login.security.MyAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
                    .antMatchers("/res/**", "/js/**")
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
                    .failureUrl("/login")
                    .and()
                .csrf()
                    .ignoringRequestMatchers(endpointsMatcher)
                    .and()
                .apply(authorizationServerConfigurer);
        }

    }

    @Configuration
    @Order(3)
    public static class UserConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**")
                .authorizeRequests()
                    .antMatchers("/", "/login", "/nonce").permitAll()
                    .antMatchers("/**").authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .successForwardUrl("/")
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .addFilterBefore(myAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // todo: replace filter
        }

      private Filter myAuthenticationFilter() {
            UsernamePasswordAuthenticationFilter filter = new MyAuthenticationFilter();
            filter.setAuthenticationManager(myAuthenticationManager());
            filter.setAuthenticationFailureHandler(myAuthenticationFailureHandler());
            return filter;
        }

        private AuthenticationFailureHandler myAuthenticationFailureHandler() {
            SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler();
            handler.setDefaultFailureUrl("/login?error");
            return handler;
        }

        private AuthenticationManager myAuthenticationManager() {
            ArrayList<AuthenticationProvider> providers = new ArrayList<>();
            providers.add(myAuthenticationProvider());
            return new ProviderManager(providers);
        }

        private AuthenticationProvider myAuthenticationProvider() {
            MyAuthenticationProvider provider = new MyAuthenticationProvider();
            provider.setUserDetailsService(myUserDetailsService());
            return provider;
        }

        @Bean
        public UserDetailsService myUserDetailsService() {
            // Create new user and store it in a db
            UserDetails user = User.builder()
                    .username("0x76384DEC5e05C2487b58470d5F40c3aeD2807AcB")
                    .password("")
                    // .username("a")
                    // .password("a")
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

}
