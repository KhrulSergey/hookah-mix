package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.MixSet
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/mixes")

class MixConstructorController {

    @GetMapping("/generator")
    fun generateMix(): List<Mix> {
        val result = listOf<Mix>(
            Mix().apply {
                mixesId = 1;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 2;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 3;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.PARTIAL_BAR;
            },
            Mix().apply {
                mixesId = 4;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 5;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 6;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 7;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 8;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                warmupTime = "";
                set = MixSet.MATCH_BAR;
            }
        )
        return result;
    }
}
