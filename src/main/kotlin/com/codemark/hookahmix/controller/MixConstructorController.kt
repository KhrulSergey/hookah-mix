package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.domain.dto.Ingredient
import com.codemark.hookahmix.domain.dto.IngredientType
import com.codemark.hookahmix.domain.dto.MixFilterInfoDto
import com.codemark.hookahmix.domain.dto.StrengthLevel
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.*
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/api/mixes")

class MixConstructorController @Autowired constructor(
        private var userRepository: UserRepository,
        private var mixRepository: MixRepository,
        private var componentRepository: ComponentRepository,
        private var makerRepository: MakerRepository,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {


    @GetMapping("/generator")
    fun generateMixes(request: HttpServletRequest,
                      response: HttpServletResponse,
                      session: HttpSession): MutableList<Mix> {


        var installationCookie = "";
        var user: User;

        if (request.cookies == null || !request.cookies.any { it.value.equals("UserId") }) {
            println("Fuck, NPE will be here!")
            installationCookie = session.getAttribute("installationCookie").toString();
            user = userRepository.findUserByInstallationCookie(installationCookie);
        } else {
            installationCookie = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findAny()
                    .get().value
            user = userRepository.findUserByInstallationCookie(installationCookie);
        }


        user = userRepository.findUserByInstallationCookie(installationCookie);

        println("User: $user")
        var mixesList: MutableList<Mix> = mixRepository.findAll();

        for (mix in mixesList) {

            if (user.tobaccos.containsAll(mix.tobaccoMixList)) {

                mix.status = MixSet.MATCH_BAR;

                for (tobacco in mix.tobaccoMixList) {
                    tobacco.status = TobaccoStatus.CONTAIN_BAR;
                }

            } else {

                var isTobaccoInBar: Boolean = mix.tobaccoMixList.stream()
                        .anyMatch { i -> user.tobaccos.stream()
                                .anyMatch { f -> StringUtils.pathEquals(
                                        i.title, f.title) } };

                if (isTobaccoInBar) {

                    var existReplacements: Boolean = false;
                    for (mixTobacco in mix.tobaccoMixList) {
                        mixTobacco.replacements = ArrayList();
                        mixTobacco.status = TobaccoStatus.PURCHASES;
                        for (userTobacco in user.tobaccos) {

                            if (mixTobacco.tobaccosId.equals(userTobacco.tobaccosId)) {
                                mixTobacco.status = TobaccoStatus.CONTAIN_BAR;

                            } else {
                                if (mixTobacco.taste?.taste.equals(userTobacco.taste?.taste)) {
                                    existReplacements = true;
                                    userTobacco.status = TobaccoStatus.CONTAIN_BAR;
                                    mixTobacco.replacements.add(userTobacco);

                                } else {
                                    if (mixTobacco.status == null ||
                                            !mixTobacco.status.equals(TobaccoStatus.CONTAIN_BAR)) {
                                        mixTobacco.status = TobaccoStatus.PURCHASES;
                                    }
                                }
                            }
                        }
                    }

                    if (existReplacements) {
                        mix.status = MixSet.REPLACEMENT_BAR
                    } else {
                        mix.status = MixSet.PARTIAL_BAR
                    }

                } else {

                    var existReplacements: Boolean = false;
                    for (mixTobacco in mix.tobaccoMixList) {

                        mixTobacco.replacements = ArrayList();
                        mixTobacco.status = TobaccoStatus.PURCHASES;
                        for (userTobacco in user.tobaccos) {

                            if (mixTobacco.taste?.taste.equals(userTobacco.taste?.taste)) {
                                existReplacements = true;
                                mixTobacco.replacements.add(userTobacco);

                            } else {
                                if (mixTobacco.status == null ||
                                        !mixTobacco.status.equals(TobaccoStatus.CONTAIN_BAR)) {
                                    mixTobacco.status = TobaccoStatus.PURCHASES;

                                }
                            }
                        }
                    }

                    if (existReplacements) {
                        mix.status = MixSet.REPLACEMENT_BAR
                    } else {
                        mix.status = MixSet.PARTIAL_BAR
                    }
                }
            }
        }
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
                          response: HttpServletResponse,
                          session: HttpSession): Int {

        var installationCookie = "";
        var user: User;

        if (request.cookies == null || !request.cookies.any { it.value.equals("UserId") }) {
            println("Fuck, NPE will be here!")
            installationCookie = session.getAttribute("installationCookie").toString();
            user = userRepository.findUserByInstallationCookie(installationCookie);
        } else {
            installationCookie = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findAny()
                    .get().value
            user = userRepository.findUserByInstallationCookie(installationCookie);
        }

        user = userRepository.findUserByInstallationCookie(installationCookie)


        return 15;
    }
}