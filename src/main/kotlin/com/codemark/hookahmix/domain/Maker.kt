package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import javax.persistence.*

//o	Название
//o	Картинка
//o	Год основания
//o	Описание
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "makers")
@JsonPropertyOrder("makersId", "title", "description", "foundingYear", "tobaccos")
class Maker(title: String="") {
    @Id
    @Column(name = "makers_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0;

    @Column(name = "title")
    @JsonProperty(value = "title")
    var title: String = title;

    @Column(name = "founding_year")
    var foundingYear: String = "";

    @Column(name = "description")
    var description: String = "";

    @Transient
    var sourceUrl: String = "";

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
