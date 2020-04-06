package com.codemark.hookahmix.domain


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.*

@JsonPropertyOrder("tobaccosId")
@Entity
@Table(name = "tobaccos")
data class Tobacco(var title: String = "",
                   var description: String = "",
                   var strength: Double = 3.0) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("tobaccosId")
    @Column(name = "tobaccos_id")
    var tobaccosId: Long = 0;

    @OneToOne
    @JoinColumn(name = "file_id")
    var image: Image? = null;

    @JsonIgnore
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "maker_id")
    var maker: Maker? = null;

    @OneToOne
    @JoinColumn(name = "taste_id")
    var taste: Taste? = null;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "my_tobaccos",
            joinColumns = [JoinColumn(name = "tobacco_id")],
            inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var users: MutableList<User> = mutableListOf();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "components",
            joinColumns = [JoinColumn(name = "tobacco_id")],
            inverseJoinColumns = [JoinColumn(name = "mix_id")]
    )
    var mixList: MutableList<Mix> = mutableListOf();

    @JsonIgnore
    @OneToMany(mappedBy = "tobacco")
    var myTobaccos: MutableSet<MyTobacco> = mutableSetOf();

    @JsonIgnore
    @OneToMany(mappedBy = "tobacco")
    var components: MutableSet<Component> = mutableSetOf()

    @Transient
    var status: TobaccoStatus = TobaccoStatus.NULL_VALUE

    @Transient
    var replacements: MutableList<Tobacco> = mutableListOf()

    @Transient
    @JsonProperty("maker")
    @JsonIgnoreProperties("foundingYear", "description", "image", "tobaccos")
    var mixesMaker: Maker? = null

    @Transient
    var composition: Int = 0
    @Transient
    var sourceUrl: String = "";

    override fun toString(): String {
        val compositionStr: String = if (composition != 0) " - $composition%" else "";
        val makerStr: String = if (maker != null) "${maker?.title}: " else "";
        return makerStr + title + compositionStr;
    }

}