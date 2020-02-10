package com.codemark.hookahmix.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping(value = ["/", "api/admin"])
class AdminPanelController {

    @GetMapping()
    fun login() : String {
        return "redirect:/main.html";
    }

    @PostMapping("/add_tobacco")
    fun addTobacco(@RequestParam tobacco : String)
            : String {
        return "redirect:/main.html";
    }


    @PostMapping("/add_mix")
    fun addMix(@RequestParam mix : String)
            : String {
        return "redirect:/main.html";
    }

}