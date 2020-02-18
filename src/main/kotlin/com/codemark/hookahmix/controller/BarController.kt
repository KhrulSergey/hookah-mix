package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.TobaccoStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors.toList

@RestController
@RequestMapping("/api/bar")
class BarController {

    private val mockData = listOf(
        Maker().apply {
            makersId = 1
            title = "Al Fakhamah1"
            val m = this;
            tobaccos = setOf<Tobacco>(
                Tobacco("Blackcurant1", "").apply {
                    tobaccosId = 1
                    maker = m
                    status = TobaccoStatus.CONTAIN_BAR
                },
                Tobacco("Blackcurant2", "").apply {
                    tobaccosId = 2
                    maker = m;
                },
                Tobacco("Blackcurant3", "").apply { tobaccosId = 3; maker = m; }
            )
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
    @GetMapping("/marker/catalog", produces = ["application/json"])
    fun findMarkersBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<Page<Maker>> = ResponseEntity.ok(
        PageImpl(mockData.stream().map { it.tobaccos?.forEach { it.maker = null }; it }.collect(toList()))
    )

    /**
     * Метод получения структурированого списка табаков для экрана В баре
     */
    @GetMapping("/marker/bar", produces = ["application/json"])
    fun findMarkersBar(): ResponseEntity<List<Maker>> = ResponseEntity.ok(
        mockData.stream().map { it.tobaccos?.forEach { it.maker = null }; it }.collect(toList())
    )

    /**
     * Метод получения списка табаков в для экрана Покупки
     */
    @GetMapping("/tobacco/shopping", produces = ["application/json"])
    fun findTobaccoBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<Page<Tobacco>?> =
        ResponseEntity.ok(
            PageImpl(mockData.stream().flatMap { maker ->
                ArrayList(maker.tobaccos).stream().map { it.maker?.tobaccos = null; it }
            }.collect(toList()))
        )

    /**
     * Метод добавления табака в бар
     */
    @PostMapping("/tobacco")
    fun addTobacco(@RequestBody tobacco: Tobacco)
            : ResponseEntity<String> {
        return ResponseEntity(
            "Tobacco '${tobacco.title}' by ${tobacco.maker} " +
                    "has been added", HttpStatus.OK
        );
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
