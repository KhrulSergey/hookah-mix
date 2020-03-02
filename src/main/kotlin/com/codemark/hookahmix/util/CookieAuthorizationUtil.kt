package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.Cookie

@Component
class CookieAuthorizationUtil @Autowired constructor(
        private var userRepository: UserRepository) {

    fun generatedInstallationCookie(): String {
        return UUID.randomUUID().toString();
    }

    fun createCookie(installationCookie: String): Cookie {

        var cookie = Cookie("UserId", installationCookie);
        cookie.path = "/";
        cookie.maxAge = 600;
        return cookie;
    }

    fun createUser(installationCookie: String): Unit {
        var user = User(installationCookie);
        userRepository.save(user);
    }

    fun findCurrentCookie(cookies: Array<Cookie>): String {

        var currentCookie: String = Arrays.stream(cookies)
                .filter { i -> i.name.equals("UserId") }
                .findAny()
                .get().value;

        return currentCookie;
    }

}