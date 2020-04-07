package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.Maker
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository : JpaRepository<Image, Long> {

    fun findByName(name : String) : Image?

}