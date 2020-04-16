package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoTastes
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**Репозиторий управления записями "Вкусы табака" */
@Repository
interface TobaccoTastesRepository : JpaRepository<TobaccoTastes, Long> {

    fun findAllByTobacco(tobacco: Tobacco): MutableList<TobaccoTastes>;

    fun save(tobaccoTaste: TobaccoTastes): TobaccoTastes?;

}