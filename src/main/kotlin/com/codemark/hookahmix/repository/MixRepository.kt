package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
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

    fun findByTitle(title: String): Mix?;

    fun existsBySourceUrl(sourceUrl: String): Boolean;

    fun existsByTitle(title: String): Boolean;

    /** Возвращает список записей ограниченных снизу по рейтингу и отсортированные по рейтингу */
    fun findAllByRatingAfterOrderByRatingAsc(rating: Double): MutableList<Mix>;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save (mix:Mix): Mix?;

}