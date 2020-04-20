package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TasteRepository : JpaRepository<Taste, Long> {

    fun findByTitle(title: String): Taste?

    /**
     * Ищет в БД нечеткое совпадение наименования табака и заданного производителя
     * Возвращает null или табак с заданным ID.
     */
    fun findAllByTitleContaining(title: String): MutableList<Taste>;

    fun existsByTitle(title: String): Boolean
}