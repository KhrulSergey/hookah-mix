package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonValue
import lombok.Data
import java.util.*
import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "files_id")
    var id: Long = 0;
    @Column(name = "file")
    var image: ByteArray? = null

    @JsonValue
    fun getImage(): String {
        val array = Base64.getEncoder().encode(image);
        return String(array);
    }

    override fun toString(): String {
        return getImage();
    }


}