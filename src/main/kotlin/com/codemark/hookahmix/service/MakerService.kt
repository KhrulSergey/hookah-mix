package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.repository.MakerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MakerService @Autowired constructor(
        private val makerRepository: MakerRepository) {

    /** Параметр, ограничивающий список записей  более данного значения */
    @Value("\${makerRatingLimitValue}")
    var makerRatingLimitValue: Double = 0.0;


    fun getOne(id: Long): Maker? {
        return makerRepository.findById(id).orElse(null);
    }

    fun getOne(title: String): Maker? {
        return makerRepository.findByTitle(title);
    }

    fun isExist(title: String): Boolean {
        return makerRepository.existsByTitle(title);
    }

    fun getAll(): List<Maker> {
        return makerRepository.findAll();
    }

    fun getAllSortedByTitle(): MutableList<Maker> {
        return makerRepository.findAllByOrderByTitleAsc();
    }

    fun getAllByLimitRatingAndSortedByTitle(ratingLimit: Double = makerRatingLimitValue): MutableList<Maker> {
        return makerRepository.findAllByRatingAfterOrderByTitleAsc(ratingLimit);
    }

    fun findAllByTitle(title: String): MutableList<Maker> {
        return makerRepository.findAllByTitleContainingIgnoreCase(title);
    }

    fun add(maker: Maker): Maker? {
        //TODO check Maker content or just save what come
        return makerRepository.save(maker);
    }
}