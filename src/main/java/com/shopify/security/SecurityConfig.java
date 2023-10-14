package com.shopify.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.shopify.filter.UserBlockingFilter;
import com.shopify.service.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    @Autowired
    CustomUserDetailService customUserDetailService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login", "/", "/home", "/shop/category/{id}", "/shop", "/shop/viewproduct/{id}", "/register", "/js/**", "/css/**", "/images/**", 
                		"/assetsAdmin/**", "/banner/**","/static/**","/forgetPassword","/passwordReset-email","/verifyPassword-otp","/reset-password").permitAll()
                .antMatchers("/admin/**", "/orders/update-status").hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/home", true)
                .usernameParameter("email")
                .passwordParameter("password")
                .and()
//                .oauth2Login()
//                .loginPage("/login")
//                .successHandler(googleOAuth2SuccessHandler)
//                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .and()
                .csrf().disable();
        http.headers().frameOptions().disable();

        //user blocking configuration
        http
                .addFilterBefore(userBlockingFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public UserBlockingFilter userBlockingFilter() {
        return new UserBlockingFilter();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/images/**", "/productImages/**", "/profileImages/**", "/css/**", "/js/**");
    }


//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig{
//	
//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//    
//        .csrf().disable()
//                .authorizeHttpRequests((authorize) ->
//                        authorize.antMatchers("/index/**").permitAll()
//                        .antMatchers("/resources/**").permitAll()
//                        .antMatchers("/register").permitAll()
//                                .antMatchers("/","/login").permitAll()
//                                .antMatchers("/images/**").permitAll()
//                                .antMatchers("/hello").authenticated()
//                ).formLogin(
//                        form -> form
//                                .loginPage("/login")
//                                
//                                .loginProcessingUrl("/home")
//                                .defaultSuccessUrl("/home")
//                                
//                                .permitAll()
//                ).logout().logoutSuccessUrl("/logout").deleteCookies("JSESSIONID");
//        return http.build();
//}
//	
//	
//	  @Bean
//	    public BCryptPasswordEncoder bCryptPasswordEncoder(){
//	        return new BCryptPasswordEncoder();
//	    }
}



