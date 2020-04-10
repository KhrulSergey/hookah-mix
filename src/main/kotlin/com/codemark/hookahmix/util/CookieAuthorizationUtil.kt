package com.codemark.hookahmix.util

import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Component
class CookieAuthorizationUtil {

    fun getInstallationCookie(request: HttpServletRequest,
                              session: HttpSession): String {

        return if (request.getHeader("X-UserId") != null) {
            request.getHeader("X-UserId");
        } else {
            session.getAttribute("installationCookie").toString();
        }
    }

}