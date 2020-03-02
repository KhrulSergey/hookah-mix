package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.util.CookieAuthorizationUtil
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
                    var userRepository: UserRepository,
                    var cookieAuthorizationUtil: CookieAuthorizationUtil) : Filter {
    override fun doFilter(servletRequest: ServletRequest?,
                          servletResponse: ServletResponse?,
                          filterChan: FilterChain?) {


        var request: HttpServletRequest = servletRequest as HttpServletRequest;
        var response: HttpServletResponse = servletResponse as HttpServletResponse;

        var installationCookie: String = "";
        var cookies = request.cookies;
        println("Check cookies...");

        if (cookies != null) {

            println("Cookie is not empty")

            var existCookie = Arrays.stream(cookies)
                    .anyMatch { i -> i.name.equals("UserId") };

            if (existCookie) {

                println("Cookie was found")

                var currentCookieValue =
                        cookieAuthorizationUtil.findCurrentCookie(
                                request.cookies);

                var existUser = userRepository.existsByInstallationCookie(currentCookieValue);
                println("User exist: $existUser")

                // if user not found -
                // create new user

                if (!existUser) {
                    cookieAuthorizationUtil.createUser(currentCookieValue);
                    println("User was created")
                }

            } else {

                // if cookie not found, user not found too -
                // create new cookie and new user

                println("Cookie not found!");

                installationCookie =
                        cookieAuthorizationUtil.generatedInstallationCookie();
                var cookie: Cookie =
                        cookieAuthorizationUtil.createCookie(installationCookie);

                response.addCookie(cookie);

                cookieAuthorizationUtil.createUser(installationCookie);
                println("User was created")
            }
        } else {

            // create new cookie and new user

            println("Cookie is empty")
            installationCookie =
                    cookieAuthorizationUtil.generatedInstallationCookie();
            println("Filter: $installationCookie")
            var cookie: Cookie =
                    cookieAuthorizationUtil.createCookie(installationCookie);
            println("Filter: cookie $installationCookie was created")
            response.addCookie(cookie);

            cookieAuthorizationUtil.createUser(installationCookie);
            println("Filter: User was created");
        }

        filterChan?.doFilter(request, response);
    }
}