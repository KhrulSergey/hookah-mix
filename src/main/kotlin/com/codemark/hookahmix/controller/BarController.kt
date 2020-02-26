package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository,
                                           private val makerRepository: MakerRepository) {


    private val barTobaccos: MutableList<Maker> = mutableListOf();
    private var installationCookie: String = "";

    private val mockDataBuilder: () -> Unit = {

    }
    private val mockData = listOf(
            Maker().let {
                it.makersId = 1
                it.title = "Al Fakhamah1"
                val maker = it
                it.tobaccos = setOf<Tobacco>(
                        Tobacco("Blackcurant1", "").let { it.tobaccosId = 1; it.maker = maker; it },
                        Tobacco("Blackcurant2", "").let { it.tobaccosId = 2; it.maker = maker; it },
                        Tobacco("Blackcurant3", "").let { it.tobaccosId = 3; it.maker = maker; it }
                ) as MutableSet<Tobacco>
                it
            },
            Maker().let {
                it.makersId = 2
                it.title = "Al Fakhamah2"
                val maker = it
                it.tobaccos = setOf<Tobacco>(
                        Tobacco("Blackcurant4", "").let { it.tobaccosId = 4; it.maker = maker; it },
                        Tobacco("Blackcurant5", "").let { it.tobaccosId = 5; it.maker = maker; it },
                        Tobacco("Blackcurant6", "").let { it.tobaccosId = 6; it.maker = maker; it }
                ) as MutableSet<Tobacco>
                it
            }
    )

    /**
     * Метод получения  структурированого списка табаков для экрана Все табаки
     */
//    @GetMapping("/marker/catalog", produces = ["application/json"])
//    fun findMarkersBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<Page<Maker>> = ResponseEntity.ok(
//            PageImpl(mockData)
//    )

    @GetMapping("/marker/catalog")
    fun findMarkersBy(): List<Maker> {

        var catalogTobaccos = makerRepository.findAllSortedByTitle();
        return catalogTobaccos;

    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */

    @GetMapping("/marker/bar")
    fun findMarkersBar(): MutableList<Maker> {

        return barTobaccos;
    }

    /**
     * Метод получения списка табаков в для экрана Покупки
     */

    @GetMapping("/tobacco/shopping", produces = ["application/json"])
    fun findTobaccoBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<PageImpl<Tobacco>> = ok(
            PageImpl(mockData.flatMap { maker -> ArrayList(maker.tobaccos) })
    )

    /**
     * Метод добавления табака в бар
     */

//     POST, of course
//    @GetMapping("/tobacco/{id}")
//    fun addTobacco(@PathVariable("id") id: Long) {
//
//        var barTobacco: Tobacco = tobaccoRepository.getOne(id);
//        barTobacco.existInBar = true;
//
//        println("Tobacco: $barTobacco")
//
//        var title: String? = barTobacco.maker?.title;
//        println("Title: $title")
//
//        var barMaker = makerRepository.getOneByTobacco(title);
//        println("Maker: $barMaker")
//
//        for (index in barTobaccos) {
//            if (index.title.equals(title)) {
//                index.tobaccos.add(barTobacco);
//                println("Tobacco $barTobaccos was added!")
//                println("Size: $barTobaccos.size")
//                return;
//            }
//        }
//
//        barMaker.tobaccos.add(barTobacco);
//        barTobaccos.add(barMaker);
//        println("Size: ${barTobaccos.size}")
//    }

    @GetMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long) {

        var barTobacco: Tobacco = tobaccoRepository.getOne(id);

        barTobacco.status = TobaccoStatus.CONTAIN_BAR.title;

        var title: String? = barTobacco.maker?.title;
        var barMaker = makerRepository.getOneByTobacco(title);

        for (index in barTobaccos) {
            if (index.title.equals(title)) {
                index.tobaccos.add(barTobacco);
                return;
            }
        }

        barMaker.tobaccos.add(barTobacco);
        barTobaccos.add(barMaker);

    }

    /**
     * Метод удаления табака из бара
     */
    @DeleteMapping("/tobacco/{id}")
    fun delete(@PathVariable("id") id: Long)
            : ResponseEntity<String> = ResponseEntity(
            "Tobacco $id successfully deleted", HttpStatus.OK
    );
}