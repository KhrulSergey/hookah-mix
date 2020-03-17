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
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

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
    fun findMarkersBy(request: HttpServletRequest,
                      response: HttpServletResponse,
                      session: HttpSession): List<Maker> {

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

        println(user)

        var catalogTobaccos = makerRepository.findAllSortedByTitle();
        return catalogTobaccos;
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun findMarkersBar(request: HttpServletRequest,
                       response: HttpServletResponse,
                       session: HttpSession): MutableList<Tobacco>? {

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

        println(user)

        return user.tobaccos;
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

        var existUser =
                userRepository.existsByInstallationCookie(installationCookie);
        println("/tobacco, user exist: $existUser");

        tobacco.status = TobaccoStatus.CONTAIN_BAR;

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
               request: HttpServletRequest,
               response: HttpServletResponse,
               session: HttpSession): ResponseEntity<String> {

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

        user = userRepository
                .findUserByInstallationCookie(installationCookie);


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