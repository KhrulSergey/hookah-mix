package com.codemark.hookahmix.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http
                    .authorizeRequests()
                    .antMatchers("/", "/*/html").permitAll()
                    .antMatchers("/api/admin", "/main.html").authenticated()
                .and()
                    .formLogin()
                .and()
                    .csrf().disable();
    }

}