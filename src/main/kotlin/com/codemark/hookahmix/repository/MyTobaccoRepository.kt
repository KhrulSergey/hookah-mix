package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.MyTobacco
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MyTobaccoRepository: JpaRepository<MyTobacco, MyTobacco.MyTobaccoId> {


    @Query(nativeQuery = true,
            value = "select exists (" +
                    "select * from my_tobaccos mt " +
                    "where mt.user_id = :userId " +
                    "and mt.tobacco_id = :tobaccoId)")
    fun existsByTobaccoIdAndUserId(@Param("userId") userId: Long,
                                   @Param("tobaccoId") tobaccoId: Long): Boolean

    @Query(nativeQuery = true,
            value = "select * from my_tobaccos mt " +
                    "where mt.user_id = :userId " +
                    "and mt.tobacco_id = :tobaccoId")
    fun findByTobaccoIdAndUserId(@Param("userId") userId: Long,
                                 @Param("tobaccoId") tobaccoId: Long): MyTobacco

    @Query(nativeQuery = true,
            value = "select * from my_tobaccos mt " +
                    "where mt.user_id = :userId " +
                    "and mt.tobacco_id = :tobaccoId")
    fun getStatusByTobaccoIdAndUserId(@Param("userId") userId: Long,
                                       @Param("tobaccoId") tobaccoId: Long): String


    }