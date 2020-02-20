package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.repository.TobaccoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

@RestController
@RequestMapping("/api/bar")
class BarController @Autowired constructor(private val tobaccoRepository: TobaccoRepository) {


    private val barTobaccos: MutableList<Tobacco> = mutableListOf();

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
                )
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
                )
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
    fun findMarkersBy(): List<Tobacco> {

        var catalogTobaccos = tobaccoRepository.findAllSortedByMaker();
        return catalogTobaccos;
    }

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */
//    @GetMapping("/marker/bar", produces = ["application/json"])
//    fun findMarkersBar(): ResponseEntity<List<Maker>> = ResponseEntity.ok(
//            mockData
//    )

    @GetMapping("/marker/bar")
    fun findMarkersBar(): MutableList<Tobacco> {

        return barTobaccos;
    }

    /**
     * Метод получения списка табаков в для экрана Покупки
     */

    @GetMapping("/tobacco/shopping", produces = ["application/json"])
    fun findTobaccoBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<PageImpl<Tobacco>> = ResponseEntity.ok(
            PageImpl(mockData.flatMap { maker -> ArrayList(maker.tobaccos) })
    )

    /**
     * Метод добавления табака в бар
     */

    @PostMapping("/tobacco/{id}")
    fun addTobacco(@PathVariable("id") id: Long): Unit {

        var barTobacco : Tobacco = tobaccoRepository.getOne(id);
        barTobacco.existInBar = true;

        barTobaccos.add(barTobacco);
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