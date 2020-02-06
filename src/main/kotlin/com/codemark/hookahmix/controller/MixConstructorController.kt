package com.codemark.hookahmix.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/mixes")

//@Autowired constructor(private val mixRepository: MixRepository)

class MixConstructorController {

    /*
    TODO there must be a list of mixes sorted by accuracy matching
     */

    @GetMapping("/all")
    fun showAvailableMixes()
            : ResponseEntity<String> = ResponseEntity(
            "All available mixes was shawn", HttpStatus.OK);

    /*
    TODO redirect to tobacco's page
     */

    @GetMapping("{id}")
    fun findMixById(@PathVariable("id") id : Long)
            : ResponseEntity<String> = ResponseEntity(
            "Mix $id was found" , HttpStatus.OK);

    /*
    TODO make a filters by tags & strength
     */

    @GetMapping()
    fun findMixUsingFilters(@RequestParam(value = "filter") tag : String)
            : ResponseEntity<String> {
        if (!tag.equals("tag")) {
            return ResponseEntity("Illegal argument exception", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity("Search by tag '$tag' was successfully done", HttpStatus.OK);
    }


}