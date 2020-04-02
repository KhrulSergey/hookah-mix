package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.MixRepository
import com.codemark.hookahmix.repository.TasteRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.service.AdminPanelService
import com.codemark.hookahmix.util.MixParser
import com.codemark.hookahmix.util.TobaccoParser
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
                                                  private val mixRepository: MixRepository,
                                                  private val tasteRepository: TasteRepository,
                                                  private val adminPanelService: AdminPanelService,
                                                  private var tobaccoParser: TobaccoParser,
                                                  private var mixParser: MixParser) {


    @GetMapping()
    fun login(): String {
        return "redirect:/main";
    }

    @GetMapping("/parseResult")
    fun parseResult(model: Model): String {
        model.addAttribute("result", "No result yet");
        return "parseResult";
    }

    @GetMapping("/parse")
    fun parse() {
        tobaccoParser.connectPage()?.let { tobaccoParser.startParse(it) };
    }

    @GetMapping("/parse-mix")
    fun parseMix(model: Model): String {
        var result = mixParser.connectPage()?.let { mixParser.startParse(it) };
        model.addAttribute("result", result);
        return "parseResult";
    }

    @GetMapping("/main")
    fun main(model: Model): String {

        var makers: List<Maker> = makerRepository.findAll();
        var tastes: List<Taste> = tasteRepository.findAll();
//        var tobaccos: List<Tobacco> = tobaccoRepository.findAll();

        model.addAttribute("makers", makers);
        model.addAttribute("tastes", tastes);
//        model.addAttribute("tobaccos", tobaccos);

        return "main";
    }

    @PostMapping("/main")
    fun addTobacco(@RequestParam("title") title: String,
                   @RequestParam("makers") maker: String,
                   @RequestParam("description") description: String,
                   @RequestParam("tastes", required = false) taste: String,
                   @RequestParam("strength") strength: Double,
                   @RequestParam("image") image: String,
                   @RequestParam("tags") tags: String,
                   model: Model)
            : String {

        adminPanelService.addTobacco(
                title, maker, description, taste, strength, image, tags);

        return "redirect:/main";
    }


    @PostMapping("/add_mix")
    fun addMix(@RequestParam("title") title: String,
               @RequestParam("tags") tags: String,
               @RequestParam("description") description: String,
               @RequestParam("strength") strength: String,
               model: Model)
            : String {

        adminPanelService.addMix(title, tags, description, strength);

        return "redirect:/main"
    }

    @PostMapping("/add_component")
    fun addComponentToMix(@RequestParam("mixTitle") mixTitle: String,
                          @RequestParam("makerTitle") makerTitle: String,
                          @RequestParam("tobaccoTitle") tobaccoTitle: String,
                          @RequestParam("composition") composition: Int,
                          model: Model)
            : String {

        adminPanelService.addComponentMix(mixTitle, makerTitle,
                tobaccoTitle, composition);

        return "redirect:/main"
    }


    @GetMapping("/catalog_tobaccos")
    fun getAllTobaccos(@RequestParam(name = "filter", defaultValue = "", required = false)
                       filter: String,
                       model: Model): String {

        var tobaccos: List<Tobacco>;
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
    fun getAllMixes(model: Model): String {

        var mixes = mixRepository.findAll();
        model.addAttribute("mixes", mixes);

        return "/catalog_mixes";
    }

}