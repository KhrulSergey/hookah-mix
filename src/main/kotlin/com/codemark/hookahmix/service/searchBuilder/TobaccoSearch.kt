package com.codemark.hookahmix.service.searchBuilder

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import org.hibernate.search.jpa.FullTextQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/** Класс для осуществления полнотекстового поиска LUCENE по модели Микс */
@Repository
@Transactional
class TobaccoSearch @Autowired constructor(
        @PersistenceContext val entityManager: EntityManager) {

    /** Возвращает список производителей содержащий переданный текст в названии производителя и/или табака */
    fun searchMakerCatalog(text: String): MutableList<Maker> {
        //извлекаем fullTextEntityManager, используя entityManager
        val fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager)

        // создаем запрос при помощи Hibernate Search query DSL
        val queryBuilder = fullTextEntityManager.searchFactory
                .buildQueryBuilder().forEntity(Maker::class.java).get()

        //обозначаем поля, по которым необходимо произвести поиск
        val query = queryBuilder
                .keyword()
                .onField("title")
                .andField("tobaccos.title")
                .matching(text)
                .createQuery()

        //оборачиваем Lucene Query в Hibernate Query object
        val jpaQuery: FullTextQuery = fullTextEntityManager.createFullTextQuery(query, Maker::class.java)

        //возвращаем список сущностей
        return jpaQuery.resultList.map { result -> result as Maker }.toMutableList();
    }

    /** Возвращает список табаков содержащий переданный текст в названии табака и рейтингом выше переданного */
    fun searchTobaccoCatalogByTitleAndLimitRating(text: String, ratingLimit: Double): MutableList<Tobacco> {
        //извлекаем fullTextEntityManager, используя entityManager
        val fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager)

        // создаем запрос при помощи Hibernate Search query DSL
        val queryBuilder = fullTextEntityManager.searchFactory
                .buildQueryBuilder().forEntity(Tobacco::class.java).get()

        //обозначаем поля, по которым необходимо произвести поиск
        val query = queryBuilder
                .bool()
                .must(queryBuilder.range()
                        .onField("rating").above(ratingLimit)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .wildcard()
                        .onField("title")
                        .matching("*${text.toLowerCase()}*")
                        .createQuery())
                .createQuery()

        //оборачиваем Lucene Query в Hibernate Query object
        val jpaQuery: FullTextQuery = fullTextEntityManager.createFullTextQuery(query, Tobacco::class.java)

        //возвращаем список сущностей
        return jpaQuery.resultList.map { result -> result as Tobacco }.toMutableList();
    }

    /** Возвращает список табаков Пользователя содержащий переданный текст в названии табака */
    fun searchBarTobacco(text: String, tobaccoList: MutableList<Tobacco>): MutableList<Tobacco> {
        val searchedList = tobaccoList.filter { tobacco ->
            tobacco.title.contains(text, ignoreCase = true) || tobacco.maker!!.title.contains(text, ignoreCase = true)
        }
        return searchedList.toMutableList();
    }
}