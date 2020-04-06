package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<User, Long>{

    fun existsByInstallationCookie(installationCookie: String): Boolean;

    fun findUserByInstallationCookie(installationCookie: String): User;

    //TODO Проверить необходимость метода. Перенести метод в репо-Табаки
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update my_tobaccos set status = 'purchase' " +
                    "where tobacco_id = :tobaccoId " +
                    "and user_id = :userId")
    fun addTobaccoInPurchases(@Param("tobaccoId") tobaccoId: Long,
                              @Param("userId") userId: Long): Unit;

    //TODO Проверить необходимость метода. Перенести метод в репо-Табаки
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update my_tobaccos set status = 'in bar' " +
                    "where tobacco_id = :tobaccoId " +
                    "and user_id = :userId")
    fun addTobaccoInBar(@Param("tobaccoId") tobaccoId: Long,
                              @Param("userId") userId: Long): Unit;



}