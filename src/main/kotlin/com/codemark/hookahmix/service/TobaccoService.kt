package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TobaccoService @Autowired constructor(
        private val tobaccoRepository: TobaccoRepository,
        private val myTobaccoRepository: MyTobaccoRepository,
        private val userService: UserService) {


    fun save(tobacco: Tobacco) {
        tobaccoRepository.save(tobacco)
    }

    fun isExist(tobaccoId: Long): Boolean {
        return tobaccoRepository.existsByTobaccosId(tobaccoId)
    }

    fun getOne(id: Long): Tobacco {
        return tobaccoRepository.getOne(id)
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

    fun addTobaccoInBar(tobaccoId: Long,
                        user: User): Unit {

        var tobacco: Tobacco = tobaccoRepository.getOne(tobaccoId)


        if (myTobaccoRepository.existsByTobaccoIdAndUserId(user.id, tobaccoId)) {

            println("Relation is exist!")
//            println(tobacco.title)

            var myTobacco = myTobaccoRepository.findByTobaccoIdAndUserId(user.id, tobaccoId)
            myTobacco.status = "contain bar"
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

//        var myTobacco = MyTobacco()
//
//        myTobacco.tobacco = tobacco
//        myTobacco.user = user
//
//        myTobacco.status = "contain bar"

//        user.tobaccos.add(tobacco)
//        userService.save(user)

//        user.myTobaccos.add(myTobacco)
//        tobacco.myTobaccos.add(myTobacco)
//
//        myTobaccoRepository.save(myTobacco)
    }

    fun addTobaccoInPurchases(tobaccoId: Long,
                              user: User): Unit {

        var tobacco: Tobacco = tobaccoRepository.getOne(tobaccoId)

        var myTobacco = MyTobacco()

        myTobacco.tobacco = tobacco
        myTobacco.user = user;

        myTobacco.status = "purchase"

        user.latestPurchases.add(tobacco)

        userService.save(user)

        user.myTobaccos.add(myTobacco)
        tobacco.myTobaccos.add(myTobacco)

        myTobaccoRepository.save(myTobacco)
    }

    fun findLatestPurchases(user: User): MutableList<Tobacco> {
        return tobaccoRepository.findLatestPurchases(user.id)
    }

    fun getTobaccosFromPurchases(user: User): MutableList<Tobacco> {
        var result = tobaccoRepository.findAllPurchases(user.id)
        if (result.isNotEmpty()) {
            result.forEach { i -> i.status = TobaccoStatus.IN_PURCHASES }
        }
        return result
    }

}