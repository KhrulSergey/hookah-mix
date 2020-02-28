package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.dto.MixDto
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TasteRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping(value = ["/", "api/admin"])
class AdminPanelController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                                  private val makerRepository: MakerRepository,
                                                  private val tasteRepository: TasteRepository) {


    @GetMapping()
    fun login() : String {
        return "redirect:/main";
    }


    @GetMapping("/main")
    fun main(model : Model) : String {

        var makers : List<Maker> = makerRepository.findAll();
        var tastes : List<Taste> = tasteRepository.findAll();

        model.addAttribute("makers", makers);
        model.addAttribute("tastes", tastes);

        return "main";
    }

    @PostMapping("/main")
    fun addTobacco(@RequestParam("title") title : String,
                   @RequestParam("makers") maker : String,
                   @RequestParam("description") description : String,
                   @RequestParam("tastes", required = false) taste : String,
                   @RequestParam("strength") strength : Int,
                   @RequestParam("image") image : String,
                   @RequestParam("tags") tags : String,
                   model : Model)
            : String {

        var findMaker : Maker = makerRepository.findByTitle(maker);
        var findTaste : Taste = tasteRepository.findByTaste(taste);

        var newTobacco = Tobacco(title, description, strength, image, tags);
        newTobacco.maker = findMaker;
        newTobacco.taste = findTaste;

        tobaccoRepository.save(newTobacco);

        return "redirect:/main";
    }


    @PostMapping("/add_mix")
    fun addMix(@RequestParam mix : String)
            : String {
        return "redirect:/main";
    }


    @GetMapping("/catalog_tobaccos")
    fun getAllTobaccos(@RequestParam(name = "filter", defaultValue = "", required = false) filter : String,
                       model : Model) : String {

        var tobaccos : List<Tobacco>;
        if (filter != null && filter.isNotEmpty()) {

            tobaccos = tobaccoRepository.findAllByMaker(filter);

            model.addAttribute("tobaccos", tobaccos);
            model.addAttribute("filter", filter);

            return "/catalog_tobaccos";
        }

        tobaccos = tobaccoRepository.findAll();

        model.addAttribute("tobaccos", tobaccos);
        model.addAttribute("filter", filter);

        return "/catalog_tobaccos";
    }



    @GetMapping("/catalog_mixes")
    fun getAllMixes(model : Model) : String {

        var mixes : List<MixDto> = mutableListOf(MixDto(), MixDto(), MixDto());
        model.addAttribute("mixes", mixes);

        return "/catalog_mixes";
    }

}