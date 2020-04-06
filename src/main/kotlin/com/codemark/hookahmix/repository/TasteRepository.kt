package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Taste
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TasteRepository : JpaRepository<Taste, Long> {

    fun findByTitle(title: String): Optional<Taste>

    fun existsByTitle(title: String): Boolean
}