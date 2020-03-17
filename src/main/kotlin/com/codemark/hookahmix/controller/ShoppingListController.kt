package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.MyTobacco
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.exception.InstallationCookieException
import com.codemark.hookahmix.repository.MyTobaccoRepository
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
@RequestMapping("/api/purchases")
class ShoppingListController @Autowired constructor(
        private var userRepository: UserRepository,
        private var tobaccoRepository: TobaccoRepository,
        private var myTobaccoRepository: MyTobaccoRepository,
        var cookieAuthorizationUtil: CookieAuthorizationUtil) {

    @GetMapping("/my")
    fun getAllPurchases(request: HttpServletRequest,
                        response: HttpServletResponse): List<Tobacco> {


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

        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        }

        println("User: $user");

        var purchases: MutableList<Tobacco> = mutableListOf();
        if (user != null) {
            purchases = tobaccoRepository.findAllPurchases(user.id)
        }
        return purchases;
    }

    @PostMapping("/my/{id}")
    fun addTobaccoInPurchases(@PathVariable("id") id: Long,
                              request: HttpServletRequest,
                              response: HttpServletResponse): ResponseEntity<String> {

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

        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        }

        var tobacco: Tobacco = tobaccoRepository.getOne(id);
        println(tobacco)

        println(user)


        var myTobacco: MyTobacco = MyTobacco();
        myTobacco.tobacco = tobacco;
        myTobacco.user = user;
        myTobacco.status = "purchase"

        if (user != null) {
            user.latestPurchases.add(tobacco)
        };
        user?.let { userRepository.save(it) };

        if (user != null) {
            user.myTobaccos.add(myTobacco)
        };
        tobacco.myTobaccos.add(myTobacco);

        myTobaccoRepository.save(myTobacco);

        return ResponseEntity("Tobacco $tobacco was added in purchases", HttpStatus.OK)
    }

    @GetMapping("/latest")
    fun getLatestPurchases(request: HttpServletRequest,
                           response: HttpServletResponse): MutableList<Tobacco>? {


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


        var user: User?;
        try {
            user =
                    userRepository.findUserByInstallationCookie(installationCookie);
        } catch (e: EmptyResultDataAccessException) {
            user = null;
        }

        var latestPurchases: MutableList<Tobacco> = mutableListOf();
        if (user != null) {
            latestPurchases = tobaccoRepository.findLatestPurchases(user.id)
        }
        return latestPurchases;
    }
}