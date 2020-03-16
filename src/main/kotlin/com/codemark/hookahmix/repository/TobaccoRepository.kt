package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TobaccoRepository : JpaRepository<Tobacco, Long> {

    override fun findAll(pageable: Pageable) : Page<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "where m.title = :filter")
    fun findAllByMaker(@Param("filter") filter : String): List<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
            "inner join Makers m on t.maker_id = m.makers_id " +
            "order by m.title, t.title")
    fun findAllSortedByMaker(): List<Tobacco>;

//    @Query(nativeQuery = true,
//            value = "select * from Tobaccos t " +
//                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
//                    "inner join users u on mt.user_id = u.users_id " +
//                    "where u.users_id = :id " +
//                    "and mt.status = 'purchase'")
//    fun findAllPurchases(id: Long): MutableList<Tobacco>;

    fun findTobaccoByTitle(title: String): Tobacco;

}