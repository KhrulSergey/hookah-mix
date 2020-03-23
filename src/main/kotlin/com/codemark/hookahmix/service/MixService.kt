package com.codemark.hookahmix.service

import com.codemark.hookahmix.repository.MixRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MixService @Autowired constructor(
        private val mixRepository: MixRepository) {


}