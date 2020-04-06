package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MakerService @Autowired constructor(
        private val makerRepository: MakerRepository,
        private val tobaccoRepository: TobaccoRepository,
        private val tobaccoService: TobaccoService) {

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): List<Maker>{
        return makerRepository.findAll();
    }

    fun getOne(title: String): Maker?{
        return makerRepository.findByTitle(title).orElse(null);
    }

    fun update(title: String) {
        val maker = makerRepository.findByTitle(title).get();

    @Transactional
    fun update(maker: Maker) {
        makerRepository.save(maker)
    }

    fun getAll(): MutableList<Maker> {
        return makerRepository.findAll()
    }

    fun isExist(title: String): Boolean {
        return makerRepository.existsByTitle(title)
    }

    fun save(maker: Maker) {
        makerRepository.save(maker)
    }

    fun getTobaccosInCatalog(user: User): MutableList<Maker> {

        var tobaccos = makerRepository.findAllSortedByTitle()
        for (item in tobaccos) {
            for (tobacco in item.tobaccos) {
                if (tobaccoService.getTobaccosInBar(item, user).contains(tobacco)) {
                    tobacco.status = TobaccoStatus.CONTAIN_BAR
                } else if (tobaccoService.getTobaccosFromPurchases(user).contains(tobacco)) {
                    tobacco.status = TobaccoStatus.IN_PURCHASES
                } else {
                    tobacco.status = TobaccoStatus.NEED_BAR
                }
            }
        }
        return tobaccos
    }


    fun getTobaccosInBar(user: User): MutableSet<Maker> {

        user.barTobaccos = makerRepository.findAllSortedByTitleAndUser(user.id)
        for (item in user.barTobaccos) {
            item.tobaccos = tobaccoService.getTobaccosInBar(item, user)
            if (item.tobaccos.isEmpty()) {
                return emptyList<Maker>().toMutableSet()
            } else {
                for (tobacco in item.tobaccos) {
                    tobacco.status = TobaccoStatus.CONTAIN_BAR
                }
            }
        }
        return user.barTobaccos
    }

}