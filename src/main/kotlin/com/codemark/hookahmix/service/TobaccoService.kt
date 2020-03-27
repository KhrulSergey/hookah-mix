package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TobaccoService @Autowired constructor(
        private val tobaccoRepository: TobaccoRepository,
        private val userService: UserService) {

    @Transactional
    fun save(tobacco: Tobacco) {
        tobaccoRepository.save(tobacco)
    }

    fun isExist(tobaccoId: Long): Boolean {
        return tobaccoRepository.existsByTobaccosId(tobaccoId)
    }

    fun getTobaccosInBar(maker: Maker, user: User): MutableSet<Tobacco> {
        return tobaccoRepository.getTobaccosInBar(maker.makersId, user.id)
    }

    fun deleteTobaccoFromBar(user: User, id: Long): Unit {

        var iterator = user.tobaccos.iterator();
        while (iterator.hasNext()) {
            var tobacco = iterator.next();
            if (tobacco.tobaccosId == id) {
                iterator.remove();
                userService.save(user);
                println("Tobacco ${tobacco.title} was successfully removed!");
                println(user.tobaccos)
            }
        }
    }

}