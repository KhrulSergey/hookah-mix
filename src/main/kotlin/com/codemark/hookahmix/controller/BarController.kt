package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
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
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val userService: UserService,
                                           private val tobaccoService: TobaccoService,
                                           var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */
    @GetMapping("/marker/catalog")
    fun getMakerCatalog(request: HttpServletRequest,
                        response: HttpServletResponse,
                        session: HttpSession): List<Maker> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println(user);
        return tobaccoService.getMakersAndStatusTobaccosInCatalogForUser(user);
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */
    @GetMapping("/marker/bar")
    fun getUserBar(request: HttpServletRequest,
                   response: HttpServletResponse,
                   session: HttpSession): MutableSet<Maker> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println(user);
        return tobaccoService.getMakersAndStatusTobaccosInBarForUser(user);
    }


    /**
     * Метод добавления табака в бар
     */
    @PostMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") tobaccoId: Long,
                   request: HttpServletRequest,
                   response: HttpServletResponse,
                   session: HttpSession): ResponseEntity<String> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        val tobacco = tobaccoService.addOneInBar(tobaccoId, user);
        return ResponseEntity("Tobacco $tobacco was added in bar: ${tobacco != null}", HttpStatus.OK);
    }

    /**
     * Метод удаления табака из бара
     */
    @GetMapping("/delete_tobacco/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest,
               response: HttpServletResponse,
               session: HttpSession): ResponseEntity<String> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        tobaccoService.deleteOneFromUserTobaccos(user, id);
        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK);
    }
}