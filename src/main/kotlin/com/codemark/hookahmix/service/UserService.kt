package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletResponse

@Service
class UserService @Autowired constructor(
        private val userRepository: UserRepository) {

    fun getOneByInstallationCookie(installationCookie: String): User {
        return userRepository.findUserByInstallationCookie(installationCookie)
    }

    fun getOne(id: Long): User {
        return userRepository.getOne(id)
    }

    fun register(response: HttpServletResponse) {

        val key = "UserId"
        val installationCookie = UUID.randomUUID().toString();

        response.setHeader(key, installationCookie)
        val user = User(installationCookie)

        if (userRepository.existsByInstallationCookie(installationCookie)) {
            println("User with ID $installationCookie already exists")
        } else {
            userRepository.save(user)
        }
    }

    fun save(user: User): Unit {
        userRepository.save(user)
    }

}