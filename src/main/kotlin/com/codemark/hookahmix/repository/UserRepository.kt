package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{

//    @Query(nativeQuery = true, value = "select u.users_id, u.installation_cookie from Users u " +
//            "where u.installation_cookie = :cookie")
//    fun getUserByCookie(@Param("cookie") cookie: String): User;

//    @Query("select case when count(u) > 0 then true else false end " +
//            "from Users u where u.istallationCookie = :installationCookie")
//    fun existUser(@Param("cookie") cookie: String): Boolean

    fun existsByInstallationCookie(installationCookie: String): Boolean;

    fun findUserByInstallationCookie(installationCookie: String): User;
}