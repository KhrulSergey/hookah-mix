package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface UserRepository : JpaRepository<User, Long>{

    fun existsByInstallationCookie(installationCookie: String): Boolean;

    fun findUserByInstallationCookie(installationCookie: String): User;
}