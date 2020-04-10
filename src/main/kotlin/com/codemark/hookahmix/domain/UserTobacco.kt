package com.codemark.hookahmix.domain

import com.codemark.hookahmix.util.TobaccoStatusConverter
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tobacco")
class UserTobacco (
        user: User? = null,
        tobacco: Tobacco? = null,
        status: TobaccoStatus = TobaccoStatus.NULL_VALUE) : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_tobaccos_id")
    var id: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tobacco_id")
    var tobacco: Tobacco? = tobacco

//    @Column(name = "user_id")
//    var userId: Long? = null
//
//    @Column(name = "tobacco_id")
//    var tobaccoId: Long? = null

    @Column(name = "status")
    @Convert(converter = TobaccoStatusConverter::class)
    var status: TobaccoStatus = status;

    @CreatedDate
    @Column(name = "created_at")
    var createdDate: LocalDate = LocalDate.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedDate: LocalDate? = null;

    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
//                    "userTobaccosId = $userTobaccosId " +
//                    "userId = $userId " +
//                    "tobaccoId = $tobaccoId " +
                    "status = $status " +
                    "createdDate = $createdDate " +
                    "updatedDate = $updatedDate " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserTobacco

//        if (userTobaccosId != other.userTobaccosId) return false
//        if (userId != other.userId) return false
//        if (tobaccoId != other.tobaccoId) return false
        if (status != other.status) return false
        if (createdDate != other.createdDate) return false
        if (updatedDate != other.updatedDate) return false

        return true
    }
}

