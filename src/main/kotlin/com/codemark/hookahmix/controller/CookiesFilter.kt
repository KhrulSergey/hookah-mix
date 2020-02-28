package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CookiesFilter(@Autowired
                    var userRepository: UserRepository) : Filter {
    override fun doFilter(servletRequest: ServletRequest?,
                          servletResponse: ServletResponse?,
                          filterChan: FilterChain?) {


        var request: HttpServletRequest = servletRequest as HttpServletRequest;
        var response: HttpServletResponse = servletResponse as HttpServletResponse;

        var installationCookie: String = "";
        var cookies= request.cookies;
        println("Check cookies...");

        if (cookies != null) {

            println("Cookie is not empty")

            var existCookie = Arrays.stream(cookies)
                    .anyMatch { i -> i.name.equals("UserId") };

            if (existCookie) {

                println("Cookie was found")
                var currentCookieValue: String = Arrays.stream(cookies)
                        .filter { i -> i.name.equals("UserId") }
                        .findFirst()
                        .get().value;

                var existUser = userRepository.existsByInstallationCookie(currentCookieValue);
                println("User exist: $existUser")

                // if user not found -
                // create new user

                if (!existUser) {
                    var user: User = User(currentCookieValue);
                    userRepository.save(user);
                    println("User was created")
                }

            } else {

                // if cookie not found, user not found too -
                // create new cookie and new user

                println("Cookie not found!")

                installationCookie = generatedInstallationCookie();
                var cookie: Cookie = Cookie("UserId", installationCookie);
                cookie.path = "/"
                cookie.maxAge = 600;
                response.addCookie(cookie);

                var user: User = User(installationCookie);
                userRepository.save(user);
                println("User was created")

            }
        } else {

            // create new cookie and new user

            println("Cookie is empty")
            installationCookie = generatedInstallationCookie();
            var cookie: Cookie = Cookie("UserId", installationCookie);
            cookie.path = "/"
            cookie.maxAge = 600;
            response.addCookie(cookie);

            var user: User = User(installationCookie);
            userRepository.save(user);
            println("User was created");
        }

        filterChan?.doFilter(request, response);
    }

    private fun generatedInstallationCookie(): String {
        return UUID.randomUUID().toString();
    }

}