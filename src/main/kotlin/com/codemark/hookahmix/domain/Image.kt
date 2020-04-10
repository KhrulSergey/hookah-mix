package com.codemark.hookahmix.domain

import com.codemark.hookahmix.util.ImagePathConverter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import lombok.Data
import javax.persistence.*

@Data
@Entity
@Table(name = "files")
class Image (name:String? = "") {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "files_id")
    var id: Long = 0;

    @JsonValue
    @JsonProperty(value = "image")
    @Column(name = "name")
    @Convert(converter = ImagePathConverter::class)
    var name: String? = name;

    override fun toString(): String {
        return "Image file URI:$name";
    }
}