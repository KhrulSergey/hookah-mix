package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
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
@RequestMapping("/api/tobacco")
class TobaccoController @Autowired constructor(private val userService: UserService,
                                               private val tobaccoService: TobaccoService,
                                               var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    //TODO убрать параметр full после отладки получения списка табаков до 01.06.20
    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */
    @GetMapping("/catalog")
    fun getMakerCatalog(@RequestParam(required = false) search: String?, @RequestParam(required = false) full: Boolean?,
                        request: HttpServletRequest, response: HttpServletResponse, session: HttpSession): MutableSet<Maker> {

        val user = userService.getOneByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println(user);
        if (full != null && full) tobaccoService.getMakersAndStatusTobaccosInCatalogForUser(user, search);
        return tobaccoService.getMakersAndStatusTobaccosInCatalogWithLimitRatingForUser(user, search);
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */
    @GetMapping("/bar")
    fun getUserBar(@RequestParam(required = false) search: String?, request: HttpServletRequest,
                   response: HttpServletResponse, session: HttpSession): MutableSet<Maker> {

        val user = userService.getOneByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println(user);
        return tobaccoService.getMakersAndStatusTobaccosInBarForUser(user, search);
    }

    /**
     * Метод добавления табака в бар
     */
    @PostMapping("/{id}")
    fun addTobacco(@PathVariable("id") tobaccoId: Long,
                   request: HttpServletRequest,
                   response: HttpServletResponse,
                   session: HttpSession): ResponseEntity<String> {

        val user = userService.getOneByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        val tobacco = tobaccoService.addOneInBar(tobaccoId, user);
        return ResponseEntity("Tobacco $tobacco was added in bar: ${tobacco != null}", HttpStatus.OK);
    }

    /**
     * Метод удаления табака из Моих табаков (из бара и корзины)
     */
    @GetMapping("/delete/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest,
               response: HttpServletResponse,
               session: HttpSession): ResponseEntity<String> {

        val user = userService.getOneByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        tobaccoService.deleteOneFromUserTobaccos(user, id);
        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK);
    }

    @GetMapping("/searchCatalog")
    fun searchCatalog(text: String): MutableList<Maker> {
        val catalogTobaccoList: MutableList<Maker> = tobaccoService.searchMakerCatalog(text);
        return catalogTobaccoList;
    }

    @GetMapping("/searchBar")
    fun searchBar(text: String): MutableList<Tobacco> {
        val barTobaccoList: MutableList<Tobacco> = tobaccoService.searchBarTobacco(text, userService.getOne(19999));
        return barTobaccoList;
    }
}