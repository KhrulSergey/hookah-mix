package com.codemark.hookahmix.domain

import lombok.*
import javax.persistence.*;
import java.io.Serializable

@Entity
@Table(name = "my_tobaccos")
class MyTobacco : Serializable {

    @EmbeddedId
    var myTobaccoId: MyTobaccoId = MyTobaccoId();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    var user: User? = null

    @ManyToOne
    @JoinColumn(name = "tobacco_id")
    @MapsId("tobaccoId")
    var tobacco: Tobacco? = null

    var status: String = ""

    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    class MyTobaccoId : Serializable {

        @Getter
        @Setter
        var userId: Long = 0

        @Getter
        @Setter
        var tobaccoId: Long = 0;

    }

}
