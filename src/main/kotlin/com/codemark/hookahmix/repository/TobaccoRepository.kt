package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
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
     * Ищет в БД точное совпадение наименования табака и производителя
     * Возвращает null или табак с заданным ID и производителем.
     */
    fun findByTitleIgnoreCaseAndMaker(title: String, maker: Maker): Tobacco?;

    /**
     * Ищет в БД нечеткое совпадение наименования табака и заданного производителя
     * Возвращает null или табак с заданным ID.
     */
    fun findAllByTitleContainingIgnoreCaseAndMaker(title: String, maker: Maker): MutableList<Tobacco>;

    //TODO разобраться с 2мя методами ниже
    override fun findAll(pageable: Pageable): Page<Tobacco>;

    @Query(nativeQuery = true,
            value = "select * from Tobaccos t " +
                    "inner join Makers m on t.maker_id = m.makers_id " +
                    "where m.title = :filter")
    fun findAllByMaker(@Param("filter") filter: String): List<Tobacco>;

    /** Возращает список табаков соответствующих заданному вкусу и производителю */
    @Query(nativeQuery = true,
            value = "select * from tobaccos t inner join tobacco_tastes ta on t.tobaccos_id = ta.tobaccos_id " +
                    "where t.maker_id = :maker_id and ta.tastes_id = :taste_id")
    fun findAllByTasteAndMaker(taste_id: Long, maker_id: Long): MutableList<Tobacco>;

    /** Возвращает список записей ограниченных снизу по рейтингу и отсортированные по названию */
    fun findAllByRatingAfterOrderByMaker(rating: Double): MutableList<Tobacco>;

    /** Возвращает сохраненную запись или null в случае неудачи */
    fun save (tobacco: Tobacco): Tobacco?;
}