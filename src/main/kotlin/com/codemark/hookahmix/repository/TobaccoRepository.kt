package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TobaccoRepository : JpaRepository<Tobacco, Long> {

    override fun findAll(pageable: Pageable) : Page<Tobacco>;
    fun findAllByMaker(pageable: Pageable, maker : String) : Page<Tobacco>;

}