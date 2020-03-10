package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Image
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<Image, Long> {

}