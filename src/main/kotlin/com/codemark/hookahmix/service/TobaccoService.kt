package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TobaccoService @Autowired constructor(
        private val tobaccoRepository: TobaccoRepository,
        private val myTobaccoRepository: MyTobaccoRepository,
        private val makerRepository: MakerRepository,
        private val userService: UserService) {

    fun getAll(): List<Tobacco> {
        return tobaccoRepository.findAll()
    }

    fun getAllByMaker(makerTitle: String): List<Tobacco> {
        return tobaccoRepository.findAllByMaker(makerTitle);
    }

    fun getOne(id: Long): Tobacco {
        return tobaccoRepository.getOne(id)
    }

    fun getOne(title: String): Tobacco {
        return tobaccoRepository.findByTitle(title)
    }

    fun getOne(title: String, maker: Maker): Tobacco? {
        return tobaccoRepository.findByTitleAndMaker(title, maker).orElse(null);
    }

    fun isExist(tobaccoId: Long): Boolean {
        return tobaccoRepository.existsByTobaccosId(tobaccoId)
    }

    fun getTobaccosInBar(maker: Maker, user: User): MutableSet<Tobacco> {
        return tobaccoRepository.getTobaccosInBar(maker.makersId, user.id)
    }

    fun save(tobacco: Tobacco) {
        tobaccoRepository.save(tobacco)
    }

    fun deleteTobaccoFromBar(user: User, id: Long): Unit {
        myTobaccoRepository.deleteTobaccoFromBar(user.id, id)
    }

    fun addTobaccoInBar(tobaccoId: Long,
                        user: User): Unit {

        var tobacco: Tobacco = tobaccoRepository.getOne(tobaccoId)


        if (myTobaccoRepository.existsByTobaccoIdAndUserId(user.id, tobaccoId)) {

            println("Relation is exist!")

            var myTobacco = myTobaccoRepository.findByTobaccoIdAndUserId(user.id, tobaccoId)
            myTobacco.status = "contain bar"

            tobaccoRepository.addInLatestPurchases(user.id, tobacco.tobaccosId)

            myTobaccoRepository.save(myTobacco)

        } else {

            var myTobacco = MyTobacco()

            myTobacco.tobacco = tobacco
            myTobacco.user = user

            myTobacco.status = "contain bar"

            user.myTobaccos.add(myTobacco)
            tobacco.myTobaccos.add(myTobacco)

            myTobaccoRepository.save(myTobacco)
        }
    }

    fun addTobaccoInPurchases(tobaccoId: Long,
                              user: User): Unit {

        var tobacco: Tobacco = tobaccoRepository.getOne(tobaccoId)

        var myTobacco = MyTobacco()

        myTobacco.tobacco = tobacco
        myTobacco.user = user;

        myTobacco.status = "purchase"

        userService.save(user)

        user.myTobaccos.add(myTobacco)
        tobacco.myTobaccos.add(myTobacco)

        myTobaccoRepository.save(myTobacco)
    }

    fun findLatestPurchases(user: User): MutableList<Tobacco> {

        var result = tobaccoRepository.findLatestPurchases(user.id)

        if (result.isNotEmpty()) {
            result.forEach { i -> i.mixesMaker = makerRepository.getOneByTobacco(i.title) }
        }

        return result;
    }

    fun getTobaccosFromPurchases(user: User): MutableList<Tobacco> {
        var result = tobaccoRepository.findAllPurchases(user.id)

        if (result.isNotEmpty()) {

            for (item in result) {
                item.status = TobaccoStatus.IN_PURCHASES
                item.mixesMaker = makerRepository.getOneByTobacco(item.title)
            }

        }
        return result
    }

    fun deleteTobaccoFromPurchases(user: User,
                                   tobaccoId: Long): Unit {
        myTobaccoRepository.deleteTobaccoFromPurchases(user.id, tobaccoId)
    }

    fun getTobaccoStatus(user: User,
                         tobaccoId: Long): String {
        return myTobaccoRepository.getStatusByTobaccoIdAndUserId(user.id, tobaccoId)
    }

    fun findAllPurchases(user: User): MutableList<Tobacco> {
        return tobaccoRepository.findAllPurchases(user.id)
    }

}