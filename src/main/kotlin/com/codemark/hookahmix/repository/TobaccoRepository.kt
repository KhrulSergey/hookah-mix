package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TobaccoRepository : JpaRepository<Tobacco, Long> {

    /**
     * Возвращает список последних купленных табаков
     */
    @Query(nativeQuery = true,
            value = "select * from tobaccos t " +
                    "inner join latest_purchases lt on t.tobaccos_id = lt.tobacco_id " +
                    "inner join users u on lt.user_id = u.users_id " +
                    "where u.users_id = :userId " +
                    "order by t.tobaccos_id")
    fun findLatestPurchases(@Param("userId") userId: Long): MutableSet<Tobacco>;

    /**
     * Возвращает null или табак с заданным ID и производителем.
     */
    fun findByTitleAndMaker(title: String, maker: Maker): Tobacco?;

    //TODO разобраться с 2мя методами ниже
    override fun findAll(pageable: Pageable) : Page<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "where m.title = :filter")
    fun findAllByMaker(@Param("filter") filter : String): List<Tobacco>;
}