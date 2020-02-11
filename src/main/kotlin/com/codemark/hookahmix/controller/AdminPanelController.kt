package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Tobacco
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import kotlin.streams.toList

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

    @GetMapping("/tobaccos_list")
    fun getAllTobaccos(@RequestParam(defaultValue = "", required = false) filter : String,
                       model : Model) : String {

        var tobaccos : List<Tobacco> = mutableListOf(
                Tobacco(1, "Tobacco", "Lotar"),
                Tobacco(2, "Another Tobacco", "Lars"),
                Tobacco(3,"Hot Tobacco", "Abuin"));

        if (filter != null && filter.isNotEmpty()) {

            var sortedTobaccos: List<Tobacco>;
            sortedTobaccos = tobaccos.stream()
                    .filter{item -> item.maker.equals(filter)}
                    .toList();

            model.addAttribute("tobaccos", sortedTobaccos);
            model.addAttribute("filter", filter);

            return "/tobaccos_list";
        }


        model.addAttribute("tobaccos", tobaccos);
        model.addAttribute("filter", filter);
        return "/tobaccos_list";
    }

    @GetMapping("/mixes_list")
    fun getAllMixes(model : Model) : String {

        var mixes : List<Mix> = mutableListOf(Mix(), Mix(), Mix());
        model.addAttribute("mixes", mixes);

        return "/mixes_list";
    }

}