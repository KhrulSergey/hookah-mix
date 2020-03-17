package com.codemark.hookahmix.controller

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
import javax.servlet.http.HttpSession

@Component
class CookiesFilter(@Autowired
                    var userRepository: UserRepository,
                    var cookieAuthorizationUtil: CookieAuthorizationUtil) : Filter {

    override fun doFilter(servletRequest: ServletRequest?,
                          servletResponse: ServletResponse?,
                          filterChain: FilterChain?) {

        var request: HttpServletRequest = servletRequest as HttpServletRequest;
        var response: HttpServletResponse = servletResponse as HttpServletResponse;
        var session: HttpSession = request.session;

        var installationCookie: String = "";
        var cookies = request.cookies;
        println("Filter: check cookies...");

        if (cookies != null) {

            println("Filter: cookie is not empty")

            var existCookie = Arrays.stream(cookies)
                    .anyMatch { i -> i.name.equals("UserId") };

            if (existCookie) {

                println("Filter: cookie was found")

                var currentCookieValue =
                        cookieAuthorizationUtil.findCurrentCookie(
                                request.cookies);

                var existUser =
                        userRepository.existsByInstallationCookie(currentCookieValue);
                println("Filter: user exist: $existUser")

                session.setAttribute("installationCookie", currentCookieValue)

                // if user not found -
                // create new user

                if (!existUser) {
                    cookieAuthorizationUtil.createUser(currentCookieValue);
                    println("Filter: user was created")
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
                session.setAttribute("installationCookie", installationCookie)

                cookieAuthorizationUtil.createUser(installationCookie);
                println("Filter: user was created");
            }
        } else {

            // create new cookie and new user

            println("Filter: cookie is empty");
            installationCookie =
                    cookieAuthorizationUtil.generatedInstallationCookie();
            println("Filter: $installationCookie");

            var cookie: Cookie =
                    cookieAuthorizationUtil.createCookie(installationCookie);
            println("Filter: cookie $installationCookie was created");

            response.addCookie(cookie);

            session.setAttribute("installationCookie", installationCookie)

            cookieAuthorizationUtil.createUser(installationCookie);
            println("Filter: User was created");
        }

        filterChain?.doFilter(request, response);
    }
}