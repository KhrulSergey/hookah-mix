package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.service.MakerService
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
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                           private val makerRepository: MakerRepository,
                                           private val userService: UserService,
                                           private val makerService: MakerService,
                                           private val tobaccoService: TobaccoService,
                                           var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */

    @GetMapping("/marker/catalog")
    fun findMarkersBy(request: HttpServletRequest,
                      response: HttpServletResponse,
                      session: HttpSession): List<Maker> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        println(user)

//        var catalogTobaccos = makerRepository.findAllSortedByTitle()

        var catalogTobaccos = makerService.getTobaccosInCatalog(user)

        return catalogTobaccos
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun findMarkersBar(request: HttpServletRequest,
                       response: HttpServletResponse,
                       session: HttpSession): MutableSet<Maker> {


        var user = userService.findUserByInstallationCookie(
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

        var tobacco: Tobacco = tobaccoRepository.getOne(id);

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

//        tobacco.status = TobaccoStatus.CONTAIN_BAR;

//        user.tobaccos.add(tobacco);
//        userService.save(user);

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

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        tobaccoService.deleteTobaccoFromBar(user, id);

        println("Tobacco was successfully removed!")
        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK)
    }
}