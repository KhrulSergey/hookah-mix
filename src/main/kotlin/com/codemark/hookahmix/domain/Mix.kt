package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.IndexedEmbedded
import javax.persistence.*

/** Модель Микса из табаков */
@Indexed
@Entity
@Table(name = "mixes")
class Mix {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var mixesId: Long = 0;

    @Field
    @Column(name = "title")
    var title: String = "";

    @Column(name = "rating")
    var rating: Double? = null;

    @Field
    @Column(name = "tags")
    var tags: String = "";

    @Column(name = "description")
    var description: String = "";

    @Column(name = "strength")
    var strength: Double? = 0.0;

    @Column(name = "source_url")
    var sourceUrl: String? = "";

    @Column(name = "is_original")
    var isOriginal: Boolean? = true;

    @OneToMany(mappedBy = "mixRef")
    var components: MutableList<MixComponent> = mutableListOf()

    /** Статус микса для пользователя */
    @Transient
    var status: MixStatus = MixStatus.NULL_VALUE

    /** Количество табаков в статусе "докупить" */
    @Transient
    var countTobaccoForPurchase: Int = 0;

    /** Служебное поле: Список табаков, входящих в состав микса */
    @JsonIgnore
    @IndexedEmbedded(depth = 3)
    @ManyToMany
    @JoinTable(
            name = "components",
            joinColumns = [JoinColumn(name = "mix_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccoMixList: MutableList<Tobacco> = mutableListOf();

    override fun toString(): String {
        return "Mix $title";
    }


}
