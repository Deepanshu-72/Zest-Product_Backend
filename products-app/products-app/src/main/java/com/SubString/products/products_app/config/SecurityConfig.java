package com.SubString.products.products_app.config;



import com.SubString.products.products_app.dtos.ApiError;
import com.SubString.products.products_app.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(AbstractHttpConfigurer :: disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm-> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizreHttpRequests ->
                        authorizreHttpRequests.requestMatchers("/api/v1/auth/register").permitAll()
                                .requestMatchers("/api/v1/auth/login").permitAll()

                                .requestMatchers("/api/v1/auth/refresh").permitAll()
                                .requestMatchers("/api/v1/auth/logout").permitAll()
                                .anyRequest().authenticated()
                )

                .exceptionHandling(ex-> ex.authenticationEntryPoint((request, response, e) -> {


                    e.printStackTrace();
                    response.setStatus(401);
                    response.setContentType("application/json");
                    String message = "unauthorized access !" + e.getMessage();

                    String error = (String) request.getAttribute("error");
                    if(error != null){
                        message = error;
                    }
                   // Map<String,String> errorMap = Map.of("message", message, "status", String.valueOf(401),"statusCode", Integer.toString(401));
                    var apiError = ApiError.of(HttpStatus.UNAUTHORIZED.value(),"Unauthorized Access !!", message,request.getRequestURI(), true);
                    var objectMaper = new ObjectMapper();
                    response.getWriter().write(objectMaper.writeValueAsString(apiError));
                }))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);



        return http.build();


    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();

    }


}
