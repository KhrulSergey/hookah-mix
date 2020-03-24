package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.MyTobacco
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
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
        private var userRepository: UserRepository,
        private var tobaccoRepository: TobaccoRepository,
        private var myTobaccoRepository: MyTobaccoRepository,
        private var userService: UserService,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    @GetMapping("/my")
    fun getAllPurchases(request: HttpServletRequest,
                        response: HttpServletResponse,
                        session: HttpSession): List<Tobacco> {


        var installationCookie = "";
        var user: User;

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        user = userService.findUserByInstallationCookie(installationCookie)

        println("User: $user");

        return tobaccoRepository.findAllPurchases(user.id)
    }

    @PostMapping("/my/{id}")
    fun addTobaccoInPurchases(@PathVariable("id") id: Long,
                              request: HttpServletRequest,
                              response: HttpServletResponse,
                              session: HttpSession): ResponseEntity<String> {

        var installationCookie = "";
        var user: User;

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        user = userService.findUserByInstallationCookie(installationCookie)

        var tobacco: Tobacco = tobaccoRepository.getOne(id);

        println(tobacco)
        println(user)


        var myTobacco: MyTobacco = MyTobacco();
        myTobacco.tobacco = tobacco;
        myTobacco.user = user;
        myTobacco.status = "purchase"

        user.latestPurchases.add(tobacco);

        userRepository.save(user);

        user.myTobaccos.add(myTobacco);
        tobacco.myTobaccos.add(myTobacco);

        myTobaccoRepository.save(myTobacco);

        return ResponseEntity("Tobacco $tobacco was added in purchases", HttpStatus.OK)
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest,
                           response: HttpServletResponse,
                           session: HttpSession): MutableList<Tobacco>? {


        var installationCookie = "";
        var user: User;

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        user = userService.findUserByInstallationCookie(installationCookie)

        return tobaccoRepository.findLatestPurchases(user.id)
    }
}