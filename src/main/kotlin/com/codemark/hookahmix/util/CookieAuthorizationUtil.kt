package com.codemark.hookahmix.util

import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class CookieAuthorizationUtil @Autowired constructor(private var userRepository: UserRepository) {

    fun generatedInstallationCookie(): String {
        return UUID.randomUUID().toString();
    }

}