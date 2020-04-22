package com.codemark.hookahmix.repository

import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.domain.UserTobacco
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** Репозиторий управления записями "Табаки в баре и в корзине у пользователя */
@Repository
interface UserTobaccosRepository : JpaRepository<UserTobacco, Long> {

    fun findAllByUserIdAndTobaccoId(userId: Long, tobaccoId: Long): MutableList<UserTobacco>;

    fun findAllByUserAndTobacco(user: User, tobacco: Tobacco): MutableList<UserTobacco>;
}