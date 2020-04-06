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

    fun get(title:String): Taste?{
        return tasteRepository.findByTitle(title).orElse(null);
    }

    fun isExist(taste: String): Boolean {
        return tasteRepository.existsByTitle(taste)
    }

    fun add(taste: Taste): Taste? {
        var savedTaste = tasteRepository.save(taste);
        if(savedTaste.id == 0L) return null;
        return savedTaste;
    }
}