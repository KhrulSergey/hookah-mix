package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Tobacco
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bar")
class BarController {


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
     * Метод получения табаков по фильтру с учетом авторизации
     * return
     */
    @GetMapping("/marker", produces = ["application/json"])
    fun findMarkersBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<Page<Maker>> = ResponseEntity.ok(
        PageImpl(mockData)
    )

    /**
     * Метод получения
     */
    @GetMapping("/tobacco", produces = ["application/json"])
    fun findTobaccoBy(@RequestParam(required = false) page: Pageable?): ResponseEntity<Page<Tobacco>?> = ResponseEntity.ok(
        PageImpl(mockData.flatMap { maker -> ArrayList(maker.tobaccos) })
    )

    @PostMapping("/tobacco")
    fun addTobacco(@RequestBody tobacco: Tobacco)
            : ResponseEntity<String> {
        return ResponseEntity(
            "Tobacco '${tobacco.title}' by ${tobacco.maker} " +
                    "has been added", HttpStatus.OK
        );
    }

    @DeleteMapping("/tobacco/{id}")
    fun delete(@PathVariable("id") id: Long)
            : ResponseEntity<String> = ResponseEntity(
        "Tobacco $id successfully deleted", HttpStatus.OK
    );
}
