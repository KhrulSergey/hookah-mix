package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.AllArgsConstructor
import javax.persistence.*

//o	Название
//o	Картинка
//o	Год основания
//o	Описание
//o	Ценовой диапазон
@AllArgsConstructor
@Entity
@Table(name = "makers")
class Maker {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var makersId: Long = 0;
    var title: String = "";
    @Column(name = "founding_year")
    var foundingYear: String = "";
    var description: String = "";

    @OneToOne
    @JoinColumn(name = "file_id")
    @JsonIgnore
    var image: Image? = null;

    @OneToMany(mappedBy = "maker")
    var tobaccos: MutableSet<Tobacco> = mutableSetOf();

    override fun toString(): String {
        return title;
    }

}
