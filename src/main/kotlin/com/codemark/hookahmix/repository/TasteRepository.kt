package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Taste
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TasteRepository : JpaRepository<Taste, Long> {

    fun findByTaste(taste: String): Taste

    fun existsByTaste(taste: String): Boolean
}