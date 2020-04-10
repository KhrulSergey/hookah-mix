package com.codemark.hookahmix.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "users")
class User(installationCookie: String = "") {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "users_id")
    var id: Long = 0;

    @Column(name = "installation_cookie")
    var installationCookie: String = installationCookie;

    @ManyToMany
    @JoinTable(
            name = "my_tobaccos",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "tobacco_id")]
    )
    var tobaccos: MutableList<Tobacco> = mutableListOf();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var userTobaccos: MutableList<UserTobacco> = mutableListOf();

    override fun toString(): String {
        return "User(installationCookie='$installationCookie', id=$id)";
    }
}