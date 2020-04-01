package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.MyTobacco
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.service.TobaccoService
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api/purchases")
class ShoppingListController @Autowired constructor(
        private var tobaccoService: TobaccoService,
        private var userService: UserService,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    @GetMapping("/my")
    fun getAllPurchases(request: HttpServletRequest,
                        response: HttpServletResponse,
                        session: HttpSession): List<Tobacco> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )
        println("User: $user");

        return tobaccoService.getTobaccosFromPurchases(user)
    }

    @PostMapping("/my/{id}")
    fun addTobaccoInPurchases(@PathVariable("id") id: Long,
                              request: HttpServletRequest,
                              response: HttpServletResponse,
                              session: HttpSession): ResponseEntity<String> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        var tobacco: Tobacco = tobaccoService.getOne(id)
        tobaccoService.addTobaccoInPurchases(id, user)

        return ResponseEntity("Tobacco $tobacco was added in purchases", HttpStatus.OK)
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest,
                           response: HttpServletResponse,
                           session: HttpSession): MutableList<Tobacco> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        return tobaccoService.findLatestPurchases(user)
    }

    @DeleteMapping("/my/{id}")
    fun deleteTobaccoFromPurchases(@PathVariable("id") id: Long,
                                   request: HttpServletRequest,
                                   response: HttpServletResponse,
                                   session: HttpSession): ResponseEntity<String> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        var tobacco = tobaccoService.getOne(id)

        tobaccoService.deleteTobaccoFromPurchases(user, tobacco.tobaccosId)

        return ResponseEntity("Tobacco $tobacco was removed from purchase", HttpStatus.OK)
    }
}