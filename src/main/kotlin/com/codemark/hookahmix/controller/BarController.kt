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
                      response: HttpServletResponse): List<Maker> {

        var installationCookie = "";
        if (request.cookies != null) {

            println("Step first: check if cookie exist...")
            var optionalCookie: Optional<Cookie> = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findFirst();
            println("Request Path: " + request.servletPath)
            println("First step completed")
            if (optionalCookie.isPresent) {
                println("Step second: check if present...")
                installationCookie = optionalCookie.get().value;
                println("Second step completed, cookie: " + installationCookie)
                if (installationCookie == null || installationCookie.isEmpty()) {
                    println("Step third: return to method...")
                    response.sendRedirect(request.servletPath)
                }
            } else {
                println("Step fourth: redirect if present is null")
                response.sendRedirect(request.servletPath)
            }
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var catalogTobaccos = makerRepository.findAllSortedByTitle();
        return catalogTobaccos;
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun findMarkersBar(request: HttpServletRequest,
                       response: HttpServletResponse): MutableList<Tobacco>? {

//        var installationCookie = "";
//        if (request.cookies != null) {
//
//            installationCookie =
//                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
//            println("Cookie in bar: $installationCookie");
//        } else {
//            throw InstallationCookieException("Cookie not found!");
//        }
        var installationCookie = "";
        if (request.cookies != null) {

            println("Step first: check if cookie exist...")
            var optionalCookie: Optional<Cookie> = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findFirst();
            println("Request Path: " + request.servletPath)
            println("First step completed")
            if (optionalCookie.isPresent) {
                println("Step second: check if present...")
                installationCookie = optionalCookie.get().value;
                println("Second step completed, cookie: " + installationCookie)
                if (installationCookie == null || installationCookie.isEmpty()) {
                    println("Step third: return to method...")
                    response.sendRedirect(request.servletPath)
                }
            } else {
                println("Step fourth: redirect if present is null")
                response.sendRedirect(request.servletPath)
            }
        } else {
            throw InstallationCookieException("Cookie not found");
        }


        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        } catch (e: KotlinNullPointerException) {
            user = null;
        }

        println(user)

        return user?.tobaccos;
    }

    /**
     * Метод добавления табака в бар
     */

    @PostMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long,
                   request: HttpServletRequest,
                   response: HttpServletResponse): ResponseEntity<String> {

        var tobacco: Tobacco = tobaccoRepository.getOne(id);


        var installationCookie = "";
        if (request.cookies != null) {

            var optionalCookie: Optional<Cookie> = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findFirst();
            if (optionalCookie.isPresent) {
                installationCookie = optionalCookie.get().value;
                if (installationCookie == null || installationCookie.isEmpty()) {
                    response.sendRedirect(request.servletPath)
                }
            } else {
                response.sendRedirect(request.servletPath)
            }
        } else {
            throw InstallationCookieException("Cookie not found");
        }

        var existUser =
                userRepository.existsByInstallationCookie(installationCookie);
        println("/tobacco, user exist: $existUser");

        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        }


        tobacco.status = TobaccoStatus.CONTAIN_BAR;
        println("Status: " + tobacco.status)
        if (user != null) {
            user.tobaccos.add(tobacco)
        };
        user?.let { userRepository.save(it) };

        println("Tobacco successfully added to user ${user?.installationCookie}!")

        return ResponseEntity("Tobacco $tobacco was added in bar", HttpStatus.OK);
    }

    /**
     * Метод удаления табака из бара
     */

    @DeleteMapping("/delete_tobacco/{id}")
    fun delete(@PathVariable("id") id: Long,
               request: HttpServletRequest,
               response: HttpServletResponse): ResponseEntity<String> {

//        var installationCookie = "";
//        if (request.cookies != null) {
//            installationCookie =
//                    cookieAuthorizationUtil.findCurrentCookie(request.cookies);
//            println("Method DELETE detected")
//        } else {
//            throw InstallationCookieException("Cookie not found");
//        }

        var installationCookie = "";
        if (request.cookies != null) {

            println("Step first: check if cookie exist...")
            var optionalCookie: Optional<Cookie> = Arrays.stream(request.cookies)
                    .filter { i -> i.name.equals("UserId") }
                    .findFirst();
            println("Request Path: " + request.servletPath)
            println("First step completed")
            if (optionalCookie.isPresent) {
                println("Step second: check if present...")
                installationCookie = optionalCookie.get().value;
                println("Second step completed, cookie: " + installationCookie)
                if (installationCookie == null || installationCookie.isEmpty()) {
                    println("Step third: return to method...")
                    response.sendRedirect(request.servletPath)
                }
            } else {
                println("Step fourth: redirect if present is null")
                response.sendRedirect(request.servletPath)
            }
        } else {
            throw InstallationCookieException("Cookie not found");
        }

//        var user: User = userRepository
//                .findUserByInstallationCookie(installationCookie);

        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        }

        var iterator = user!!.tobaccos.iterator();
        while (iterator.hasNext()) {
            var tobacco = iterator.next();
            if (tobacco.tobaccosId == id) {
                iterator.remove();
                user.let { userRepository.save(it) };
                println("Tobacco ${tobacco.title} was successfully removed!");
                println(user.tobaccos)
            }
        }

        println("Tobacco was successfully removed!")
        return ResponseEntity("Tobacco with ID $id was deleted from bar", HttpStatus.OK)
    }
}