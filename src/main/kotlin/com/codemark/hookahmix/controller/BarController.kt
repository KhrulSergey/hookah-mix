package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList


@RestController
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                           private val makerRepository: MakerRepository,
                                           private var userRepository: UserRepository,
                                           var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    private val mockDataBuilder: () -> Unit = {

    }
    private val mockData = listOf(
            Maker().let {
                it.makersId = 1
                it.title = "Al Fakhamah1"
                val maker = it
                it.tobaccos = setOf<Tobacco>(
                        Tobacco("Blackcurant1", "").let { it.tobaccosId = 1; it.maker = maker; it },
                        Tobacco("Blackcurant2", "").let { it.tobaccosId = 2; it.maker = maker; it },
                        Tobacco("Blackcurant3", "").let { it.tobaccosId = 3; it.maker = maker; it }
                ) as MutableSet<Tobacco>
                it
            },
            Maker().let {
                it.makersId = 2
                it.title = "Al Fakhamah2"
                val maker = it
                it.tobaccos = setOf<Tobacco>(
                        Tobacco("Blackcurant4", "").let { it.tobaccosId = 4; it.maker = maker; it },
                        Tobacco("Blackcurant5", "").let { it.tobaccosId = 5; it.maker = maker; it },
                        Tobacco("Blackcurant6", "").let { it.tobaccosId = 6; it.maker = maker; it }
                ) as MutableSet<Tobacco>
                it
            }
    )

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */

    @GetMapping("/marker/catalog")
    fun findMarkersBy(request: HttpServletRequest): List<Maker> {

        if (request.cookies != null) {
            var existCookie: Boolean = Arrays.stream(request.cookies)
                    .anyMatch { i -> i.name.equals("UserId") };

            if (!existCookie) {
                throw Exception("Cookie not found");
            }
        }

        var catalogTobaccos = makerRepository.findAllSortedByTitle();
        return catalogTobaccos;
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun findMarkersBar(request: HttpServletRequest): MutableList<Tobacco> {

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
            println("Cookie in bar: $installationCookie");
        } else {
            throw InstallationCookieException("Cookie not found!");
        }

        var existUser: Boolean =
                userRepository.existsByInstallationCookie(installationCookie);
        println("Bar, existUser: $existUser");

        var user: User = userRepository
                .findUserByInstallationCookie(installationCookie);
        println(user)

        println(user.tobaccos)
        return user.tobaccos;
    }

    /**
     * Метод получения списка табаков в для экрана Покупки
     */

    @GetMapping("/tobacco/shopping", produces = ["application/json"])
    fun findTobaccoBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<PageImpl<Tobacco>> = ok(
            PageImpl(mockData.flatMap { maker -> ArrayList(maker.tobaccos) })
    )
    /**
     * Метод добавления табака в бар
     */

//     POST, of course
    @GetMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long,
                   request: HttpServletRequest) {

        var tobacco: Tobacco = tobaccoRepository.getOne(id);

        var installationCookie = "";
        if (request.cookies != null) {

            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);

            println("Cookie in /tobacco: $installationCookie")
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var existUser =
                userRepository.existsByInstallationCookie(installationCookie);
        println("/tobacco, user exist: $existUser");

        var user: User = userRepository
                .findUserByInstallationCookie(installationCookie);

        user.tobaccos.add(tobacco);
        userRepository.save(user);

        println("Tobacco successfully added to user ${user.installationCookie}!")
    }

    /**
     * Метод удаления табака из бара
     */

    // DELETE, of course

    @GetMapping("/delete_tobacco/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest) {

        var installationCookie = "";
        if (request.cookies != null) {
            installationCookie =
                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
            println("Method DELETE detected")
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var user: User = userRepository
                .findUserByInstallationCookie(installationCookie);

//        if (installationCookie != null && installationCookie.isNotEmpty()) {
//            var existUser = userRepository.existsByInstallationCookie(installationCookie);
//            if (existUser) {
//                user = userRepository.findUserByInstallationCookie(installationCookie);
//            }
//        }

        var iterator = user.tobaccos.iterator();
        while (iterator.hasNext()) {
            var tobacco = iterator.next();
            if (tobacco.tobaccosId == id) {
                iterator.remove();
                userRepository.save(user);
                println("Tobacco ${tobacco.title} was successfully removed!");
                println(user.tobaccos)
            }
        }

        println("Tobacco was successfully removed!")
    }
}