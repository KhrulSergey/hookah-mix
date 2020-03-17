package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.MyTobacco
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/purchases")
class ShoppingListController @Autowired constructor(
        private var userRepository: UserRepository,
        private var tobaccoRepository: TobaccoRepository,
        private var myTobaccoRepository: MyTobaccoRepository,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    @GetMapping("/my")
    fun getAllPurchases(request: HttpServletRequest): List<Tobacco> {

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var user: User =
                userRepository.findUserByInstallationCookie(installationCookie);

        println("User: $user");


//        return tobaccoRepository.findAllPurchases(user.id);
        return tobaccoRepository.findAllPurchases(user.id);
    }

    @PostMapping("/my/{id}")
    fun addTobaccoInPurchases(@PathVariable("id") id: Long,
                              request: HttpServletRequest): ResponseEntity<String> {

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var tobacco: Tobacco = tobaccoRepository.getOne(id);
        println(tobacco)

        var user: User =
                userRepository.findUserByInstallationCookie(installationCookie);
        println(user)

        println("UserId: ${user.id}")
        println("TobaccoId: ${tobacco.tobaccosId}")
        println("ParamId: $id")

//        var myTobacco: MyTobacco = MyTobacco(
//                userId = user.id, tobaccoId = tobacco.tobaccosId);

        var myTobacco: MyTobacco = MyTobacco();
        myTobacco.tobacco = tobacco;
        myTobacco.user = user;
        myTobacco.status = "purchase"

        user.latestPurchases.add(tobacco);
        userRepository.save(user);

        user.myTobaccos.add(myTobacco);
        tobacco.myTobaccos.add(myTobacco);

        myTobaccoRepository.save(myTobacco);

        println("End")
        return ResponseEntity("Tobacco $tobacco was added in purchases", HttpStatus.OK)
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest): MutableList<Tobacco>? {

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var user: User =
                userRepository.findUserByInstallationCookie(installationCookie);

        return tobaccoRepository.findLatestPurchases(user.id);
    }
}