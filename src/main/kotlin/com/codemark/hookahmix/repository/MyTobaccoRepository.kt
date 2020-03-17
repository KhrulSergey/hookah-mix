package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.MyTobacco
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MyTobaccoRepository: JpaRepository<MyTobacco, MyTobacco.MyTobaccoId> {

}