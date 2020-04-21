package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class MixStatus(@JsonProperty("title") val title: String) {
    MATCH_BAR("Все есть"),
    REPLACEMENT_BAR("С заменой"),
    PARTIAL_BAR("Нужно докупить"),
    NULL_VALUE("Неизвестен");


    @JsonValue
    fun getId(): String {
        return name
    }

}
