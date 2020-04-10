package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Mix
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MixRepository : JpaRepository<Mix, Long> {

    @Query(nativeQuery = true,
            value = "select c.composition from components c " +
                    "inner join mixes m on m.mixes_id = c.mix_id " +
                    "where c.tobacco_id = :tobaccoId " +
                    "and c.mix_id = :mixId")
    fun getCompositionInMix(@Param("tobaccoId") tobaccoId: Long,
                            @Param("mixId") mixId: Long): Int;

    fun findByTitle(title: String): Mix;

    fun existsByTitle(title: String): Boolean;

}