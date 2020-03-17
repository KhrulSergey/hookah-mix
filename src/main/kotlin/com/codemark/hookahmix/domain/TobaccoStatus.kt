package com.codemark.hookahmix.domain


import com.fasterxml.jackson.annotation.JsonFormat
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


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = TobaccoStatus.Deserializer::class)
enum class TobaccoStatus(@JsonProperty("title") val title: String) {
    NEED_BAR("В бар"),
    CONTAIN_BAR("В баре"),
    PURCHASES("Докупить"),
    IN_PURCHASES("В покупках");

//    @JsonProperty("id")
    @JsonValue
    fun getId(): String {
        return name
    }

    class Deserializer protected constructor() :
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

