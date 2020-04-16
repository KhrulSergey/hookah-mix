package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoTastes
import com.codemark.hookahmix.repository.TasteRepository
import com.codemark.hookahmix.repository.TobaccoTastesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/** Управление записями вкусов и связями вкусов с табаками */
@Service
class TasteService @Autowired constructor(
        private val tasteRepository: TasteRepository,
        private val tobaccosTastesRepository: TobaccoTastesRepository) {

    fun getAllTastes(): List<Taste> {
        return tasteRepository.findAll();
    }

    fun getOneTastes(title: String): Taste? {
        return tasteRepository.findByTitle(title);
    }

    fun getTobaccoTastes(tobacco: Tobacco): MutableList<Taste> {
        return tobaccosTastesRepository.findAllByTobacco(tobacco).map { it.taste!! }.toMutableList();
    }

    fun isExistTaste(taste: String): Boolean {
        return tasteRepository.existsByTitle(taste)
    }

    fun addTaste(taste: Taste): Taste? {
        val savedTaste = tasteRepository.save(taste);
        if (savedTaste.id == 0L) return null;
        return savedTaste;
    }

    fun saveTobaccoTaste(taste: Taste, tobacco: Tobacco): Boolean {
        val newTobaccoTasteEntry = tobaccosTastesRepository.save(TobaccoTastes(taste, tobacco));
        return newTobaccoTasteEntry != null;
    }
}