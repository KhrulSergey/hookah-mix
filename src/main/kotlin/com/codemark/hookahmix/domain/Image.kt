package com.codemark.hookahmix.domain

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
    var id: Long = 0
    @Column(name = "name")
    var name: String? = name

//    fun getImage(): String {
//        val array = Base64.getDecoder().decode(image)
//        return String(array)
//    }

    @JsonValue
    @JsonProperty(value = "image")
    override fun toString(): String {
        return "Image filename:$name";
    }


}