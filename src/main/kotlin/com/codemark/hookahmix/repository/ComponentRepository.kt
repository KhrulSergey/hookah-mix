package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Component
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ComponentRepository : JpaRepository<Component, Long> {

//    @Query(nativeQuery = true,
//            value = "select c from components c " +
//                    "inner join mixes m on c.mix_id = m.mixes_id " +
//                    "inner join tobaccos t on t.tobaccos_id = c.tobacco_id " +
//                    "where m.mixes_id = :mixId " +
//                    "and t.tobaccos_id = :tobaccoId")
//    fun getCompositionInComponent(@Param("mixId") mixId: Long,
//                                  @Param("tobaccoId") tobaccoId: Long): Component

//    @Query(nativeQuery = true,
//            value = "select c.composition from components c " +
//                    "where c.mix_id = :mixId " +
//                    "and c.tobacco_id = :tobaccoId")
//    fun getCompositionInComponent(@Param("mixId") mixId: Long,
//                                  @Param("tobaccoId") tobaccoId: Long): Int




}