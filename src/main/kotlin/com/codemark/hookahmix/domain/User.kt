package com.codemark.hookahmix.domain

import javax.persistence.*;

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "users_id")
    var id: Long = 0;
    @Column(name = "installation_cookie")
    var installationCookie = ""

}