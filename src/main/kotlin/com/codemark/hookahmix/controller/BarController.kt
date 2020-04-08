package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.repository.MyTobaccoRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.service.MakerService
import com.codemark.hookahmix.service.TobaccoService
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                           private val myTobaccoRepository: MyTobaccoRepository,
                                           private val userService: UserService,
                                           private val makerService: MakerService,
                                           private val tobaccoService: TobaccoService,
                                           var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */

    @GetMapping("/marker/catalog")
    fun getMakerCatalog(request: HttpServletRequest,
                        response: HttpServletResponse,
                        session: HttpSession): List<Maker> {

        val user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        println(user)

        val catalogTobaccos = makerService.getTobaccosInCatalog(user)

        return catalogTobaccos
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun getUserBar(request: HttpServletRequest,
                   response: HttpServletResponse,
                   session: HttpSession): MutableSet<Maker> {


        val user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        println(user)
        println(user.tobaccos)
        return makerService.getTobaccosInBar(user)
    }


    /**
     * Метод добавления табака в бар
     */

    @PostMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long,
                   request: HttpServletRequest,
                   response: HttpServletResponse,
                   session: HttpSession): ResponseEntity<String> {

        val tobacco: Tobacco = tobaccoRepository.getOne(id);

        val user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        tobaccoService.addTobaccoInBar(id, user)

        println("Tobacco successfully added to user ${user.installationCookie}!")

        return ResponseEntity("Tobacco $tobacco was added in bar", HttpStatus.OK);
    }

    /**
     * Метод удаления табака из бара
     */

    @GetMapping("/delete_tobacco/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest,
               response: HttpServletResponse,
               session: HttpSession): ResponseEntity<String> {

        val user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        tobaccoService.deleteTobaccoFromBar(user, id);

        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK)
    }

}