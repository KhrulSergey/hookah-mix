package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface PurchaseRepository : JpaRepository<User, Tobacco> {

    /**
     * Добавляет табак в список купленных
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into latest_purchases (user_id, tobacco_id) values (:userId, :tobaccoId);")
    fun addInLatestPurchases(@Param("userId") userId: Long,
                             @Param("tobaccoId") tobaccoId: Long);

    /**
     * Добавляет табак в список купленных
     */
    @Transactional
    @Query(nativeQuery = true,
            value = "select case when EXISTS( select from latest_purchases where user_id = :userId and tobacco_id = :tobaccoId) then true else false end")
    fun isExistLatestPurchases(@Param("userId") userId: Long,
                               @Param("tobaccoId") tobaccoId: Long): Boolean;

    /**
     * Удаляет табак из списка купленных
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from latest_purchases lt where lt.user_id = :userId and lt.tobacco_id = :tobaccoId")
    fun deleteOneFromLatestPurchases(@Param("userId") userId: Long,
                                     @Param("tobaccoId") tobaccoId: Long);
}