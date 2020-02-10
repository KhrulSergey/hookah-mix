package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Tobacco
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bar")

class BarController {

    @GetMapping("/all")
    fun findAll(@RequestParam(defaultValue = "", required = false) maker : String)
            : ResponseEntity<String> {

        if (maker.isNotEmpty()) {
            return ResponseEntity(
                    "Tobaccos from $maker has been shown", HttpStatus.OK);
        }

        return ResponseEntity(
                "All available tobaccos has been shown", HttpStatus.OK);
    }

    @GetMapping("{id}")
    fun findById(@PathVariable("id") id : Long)
            : ResponseEntity<String> = ResponseEntity(
            "Tobacco with ID $id was found", HttpStatus.OK);


    @GetMapping("/mixes/{tobacco}")
    fun getAvailableMixes(@PathVariable("tobacco") title : String)
            : ResponseEntity<String> = ResponseEntity(
            "All available mixes with tobacco '$title'", HttpStatus.OK);


    @PostMapping("/add")
    fun addTobacco(@RequestBody tobacco : Tobacco)
            : ResponseEntity<String> {
        return ResponseEntity(
                "Tobacco '${tobacco.title}' by ${tobacco.maker} " +
                        "has been added", HttpStatus.OK);
    }


    @DeleteMapping("delete/{id}")
    fun delete(@PathVariable("id") id : Long)
            : ResponseEntity<String> = ResponseEntity(
            "Tobacco $id successfully deleted", HttpStatus.OK);


}