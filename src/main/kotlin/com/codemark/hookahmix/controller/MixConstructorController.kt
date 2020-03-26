package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.domain.dto.Ingredient
import com.codemark.hookahmix.domain.dto.IngredientType
import com.codemark.hookahmix.domain.dto.MixFilterInfoDto
import com.codemark.hookahmix.domain.dto.StrengthLevel
import com.codemark.hookahmix.repository.*
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
import java.util.stream.Collectors.toList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import kotlin.streams.toList

@RestController
@RequestMapping("/api/mixes")

class MixConstructorController @Autowired constructor(
        private var userService: UserService,
        private var mixService: MixService,
        private var cookieAuthorizationUtil: CookieAuthorizationUtil) {


    @GetMapping("/generator")
    fun generateMixes(request: HttpServletRequest,
                      session: HttpSession): MutableList<Mix> {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        println("User: $user")

        var mixesList: MutableList<Mix> = mixService.showAllMixes(user)

        Collections.sort(mixesList, MixComparator())

        return mixesList;
    }

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
                    tastesId = 1
                    taste = "Яблоко"
                },
                Taste().apply {
                    tastesId = 2
                    taste = "Цитрус"
                },
                Taste().apply {
                    tastesId = 3
                    taste = "Ваниль"
                },
                Taste().apply {
                    tastesId = 4
                    taste = "Арбуз"
                },
                Taste().apply {
                    tastesId = 5
                    taste = "Корица"
                }
        )
        );
    }

    @GetMapping("/count")
    fun countGeneratedMix(request: HttpServletRequest,
                          session: HttpSession,
                          @RequestParam("status") status: String?,
                          @RequestParam("strength") strength: String?,
                          @RequestParam("taste") taste: String?): Int {

        var user = userService.findUserByInstallationCookie(
                cookieAuthorizationUtil.getInstallationCookie(request, session)
        )

        println("User: $user")

        return mixService.generateMixCount(user, status, strength, taste)
    }
}