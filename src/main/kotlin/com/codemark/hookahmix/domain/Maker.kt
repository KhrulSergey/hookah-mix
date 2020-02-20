package com.codemark.hookahmix.domain

import lombok.AllArgsConstructor
import java.time.LocalDate
import java.util.*
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
    var makersId : Long = 0;
    var title : String = "";
    var image : String = "";
    @Column(name = "founding_year")
    var foundingYear : String = "";
    var description : String = "";

    @OneToMany
    @JoinColumn(name = "tobaccos_id")
    var tobaccos : Set<Tobacco>? = null;

    override fun toString(): String {
        return title;
    }

}
