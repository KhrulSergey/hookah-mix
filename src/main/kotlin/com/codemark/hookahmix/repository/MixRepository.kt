package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Mix
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MixRepository : JpaRepository<Mix, Long> {
}