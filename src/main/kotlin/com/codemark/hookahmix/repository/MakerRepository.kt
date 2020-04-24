package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MakerRepository : JpaRepository<Maker, Long> {

    fun findByTitle(title : String) : Maker?

    fun existsByTitle(title : String) : Boolean

    fun findAllByOrderByTitleAsc(): MutableList<Maker>;

    fun findAllByTitleContainingIgnoreCase(title:String): MutableList<Maker>;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save (maker: Maker): Maker?;
}