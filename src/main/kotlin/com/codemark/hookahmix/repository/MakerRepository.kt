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

    @Query(nativeQuery = true,
            value = "select * from makers m inner join tobaccos t on m.makers_id = t.maker_id " +
                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
                    "inner join users u on mt.user_id = u.users_id " +
                    "where u.users_id = :userId order by m.title")
    fun findAllSortedByTitleAndUser(@Param("userId") userId: Long): MutableSet<Maker>;
}