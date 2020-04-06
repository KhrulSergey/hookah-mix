package com.codemark.hookahmix.domain.dto

import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

//TODO сделать описание модели
data class MixFilterInfoDto(
    var ingredients: List<Ingredient> = ArrayList(),
    var strengthLevel: List<StrengthLevel> = ArrayList(),
    var tastes: List<Taste> = ArrayList()
)

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class StrengthLevel(@JsonProperty("title") val title: String) {
    LIGHT("Легкий"),
    MEDIUM("Средний"),
    STRONG("Крепкий");
}

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class IngredientType(@JsonProperty("title") val title: String) {
    ALL_IN_BAR("Все есть"),
    WITH_REPLACE("С заменой"),
    WITH_BAY("С докупкой"),
}

data class Ingredient(
    var type: IngredientType,
    var title: String,
    var tobacco: Tobacco? = null
)


