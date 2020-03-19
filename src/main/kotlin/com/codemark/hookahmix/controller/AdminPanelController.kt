package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.*
import com.codemark.hookahmix.util.ImageUtil
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
                                                  private val componentRepository: ComponentRepository,
                                                  private val fileRepository: FileRepository,
                                                  private val imageUtil: ImageUtil,
                                                  private var tobaccoParser: TobaccoParser) {


    @GetMapping()
    fun login() : String {
        return "redirect:/main";
    }

    @GetMapping("/parse")
    fun parse() {
        tobaccoParser.connectPage()?.let { tobaccoParser.startParse(it) };
    }

    @GetMapping("/main")
    fun main(model: Model) : String {

        var makers: List<Maker> = makerRepository.findAll();
        var tastes: List<Taste> = tasteRepository.findAll();
//        var tobaccos: List<Tobacco> = tobaccoRepository.findAll();

        model.addAttribute("makers", makers);
        model.addAttribute("tastes", tastes);
//        model.addAttribute("tobaccos", tobaccos);

        return "main";
    }

    @PostMapping("/main")
    fun addTobacco(@RequestParam("title") title : String,
                   @RequestParam("makers") maker : String,
                   @RequestParam("description") description : String,
                   @RequestParam("tastes", required = false) taste : String,
                   @RequestParam("strength") strength : Double,
                   model : Model)
            : String {

        var findMaker: Maker = makerRepository.findByTitle(maker);
        var findTaste: Taste = tasteRepository.findByTaste(taste);

        var newTobacco = Tobacco(title, description, strength);
        newTobacco.maker = findMaker;
        newTobacco.taste = findTaste;

        tobaccoRepository.save(newTobacco);

        return "redirect:/main";
    }


    @PostMapping("/add_mix")
    fun addMix(@RequestParam("title") title: String,
               @RequestParam("tags") tags: String,
               @RequestParam("description") description: String,
               @RequestParam("strength") strength: String,
               model: Model)
            : String {

        var mix = Mix();
        mix.title = title;
        mix.tags = tags;
        mix.rating = 0;
        mix.description = description;
        mix.strength = Integer.parseInt(strength);

        mixRepository.save(mix)

        println("Mix: ${mix.title}, ${mix.tags}, ${mix.description}, ${mix.strength}")


        return "redirect:/main"
    }

    @PostMapping("/add_component")
    fun addComponentToMix(@RequestParam("mixTitle") mixTitle: String,
                          @RequestParam("makerTitle") makerTitle: String,
                          @RequestParam("tobaccoTitle") tobaccoTitle: String,
                          @RequestParam("composition") composition: Int,
                          model: Model): String {

        var mix = mixRepository.findByTitle(mixTitle);
        var tobacco = tobaccoRepository.findOneByTitleAndMaker(tobaccoTitle, makerTitle)

        var component: Component = Component();
        component.mix = mix;
        component.tobacco = tobacco;
        component.composition = composition;

        mix.components.add(component);
        tobacco.components.add(component);

        componentRepository.save(component);

        return "redirect:/main"
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

        var mixes = mixRepository.findAll();
        model.addAttribute("mixes", mixes);

        return "/catalog_mixes";
    }

}