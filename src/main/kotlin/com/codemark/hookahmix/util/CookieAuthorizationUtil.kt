package com.codemark.hookahmix.util

import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Component
class CookieAuthorizationUtil {

    fun getInstallationCookie(request: HttpServletRequest,
                              session: HttpSession): String {

        var installationCookie = ""

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            println("From session...")
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        return installationCookie;
    }

}