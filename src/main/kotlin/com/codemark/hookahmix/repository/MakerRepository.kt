package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MakerRepository : JpaRepository<Maker, Long> {

    fun findByTitle(title : String) : Maker;

    @Query(nativeQuery = true, value = "select * from Makers m order by m.title")
    fun findAllSortedByTitle(): List<Maker>;

//    @Query(nativeQuery = true, value = "select * from Makers m " +
//            "inner join Tobaccos t on m.makers_id = t.maker_id " +
//            "where m.title = :title")
//    fun getOneByTobacco(@Param("title") title: String?): Maker;

    @Query(nativeQuery = true, value = "select * from Makers m " +
            "where m.title = :title order by m.title")
    fun getOneByTobacco(@Param("title") title: String?): Maker;

}