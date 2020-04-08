package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MakerService @Autowired constructor(
        private val makerRepository: MakerRepository,
        private val imageService: ImageService,
        private val tobaccoService: TobaccoService) {

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): List<Maker> {
        return makerRepository.findAll();
    }

    fun getOne(title: String): Maker? {
        return makerRepository.findByTitle(title);
    }

    @Transactional
    fun update(maker: Maker) {
        makerRepository.save(maker)
    }

    fun isExist(title: String): Boolean {
        return makerRepository.existsByTitle(title)
    }

    fun add(maker: Maker): Maker? {
        var newMaker = Maker()
        //TODO check Maker content or just save what come
        newMaker = makerRepository.save(maker);
        //check maker creation
        if (newMaker.id == 0L) return null;
        return newMaker;

    }

    fun getTobaccosInCatalog(user: User): MutableList<Maker> {

        val makerList = makerRepository.findAllSortedByTitle()
        for (maker in makerList) {
            maker.image!!.name = imageService.getFileWebPath() + maker.image!!.name;
            for (tobacco in maker.tobaccos) {
                tobacco.image!!.name = imageService.getFileWebPath() + tobacco.image!!.name;
                when {
                    tobaccoService.getTobaccosInBar(maker, user).contains(tobacco) -> {
                        tobacco.status = TobaccoStatus.CONTAIN_BAR
                    }
                    tobaccoService.getTobaccosFromPurchases(user).contains(tobacco) -> {
                        tobacco.status = TobaccoStatus.IN_PURCHASES
                    }
                    else -> {
                        tobacco.status = TobaccoStatus.NEED_BAR
                    }
                }
            }
        }
        return makerList
    }


    fun getTobaccosInBar(user: User): MutableSet<Maker> {

        user.barTobaccos = makerRepository.findAllSortedByTitleAndUser(user.id)
        for (maker in user.barTobaccos) {
            maker.image!!.name = imageService.getFileWebPath() + maker.image!!.name;
            maker.tobaccos = tobaccoService.getTobaccosInBar(maker, user)
            if (maker.tobaccos.isEmpty()) {
                return emptyList<Maker>().toMutableSet()
            } else {
                for (tobacco in maker.tobaccos) {
                    tobacco.image!!.name = imageService.getFileWebPath() + tobacco.image!!.name;
                    tobacco.status = TobaccoStatus.CONTAIN_BAR
                }
            }
        }
        return user.barTobaccos
    }

}