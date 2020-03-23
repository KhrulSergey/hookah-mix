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
    fun findAllPurchases(@Param("userId") userId: Long): MutableList<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join my_tobaccos mt on t.tobaccos_id = mt.tobacco_id " +
                    "inner join users u on mt.user_id = u.users_id " +
                    "where u.users_id = :userId " +
                    "and mt.status = 'purchase'" +
                    "order by t.tobaccos_id desc limit 5")
    fun findLatestPurchases(@Param("userId") userId: Long?): MutableList<Tobacco>

    fun findByTitle(title: String): Tobacco;

    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join makers m on t.maker_id = m.makers_id " +
                    "where t.title = :tobaccoTitle " +
                    "and m.title = :makerTitle")
    fun findOneByTitleAndMaker(@Param("tobaccoTitle") tobaccoTitle: String,
                               @Param("makerTitle") makerTitle: String): Tobacco

    fun existsByTobaccosId(tobaccoId: Long): Boolean


    @Query(nativeQuery = true,
            value = "select c.composition from components c " +
                    "inner join mixes m on c.mix_id = m.mixes_id " +
                    "inner join tobaccos t on t.tobaccos_id = c.tobacco_id " +
                    "where m.mixes_id = :mixId " +
                    "and t.tobaccos_id = :tobaccoId")
    fun getCompositionInComponent(@Param("mixId") mixId: Long,
                                  @Param("tobaccoId") tobaccoId: Long): Int


//    fun isCorrectComposition(): Boolean



}