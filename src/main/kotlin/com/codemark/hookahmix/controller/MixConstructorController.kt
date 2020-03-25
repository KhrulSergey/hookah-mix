package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.domain.dto.Ingredient
import com.codemark.hookahmix.domain.dto.IngredientType
import com.codemark.hookahmix.domain.dto.MixFilterInfoDto
import com.codemark.hookahmix.domain.dto.StrengthLevel
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.*
import com.codemark.hookahmix.service.MixService
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import com.codemark.hookahmix.util.MixComparator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/api/mixes")

class MixConstructorController @Autowired constructor(
        private var userRepository: UserRepository,
        private var userService: UserService,
        private var mixService: MixService,
        private var mixRepository: MixRepository,
        private var tobaccoRepository: TobaccoRepository,
        private var componentRepository: ComponentRepository,
        private var makerRepository: MakerRepository,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {


    @GetMapping("/generator")
    fun generateMixes(request: HttpServletRequest,
                      session: HttpSession,
                      status: String,
                      strength: String,
                      taste: String): MutableList<Mix> {

        var installationCookie = "";
        var user: User;

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        user = userService.findUserByInstallationCookie(installationCookie)

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
                    taste = "Яблако"
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
                    tastesId = 4
                    taste = "Корица"
                }
        )
        );
    }


    @GetMapping("/count")
    fun countGeneratedMix(request: HttpServletRequest,
                          session: HttpSession,
                          status: String,
                          strength: String,
                          taste: String): Int {

        var installationCookie = "";
        var user: User;

        if (request.getHeader("X-UserId") != null) {
            installationCookie = request.getHeader("X-UserId");
        } else {
            installationCookie = session.getAttribute("installationCookie").toString()
        }

        user = userService.findUserByInstallationCookie(installationCookie)

        var mix = mixService.showAllMixes(user)

        return 15;
    }
}