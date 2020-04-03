package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import lombok.AllArgsConstructor
import javax.persistence.*

//o	Название
//o	Картинка
//o	Год основания
//o	Описание
@AllArgsConstructor
@Entity
@Table(name = "makers")
@JsonPropertyOrder("makersId", "title", "description", "foundingYear", "tobaccos")
class Maker(title: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var makersId: Long = 0;
    @JsonProperty(value = "title")
    var title: String = title;
    @Column(name = "founding_year")
    var foundingYear: String = "";
    var description: String = "";

    @OneToOne
    @JoinColumn(name = "file_id")
    var image: Image? = null;

    @JsonIgnoreProperties("maker")
    @OneToMany(mappedBy = "maker")
    var tobaccos: MutableSet<Tobacco> = mutableSetOf();

    override fun toString(): String {
        return title;
    }

}
