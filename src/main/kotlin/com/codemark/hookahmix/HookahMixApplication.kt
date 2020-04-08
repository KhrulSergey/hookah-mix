package com.codemark.hookahmix

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class HookahMixApplication : SpringBootServletInitializer() {

    override fun configure(builder: SpringApplicationBuilder?): SpringApplicationBuilder? {
        return builder?.sources(HookahMixApplication::class.java)
    }
}

fun main(args: Array<String>) {
    System.setProperty("server.port", "8888");
    System.setProperty("server.servlet.context-path", "/hookah-mix");
    SpringApplication.run(HookahMixApplication::class.java, *args)
}

