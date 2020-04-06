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

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun register(response: HttpServletResponse) {

        var key = "UserId"
        var installationCookie = UUID.randomUUID().toString();

        response.setHeader(key, installationCookie)
        var user = User(installationCookie)

        if (userRepository.existsByInstallationCookie(installationCookie)) {
            println("User with ID $installationCookie already exists")
        } else {
            userRepository.save(user)
        }
    }

    fun save(user: User): Unit {
        userRepository.save(user)
    }

    fun isUserExists(installationCookie: String): Boolean {
        return userRepository.existsByInstallationCookie(installationCookie)
    }

    fun findUserByInstallationCookie(installationCookie: String): User {
        return userRepository.findUserByInstallationCookie(installationCookie)
    }

}