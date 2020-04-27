package com.codemark.hookahmix.service.searchBuilder

import com.codemark.hookahmix.domain.Mix
import org.hibernate.search.jpa.FullTextQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

/** Класс для осуществления полнотекстового поиска LUCENE по модели Микс */
@Repository
@Transactional
class MixSearch @Autowired constructor(
        @PersistenceContext val entityManager: EntityManager) {

    /** Поиск по тегам в миксе */
    fun searchTagsInMixes(text: String, ratingLimit: Double): MutableList<Mix> {
        //извлекаем fullTextEntityManager, используя entityManager
        val fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager)

        // создаем запрос при помощи Hibernate Search query DSL
        val queryBuilder = fullTextEntityManager.searchFactory
                .buildQueryBuilder().forEntity(Mix::class.java).get()

        //обозначаем поля, по которым необходимо произвести поиск
        val query = queryBuilder
                .bool()
                .must(queryBuilder.range()
                        .onField("rating").above(ratingLimit)
                        .createQuery())
                .must(queryBuilder
                        .keyword()
                        .wildcard()
//                .fuzzy()
//                .withEditDistanceUpTo(2)
//                .withPrefixLength(0)
                        .onFields("tags")
                        .matching("*${text.toLowerCase()}*")
                        .createQuery())
                .createQuery()

        //оборачиваем Lucene Query в Hibernate Query object
        val jpaQuery: FullTextQuery = fullTextEntityManager.createFullTextQuery(query, Mix::class.java)
        //возвращаем список сущностей
        return jpaQuery.resultList.map { result -> result as Mix }.toMutableList();
    }
}