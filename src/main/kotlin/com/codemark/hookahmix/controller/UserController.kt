package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.FileRepository
import com.codemark.hookahmix.service.UserService
import com.codemark.hookahmix.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
        private val userService: UserService,
        private val fileRepository: FileRepository,
        private val imageUtil: ImageUtil) {


    @GetMapping("/registration")
    fun register(session: HttpSession): ResponseEntity<Map<String, String>> {

        var installationCookie = UUID.randomUUID().toString();
        session.setAttribute("installationCookie", installationCookie)

        userService.save(User(installationCookie))

//        var image = Image()
//        image.image = imageUtil.save("https://alghazalhookah.co.za/wp-content/uploads/2019/08/Bubba0151.jpg")
//        fileRepository.save(image)

        var jsonObject = HashMap<String, String>()
        jsonObject.put("userId", installationCookie)

        return ResponseEntity(jsonObject, HttpStatus.OK)
    }



}