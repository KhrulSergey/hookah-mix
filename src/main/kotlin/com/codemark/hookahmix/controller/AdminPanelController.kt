package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.dto.DataParserInfoDto
import com.codemark.hookahmix.domain.dto.ParseStatus
import com.codemark.hookahmix.repository.TasteRepository
import com.codemark.hookahmix.service.*
import com.codemark.hookahmix.util.MixParser
import com.codemark.hookahmix.util.TobaccoParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@PropertySource("classpath:parser-mixes.properties")
@Controller
@RequestMapping(value = ["/", "api/admin"])
class AdminPanelController @Autowired constructor(private val tobaccoService: TobaccoService,
                                                  private val makerService: MakerService,
                                                  private val mixService: MixService,
                                                  private val tasteService: TasteService,
                                                  private val adminPanelService: AdminPanelService,
                                                  private var tobaccoParser: TobaccoParser,
                                                  private var mixParser: MixParser) {
    @Value("\${DEFAULT_PARSE_DATA_SIZE}")
    var defaultParseDataSize: Int = 0;


    @GetMapping()
    fun login(): String {
        return "redirect:/main";
    }

    @GetMapping("/parseResult")
    fun parseResult(model: Model): String {

        model.addAttribute("result", DataParserInfoDto<Any>(status=ParseStatus.NOT_STARTED));
        return "parseResult";
    }

    @GetMapping("/parse")
    fun parse() {
        tobaccoParser.connectPage()?.let { tobaccoParser.startParse(it) };
    }

    @GetMapping("/parse-mix")
    fun parseMix(@RequestParam(required = false) count: Int?, model: Model): String {
        var dataCount = count;
        if (dataCount == null) dataCount = defaultParseDataSize;
        var result: DataParserInfoDto<Mix>? = mixParser.connectPage()?.let { mixParser.startParse(it, dataCount) };
        model.addAttribute("result", result);
        return "parseResult";
    }

    @GetMapping("/main")
    fun main(model: Model): String {

        var makers: List<Maker> = makerService.getAll();
        var tastes: List<Taste> = tasteService.getAll();
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

            tobaccos = tobaccoService.getAllByMaker(filter);

            model.addAttribute("tobaccos", tobaccos);
            model.addAttribute("filter", filter);

            return "/catalog_tobaccos";
        }

        tobaccos = tobaccoService.getAll();

        model.addAttribute("tobaccos", tobaccos);
        model.addAttribute("filter", filter);

        return "/catalog_tobaccos";
    }


    @GetMapping("/catalog_mixes")
    fun getAllMixes(model: Model): String {

        var mixes = mixService.getAll();
        model.addAttribute("mixes", mixes);

        return "/catalog_mixes";
    }

}