package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
@JsonPropertyOrder("title", "id", "description", "image", "foundingYear", "tobaccos")
class Maker(title: String = "") {
    @Id
    @Column(name = "makers_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0;

    @Column(name = "title")
    var title: String = title;

    @Column(name = "founding_year")
    var foundingYear: String = "";

    @Column(name = "description")
    var description: String = "";

    @OneToOne
    @JoinColumn(name = "file_id")
    var image: Image? = null;

    @JsonIgnoreProperties("maker")
    @OneToMany(mappedBy = "maker")
    var tobaccos: MutableSet<Tobacco> = mutableSetOf();

    /** Дополнительные поля */
    @Transient
    @JsonIgnore
    var sourceUrl: String = "";

    @Transient
    @JsonIgnore
    var strength: Double = 0.0;

    override fun toString(): String {
        return title;
    }

}
