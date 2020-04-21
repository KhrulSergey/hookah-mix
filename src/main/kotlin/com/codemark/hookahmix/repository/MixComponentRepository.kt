package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.MixComponent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MixComponentRepository : JpaRepository<MixComponent, Long> {

    //TODO Удалить неиспользуемый метод?
    @Query(nativeQuery = true,
            value = "select c.composition from components c " +
                    "where c.mix_id = :mixId " +
                    "and c.tobacco_id = :tobaccoId")
    fun getCompositionInComponent(@Param("mixId") mixId: Long,
                                  @Param("tobaccoId") tobaccoId: Long): Int;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save (mixComponent: MixComponent): MixComponent?;
}