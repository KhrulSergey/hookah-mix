package com.codemark.hookahmix.controller

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.MixSet
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.dto.Ingredient
import com.codemark.hookahmix.domain.dto.IngredientType
import com.codemark.hookahmix.domain.dto.MixFilterInfoDto
import com.codemark.hookahmix.domain.dto.StrengthLevel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Arrays.asList

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
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 2;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 3;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.PARTIAL_BAR;
            },
            Mix().apply {
                mixesId = 4;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 5;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 6;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 7;
                title = "Лунная ночь";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            },
            Mix().apply {
                mixesId = 8;
                title = "Жвачка";
                rating = ""
                tags = "Сладкий,Пряный";
                set = MixSet.MATCH_BAR;
            }
        )
        return result;
    }

    @GetMapping("/filter")
    fun generateFilter(): MixFilterInfoDto {
        return MixFilterInfoDto(
            listOf(
                Ingredient(IngredientType.ALL_IN_BAR, "Все есть"),
                Ingredient(IngredientType.WITH_REPLACE, "С заменой"),
                Ingredient(IngredientType.WITH_BAY, "Докупить")
            ), listOf(
                StrengthLevel.LIGHT,
                StrengthLevel.MEDIUM,
                StrengthLevel.STRONG
            ), listOf(
                Taste().apply {
                    tastesId = 1
                    taste = "Яблако"
                },
                Taste().apply {
                    tastesId = 2
                    taste = "Цитрус"
                },
                Taste().apply {
                    tastesId = 3
                    taste = "Ваниль"
                },
                Taste().apply {
                    tastesId = 4
                    taste = "Арбуз"
                },
                Taste().apply {
                    tastesId = 4
                    taste = "Корица"
                }
            )
        );
    }

    @GetMapping("/count")
    fun countGeneratedMix(): Int {
        return 15;
    }
}
