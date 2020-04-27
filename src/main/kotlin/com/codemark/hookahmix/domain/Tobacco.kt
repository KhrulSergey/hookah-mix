package com.codemark.hookahmix.domain

import com.codemark.hookahmix.util.TobaccoStatusConverter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.hibernate.search.annotations.*
import javax.persistence.*

@JsonPropertyOrder("tobaccosId")
@Indexed
@Entity
@Table(name = "tobaccos")
class Tobacco(title: String = "",
              description: String = "",
              strength: Double = 0.0) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("tobaccosId")
    @Column(name = "tobaccos_id")
    var id: Long = 0;

    /** Название табака */
    @Field
    @Column(name = "title")
    var title: String = title;

    /** Описание табака */
    @Column(name = "description")
    var description: String = description;

    /** Крепость табака */
    @Column(name = "strength")
    var strength: Double = strength;

    /** Изображение табака */
    @OneToOne
    @JoinColumn(name = "file_id")
    var image: Image? = null;

    /** Главный вкус табака */
    @OneToOne
    @JsonProperty("taste")
    @JoinColumn(name = "taste_id")
    var mainTaste: Taste? = null;

    /** Пользовательский рейтинг табака */
    @Field
    @Column(name = "rating")
    var rating: Double? = null;

    /** Список вкусов для табака */
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
            name = "tobacco_tastes",
            joinColumns = [JoinColumn(name = "tobaccos_id")],
            inverseJoinColumns = [JoinColumn(name = "tastes_id")]
    )
    var tasteList: MutableList<Taste> = mutableListOf();

    /** Производитель */
    @IndexedEmbedded(depth = 3)
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    @JsonProperty("maker")
    @JsonIgnoreProperties("foundingYear", "description", "tobaccos")
    var maker: Maker? = null;

    /** Ссылка на список табаков для пользователей */
    @JsonIgnore
    @OneToMany(mappedBy = "tobacco", fetch = FetchType.LAZY)
    var userTobaccos: MutableList<UserTobacco> = mutableListOf();

    /** Источник получения табака */
    @Column(name = "source_url")
    var sourceUrl: String? = "";

    /** Дополнительные поля */
    //Статус табака для пользователя
    @Transient
    @Convert(converter = TobaccoStatusConverter::class)
    var status: TobaccoStatus = TobaccoStatus.NULL_VALUE

    override fun toString(): String {
        val makerStr: String = if (maker != null) "${maker?.title}: " else "";
        return makerStr + title;
    }
}