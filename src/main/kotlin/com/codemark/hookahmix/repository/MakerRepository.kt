package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MakerRepository : JpaRepository<Maker, Long> {

    //TODO Проверить возможен ли нативный запрос
    @Query(nativeQuery = true, value = "select * from Makers m order by m.title")
    fun findAllSortedByTitle(): MutableList<Maker>;

    @Query(nativeQuery = true,
            value = "select * from makers m " +
                    "inner join tobaccos t on m.makers_id = t.maker_id " +
                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
                    "inner join users u on mt.user_id = u.users_id " +
                    "where u.users_id = :userId " +
                    "order by m.title")
    fun findAllSortedByTitleAndUser(@Param("userId") userId: Long): MutableSet<Maker>

    fun findByTitle(title : String) : Optional<Maker>

    fun existsByTitle(title : String) : Boolean

    //TODO Проверить использование метода - отрефакторить и Искать Производителя по ID
    @Query(nativeQuery = true,
            value = "select * from Makers m " +
                    "inner join Tobaccos t on m.makers_id = t.maker_id " +
                    "where t.title = :tobaccoTitle")
    fun getOneByTobacco(@Param("tobaccoTitle") tobaccoTitle: String): Maker

}