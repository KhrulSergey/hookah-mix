package com.codemark.hookahmix.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/shop")
class ShoppingListController {

    @GetMapping("/add")
    fun addTobaccoFromMix()
            : ResponseEntity<String> = ResponseEntity(
            "Tobacco successfully added", HttpStatus.OK);


    @GetMapping("/list")
    fun showAllPurchases()
            : ResponseEntity<String> = ResponseEntity(
            "All available purchases", HttpStatus.OK);

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id : Long)
            : ResponseEntity<String> = ResponseEntity(
            "Removal has been done", HttpStatus.OK);

}