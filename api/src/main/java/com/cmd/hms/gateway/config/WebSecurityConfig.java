package com.cmd.hms.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }


    @Bean
    public PasswordEncoder getPasswordEncoder() {
        /* TODO - use password encoder */
        return new BCryptPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                // Authentication not required when logging in
                .authorizeRequests().antMatchers("/authenticate").permitAll()
                // Any authenticated user can access metadata
                .antMatchers("/odata/$metadata").authenticated()
                // USER can access their records
                .antMatchers(HttpMethod.GET, "/odata/Patients/**").hasRole("USER") 
                .antMatchers(HttpMethod.GET, "/odata/Patients{\\d+}/**").hasRole("USER") 
                .antMatchers(HttpMethod.GET, "/odata/Contacts/**").hasRole("USER") 
                .antMatchers(HttpMethod.GET, "/odata/Contacts{\\d+}/**").hasRole("USER") 
                .antMatchers(HttpMethod.GET, "/odata/Addresss/**").hasRole("USER") 
                .antMatchers(HttpMethod.GET, "/odata/Addresss{\\d+}/**").hasRole("USER") 
                // ADMIN can access anything
                .antMatchers("/odata/**").hasRole("ADMIN") 
                // All other requests need to be authenticated
                .anyRequest().authenticated().and().
                // Use statless session
                exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}