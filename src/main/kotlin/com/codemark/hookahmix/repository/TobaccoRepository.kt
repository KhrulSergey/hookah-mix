package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional

@Repository
interface TobaccoRepository : JpaRepository<Tobacco, Long> {

    override fun findAll(pageable: Pageable) : Page<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "where m.title = :filter")
    fun findAllByMaker(@Param("filter") filter : String): List<Tobacco>;

    //TODO Удалить неиспользуемый метод?
    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "order by m.title, t.title")
    fun findAllSortedByMaker(): List<Tobacco>;

//    fun findByTitle(title: String): Tobacco;

    fun findByTitle(title: String): MutableSet<Tobacco>

    fun findByTitleAndMaker(title: String, maker: Maker): Tobacco?;

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join makers m on t.maker_id = m.makers_id " +
                    "where t.title = :tobaccoTitle " +
                    "and m.title = :makerTitle")
    fun findOneByTitleAndMaker(@Param("tobaccoTitle") tobaccoTitle: String,
                               @Param("makerTitle") makerTitle: String): Tobacco

    //TODO Удалить неиспользуемый метод?
    @Query(nativeQuery = true,
            value = "select case when t.title = :tobaccoTitle and m.title = :makerTitle " +
                    "then true else false end from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "where t.title = :tobaccoTitle and m.title = :makerTitle")
    fun existsByTitleAndMaker(@Param("tobaccoTitle") tobaccoTitle: String,
                              @Param("makerTitle") makerTitle: String): Boolean

    fun existsByTitle(title: String): Boolean

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
                    "inner join users u on mt.user_id = u.users_id " +
                    "where u.users_id = :userId " +
                    "and mt.status = 'purchase'")
    fun findAllPurchases(@Param("userId") userId: Long): MutableList<Tobacco>

    //TODO Удалить неиспользуемый метод?
//    @Query(nativeQuery = true,
//            value = "select * from tobaccos t " +
//                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
//                    "inner join users u on mt.user_id = u.users_id " +
//                    "where u.users_id = :userId " +
//                    "and mt.status = 'purchase'" +
//                    "order by t.tobaccos_id desc limit 5")
//    fun findLatestPurchases(@Param("userId") userId: Long?): MutableList<Tobacco>

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join latest_purchases lt on t.tobaccos_id = lt.tobacco_id " +
                    "inner join users u on lt.user_id = u.users_id " +
                    "where u.users_id = :userId " +
                    "order by t.tobaccos_id desc limit 5")
    fun findLatestPurchases(@Param("userId") userId: Long): MutableList<Tobacco>

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "insert into latest_purchases (user_id, tobacco_id)" +
                    "values (:userId, :tobaccoId);")
    fun addInLatestPurchases(@Param("userId") userId: Long,
                             @Param("tobaccoId") tobaccoId: Long): Unit

    fun existsByTobaccosId(tobaccoId: Long): Boolean

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join makers m on t.maker_id = m.makers_id " +
                    "inner join my_tobaccos mt on mt.tobacco_id = t.tobaccos_id " +
                    "where m.makers_id = :makerId and mt.user_id = :userId " +
                    "and status = 'contain bar'")
    fun getTobaccosInBar(@Param("makerId") makerId: Long,
                         @Param("userId") userId: Long): MutableSet<Tobacco>


    //TODO Удалить неиспользуемый метод?
    @Query(nativeQuery = true,
            value = "select mt.status from my_tobaccos mt " +
                    " where mt.user_id = :userId and tobacco_id = :tobaccoId")
    fun getTobaccoStatus(@Param("userId") userId: Long,
                         @Param("tobaccoId") tobaccoId: Long): String
}