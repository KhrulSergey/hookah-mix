package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
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


        return emptyList();
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


        println("End")
        return ResponseEntity("Tobacco $tobacco was added in purchases", HttpStatus.OK)
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest): Queue<Tobacco>? {

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var user: User =
                userRepository.findUserByInstallationCookie(installationCookie);

        return user.latestPurchases;
    }
}