package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserRepository
import com.codemark.hookahmix.util.CookieAuthorizationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                           private val makerRepository: MakerRepository,
                                           private var userRepository: UserRepository,
                                           var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */

    @GetMapping("/marker/catalog")
    fun findMarkersBy(request: HttpServletRequest): List<Maker> {

        if (request.cookies != null) {
            var existCookie: Boolean = Arrays.stream(request.cookies)
                    .anyMatch { i -> i.name.equals("UserId") };

            if (!existCookie) {
                throw InstallationCookieException("Cookie not found");
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
     * Метод добавления табака в бар
     */

    @PostMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long,
                   request: HttpServletRequest): ResponseEntity<String> {

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

        tobacco.status = TobaccoStatus.CONTAIN_BAR;
        println("Status: " + tobacco.status)
        user.tobaccos.add(tobacco);
        userRepository.save(user);

        println("Tobacco successfully added to user ${user.installationCookie}!")

        return ResponseEntity("Tobacco $tobacco was added in bar", HttpStatus.OK);
    }

    /**
     * Метод удаления табака из бара
     */

    @DeleteMapping("/delete_tobacco/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest): ResponseEntity<String> {

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
        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK)
    }
}