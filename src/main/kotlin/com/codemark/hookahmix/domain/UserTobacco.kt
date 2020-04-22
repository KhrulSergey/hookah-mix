package com.codemark.hookahmix.domain

import com.codemark.hookahmix.util.TobaccoStatusConverter
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

/** Модель для хранения списка табаков у пользователя*/
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
    @Column(name = "user_tobacco_id")
    var id: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tobacco_id")
    var tobacco: Tobacco? = tobacco

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
                    "status = $status " +
                    "createdDate = $createdDate " +
                    "updatedDate = $updatedDate " +
                    ")"
}

