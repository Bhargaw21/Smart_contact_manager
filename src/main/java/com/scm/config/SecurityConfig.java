package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.scm.services.impln.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    // private InMemoryUserDetailsManager inMemoryUserDetailsManager;

   /*  @Bean
    public UserDetailsService userDetailsService(){

       UserDetails user1 = User.withDefaultPasswordEncoder().username("admin").password("admin123").build();
        var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1);
        return inMemoryUserDetailsManager;
    }
}*/


@Autowired
private SecurityCustomUserDetailService userDetailService;

@Autowired
private OAuthAuthenticationSuccessHandler handler;

@Autowired
private  AuthFailureHandler authFailureHandler; 


@Bean
public AuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setUserDetailsService(userDetailService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return daoAuthenticationProvider;
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
       
    httpSecurity.authorizeHttpRequests(authorize->{

       // authorize.requestMatchers("/home","/register").permitAll();

       authorize.requestMatchers("/user/**").authenticated();
       authorize.anyRequest().permitAll(); 
    });

    httpSecurity.formLogin(formLogin->{
        formLogin.loginPage("/login");
        formLogin.loginProcessingUrl("/authenticate");
        formLogin.successForwardUrl("/user/profile");
       // formLogin.failureForwardUrl("/login?error=true");
        formLogin.usernameParameter("email");
        formLogin.passwordParameter("password");

       /*  formLogin.failureHandler(new AuthenticationFailureHandler() {

            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                    AuthenticationException exception) throws IOException, ServletException {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationFailure'");
            }
            
        });

        formLogin.successHandler(new AuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'onAuthenticationSuccess'");
            }
            
        });*/

        formLogin.failureHandler(authFailureHandler);
    });


    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    httpSecurity.logout(logoutForm->{
        logoutForm.logoutUrl("/do-logout");
        logoutForm.logoutSuccessUrl("/login?logout=true");
    });

    httpSecurity.oauth2Login(oauth->{
        oauth.loginPage("/login");
        oauth.successHandler(handler);
    });



    return httpSecurity.build();
}

@Bean
public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
}
}