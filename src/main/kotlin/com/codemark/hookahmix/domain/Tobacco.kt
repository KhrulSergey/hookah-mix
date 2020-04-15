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

    @Field
    @Column(name = "title")
    var title: String = title;

    @Column(name = "description")
    var description: String = description;

    @Column(name = "strength")
    var strength: Double = strength;

    @OneToOne
    @JoinColumn(name = "file_id")
    var image: Image? = null;

    @OneToOne
    @JoinColumn(name = "taste_id")
    var taste: Taste? = null;

    @IndexedEmbedded(depth=3)
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id")
    @JsonProperty("maker")
    @JsonIgnoreProperties("foundingYear", "description", "tobaccos")
    var maker: Maker? = null;

    @JsonIgnore
    @OneToMany(mappedBy = "tobacco", fetch = FetchType.LAZY)
    var userTobaccos: MutableList<UserTobacco> = mutableListOf();

//    @IndexedEmbedded
//    @JsonIgnore
//    @ManyToMany
//    @JoinTable(
//            name = "components",
//            joinColumns = [JoinColumn(name = "tobacco_id")],
//            inverseJoinColumns = [JoinColumn(name = "mix_id")]
//    )
//    var mixList: MutableList<Mix> = mutableListOf();
//
//    @ContainedIn
//    @JsonIgnore
//    @OneToMany(mappedBy = "tobacco")
//    var components: MutableSet<Component> = mutableSetOf()

    /** Дополнительные поля */
    //Статус табака для пользователя
    @Transient
    @Convert(converter = TobaccoStatusConverter::class)
    var status: TobaccoStatus = TobaccoStatus.NULL_VALUE

    //Список табаков-замен из тех табаков что есть у пользователя в баре
    @Transient
    var replacements: MutableList<Tobacco> = mutableListOf()

    //Соотношение табака в миксе
    @Transient
    var composition: Int = 0

    //Источник получения табака
    @JsonIgnore
    @Transient
    var sourceUrl: String = "";

    override fun toString(): String {
        val compositionStr: String = if (composition != 0) " - $composition%" else "";
        val makerStr: String = if (maker != null) "${maker?.title}: " else "";
        return makerStr + title + compositionStr;
    }

}