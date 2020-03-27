package com.codemark.hookahmix.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http
                    .authorizeRequests()
                    .antMatchers("/", "/*/html").permitAll()
                    .antMatchers("/api/admin", "/main",
                            "/catalog_tobaccos", "/catalog_mixes")
                    .authenticated()
                    .antMatchers("/api/bar/**").permitAll()
                .and()
                    .formLogin()
                .and()
                    .cors().disable()
                    .csrf().disable()
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer? {
        return object : WebMvcConfigurerAdapter() {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins(
                        "http://192.168.2.41:19006",
                        "http://192.168.1.100:19006")
            }
        }
    }
}
