package com.codemark.hookahmix.domain

import javax.persistence.*;

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

    override fun toString(): String {
        return "User(installationCookie='$installationCookie', id=$id)"
    }

}