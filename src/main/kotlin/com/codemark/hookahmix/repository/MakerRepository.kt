package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MakerRepository : JpaRepository<Maker, Long> {
    /** Возвращает запись совпадающую с указанным наименованием */
    fun findByTitle(title: String): Maker?

    /** Возвращает true, если запись с указанным наименованием найдена или false если не найдена */
    fun existsByTitle(title: String): Boolean

    /** Возвращает список записей отсортированные по названию */
    fun findAllByOrderByTitleAsc(): MutableList<Maker>;

    /** Возвращает список записей ограниченных снизу по рейтингу и отсортированные по названию */
    fun findAllByRatingAfterOrderByTitleAsc(rating: Double): MutableList<Maker>;

    /** Возвращает список записей содержащих переданный текст в названии*/
    fun findAllByTitleContainingIgnoreCase(title: String): MutableList<Maker>;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save(maker: Maker): Maker?;
}