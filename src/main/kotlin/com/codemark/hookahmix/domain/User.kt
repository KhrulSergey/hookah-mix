package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
class User (@Column(name = "installation_cookie")
            var installationCookie: String = "") {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "users_id")
    var id: Long = 0;

    @ManyToMany
    @JoinTable(
            name = "my_tobaccos",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccos: MutableList<Tobacco> = mutableListOf();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    var myTobaccos: MutableSet<MyTobacco> = mutableSetOf();

    @Transient
    var latestPurchases: Queue<Tobacco> = ArrayDeque(); // delete?

    @JsonIgnoreProperties("tobaccos")
    @Transient
    var barTobaccos: MutableSet<Maker> = mutableSetOf()

    override fun toString(): String {
        return "User(installationCookie='$installationCookie', id=$id)"
    }

}