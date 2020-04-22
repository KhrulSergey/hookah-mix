package com.codemark.hookahmix.domain

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import javax.persistence.*

/** Модель для хранения списка покупок табаков у пользователя */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchases")
class Purchase(user: User? = null, tobacco: Tobacco? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "purchase_id")
    var id: Long? = null;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "users_id")
    var user: User? = user;

    @ManyToOne
    @JoinColumn(name = "tobacco_id", referencedColumnName = "tobaccos_id")
    var tobacco: Tobacco? = tobacco;

    @CreatedDate
    @Column(name = "created_at", nullable = true)
    var createdDate: LocalDate = LocalDate.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    var updatedDate: LocalDate? = null;

    override fun toString(): String =
            "Purchase $id by user-${user?.id} with tobacco-${tobacco?.id}:${tobacco?.title} " +
                    "was on $createdDate)";
}

