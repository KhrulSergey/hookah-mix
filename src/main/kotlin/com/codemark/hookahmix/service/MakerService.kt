package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.MakerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MakerService @Autowired constructor(
        private val makerRepository: MakerRepository,
        private val tobaccoService: TobaccoService) {

    fun update(title: String) {
        var maker = makerRepository.findByTitle(title);
        makerRepository.save(maker)
    }

    fun isExist(title: String): Boolean {
        return makerRepository.existsByTitle(title)
    }

//    fun save(title: String, foundingYear: String, description: String,
////             image: Image) {
////
////        var maker = Maker()
////
////        maker.title = title
////        maker.foundingYear = foundingYear
////        maker.description = description
////
////        makerRepository.save(maker)
////    }

    fun save(maker: Maker) {
        makerRepository.save(maker)
    }


    fun getTobaccosInBar(user: User): MutableSet<Maker> {

        user.barTobaccos = makerRepository.findAllSortedByTitleAndUser(user.id)
        for (item in user.barTobaccos) {
//            item.tobaccos = tobaccoRepository.getTobaccosInBar(item.makersId, user.id)
            item.tobaccos = tobaccoService.getTobaccosInBar(item, user)
        }
        return user.barTobaccos
    }

}