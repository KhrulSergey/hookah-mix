package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Component
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ComponentRepository : JpaRepository<Component, Long> {

}