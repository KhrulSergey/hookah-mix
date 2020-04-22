package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** Репозиторий управления записями "Пользователи" */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByInstallationCookie(installationCookie: String): Boolean;

    fun findUserByInstallationCookie(installationCookie: String): User;
}