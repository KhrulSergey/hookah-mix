package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MakerRepository : JpaRepository<Maker, Long> {

    fun findByTitle(title : String) : Maker;
}