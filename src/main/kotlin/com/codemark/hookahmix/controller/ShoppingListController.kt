package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.service.TobaccoService
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println("User: $user");
        return tobaccoService.getAllUserTobaccoInPurchases(user);
    }

    @PostMapping("/my/{id}")
    fun addTobaccoInPurchases(@PathVariable("id") id: Long,
                              request: HttpServletRequest,
                              response: HttpServletResponse,
                              session: HttpSession): ResponseEntity<String> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        val tobacco: Tobacco? = tobaccoService.addOneInPurchases(id, user);
        val status = if (tobacco != null) HttpStatus.OK else HttpStatus.NOT_FOUND;
        return ResponseEntity("Tobacco $tobacco was added in purchases: ${tobacco != null}", status);
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest,
                           response: HttpServletResponse,
                           session: HttpSession): MutableSet<Tobacco> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        return tobaccoService.getAllFromLatestPurchases(user);
    }

    @GetMapping("/delete_latest/{id}")
    fun deleteTobaccoFromPurchases(@PathVariable("id") id: Long,
                                   request: HttpServletRequest,
                                   response: HttpServletResponse,
                                   session: HttpSession): ResponseEntity<String> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        val tobacco = tobaccoService.getOne(id);
        tobaccoService.deleteOneFromLatestPurchases(user, tobacco.id);
        return ResponseEntity("Tobacco $tobacco was removed from purchase", HttpStatus.OK);
    }
}