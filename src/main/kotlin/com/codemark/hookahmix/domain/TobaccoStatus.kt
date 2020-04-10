package com.codemark.hookahmix.domain


import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import java.util.*
import java.util.function.Predicate
import java.util.function.Supplier


/**
 * Статусы табака для пользователя
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = TobaccoStatus.Deserializer::class)
enum class TobaccoStatus(
        @JsonProperty("title") val title: String,
        @JsonIgnore val code: String) {
    NULL_VALUE("Неизвестен", "null"),
    CONTAIN_BAR("В баре", "contain_bar"),
    IN_PURCHASES("В корзине", "in_purchases"),
    PURCHASED("Куплен", "purchased");

    @JsonValue
    fun getId(): String {
        return name;
    }

    companion object {
        /** Convert status from code */
        fun fromCode(statusCode: String): TobaccoStatus {
            return when (statusCode) {
                CONTAIN_BAR.code -> {
                    CONTAIN_BAR
                }
                IN_PURCHASES.code -> {
                    IN_PURCHASES
                }
                PURCHASED.code -> {
                    PURCHASED
                }
                else -> NULL_VALUE
            };
        }
    }

    open class Deserializer protected constructor() :
            StdDeserializer<TobaccoStatus>(TobaccoStatus::class.java) {
        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TobaccoStatus {
            val jsonNode = p.readValueAsTree<JsonNode>()
            val id = jsonNode["id"].asText()
            return Arrays.stream(TobaccoStatus.values())
                    .filter(Predicate<TobaccoStatus> { s: TobaccoStatus ->
                        id == s.getId()
                    })
                    .findFirst()
                    .orElseThrow<JsonMappingException?>(Supplier {
                        ctxt.mappingException(
                                String.format(
                                        "Cannot deserialize DtRegisterType from id = %s",
                                        id
                                )
                        )
                    })
        }
    }
}

