package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.repository.TasteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TasteService @Autowired constructor(
        private val tasteRepository: TasteRepository) {

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): List<Taste> {
        return tasteRepository.findAll();
    }

    fun isExist(taste: String): Boolean {
        return tasteRepository.existsByTaste(taste)
    }

    fun save(taste: Taste) {
        tasteRepository.save(taste)
    }
}