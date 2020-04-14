package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.dto.Ingredient
import com.codemark.hookahmix.domain.dto.IngredientType
import com.codemark.hookahmix.domain.dto.MixFilterInfoDto
import com.codemark.hookahmix.domain.dto.StrengthLevel
import com.codemark.hookahmix.service.MixService
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import com.codemark.hookahmix.util.MixComparator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api/mixes")
class MixController @Autowired constructor(
        private var userService: UserService,
        private var mixService: MixService,
        private var cookieAuthorizationUtil: CookieAuthorizationUtil) {


    @GetMapping("/generator")
    fun generateMixes(@RequestParam(required = false) search: String?, request: HttpServletRequest,
                      session: HttpSession): MutableList<Mix> {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println("User: $user");
        val mixesList: MutableList<Mix> = mixService.getAllForUserWithSearch(user, search);
        Collections.sort(mixesList, MixComparator());
        return mixesList;
    }

    @GetMapping("/search")
    fun search(text: String): MutableList<Mix> {
        val mixesList: MutableList<Mix> = mixService.search(text);
        return mixesList;
    }

    //TODO доработать метод на реальные данные
    @GetMapping("/filter")
    fun generateFilter(): MixFilterInfoDto {
        return MixFilterInfoDto(
                listOf(
                        Ingredient(IngredientType.ALL_IN_BAR, "Все есть"),
                        Ingredient(IngredientType.WITH_REPLACE, "С заменой"),
                        Ingredient(IngredientType.WITH_BAY, "Докупить")
                ), listOf(
                StrengthLevel.LIGHT,
                StrengthLevel.MEDIUM,
                StrengthLevel.STRONG
        ), listOf(
                Taste().apply {
                    id = 1
                    title = "Яблоко"
                },
                Taste().apply {
                    id = 2
                    title = "Цитрус"
                },
                Taste().apply {
                    id = 3
                    title = "Ваниль"
                },
                Taste().apply {
                    id = 4
                    title = "Арбуз"
                },
                Taste().apply {
                    id = 5
                    title = "Корица"
                }
        ));
    }

    @GetMapping("/count")
    fun countGeneratedMix(request: HttpServletRequest,
                          session: HttpSession,
                          @RequestParam("status") status: String?,
                          @RequestParam("strength") strength: String?,
                          @RequestParam("taste") taste: String?): Int {

        val user = userService.findUserByInstallationCookie(cookieAuthorizationUtil
                .getInstallationCookie(request, session));
        println("User: $user");
        return mixService.generateMixCount(user, status, strength, taste);
    }
}