package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Mix
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MixRepository : JpaRepository<Mix, Long> {
    /** Возвращает запись совпадающую с указанным наименованием */
    fun findByTitle(title: String): Mix?;

    /** Возвращает true, если запись с указанным источником найдена или false если не найдена */
    fun existsBySourceUrl(sourceUrl: String): Boolean;

    /** Возвращает true, если запись с указанным наименованием найдена или false если не найдена */
    fun existsByTitle(title: String): Boolean;

    /** Возвращает список записей ограниченных снизу по рейтингу и отсортированные по рейтингу */
    fun findAllByRatingAfterOrderByRatingAsc(rating: Double): MutableList<Mix>;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save(mix: Mix): Mix?;

}