package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Purchase
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.PurchaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate


@Service
class PurchaseService @Autowired constructor(
        private val purchaseRepository: PurchaseRepository) {

    @Value("\${latestPeriodInDays}")
    var latestPeriodValue: Long = 0;

    fun getOne(id: Long): Purchase? {
        return purchaseRepository.findById(id).orElse(null);
    }

    fun getAllLatestPurchasedTobaccoForUser(user: User): MutableSet<Tobacco> {
        val latestDate = LocalDate.now().minusDays(latestPeriodValue);
        val purchaseList = purchaseRepository.findAllByUserAndCreatedDateAfter(user, latestDate);
        return purchaseList.map { purchase -> purchase.tobacco!! }.toMutableSet();
    }

    fun addOne(purchase: Purchase): Purchase? {
        return purchaseRepository.save(purchase);
    }

    /** Удалить покупку табака для пользователя */
    fun deleteOne(purchaseId: Long): Boolean {
        val purchase: Purchase? = purchaseRepository.getOne(purchaseId);
        if (purchase != null) {
            purchaseRepository.delete(purchase);
            return true
        }
        return false
    }

    /** Удалить все покупки о табаке для пользователя */
    fun deleteAllByUserAndTobacco(user: User, tobacco: Tobacco): Boolean {
        val purchaseList: MutableList<Purchase> = purchaseRepository.findAllByUserAndTobacco(user, tobacco);
        if (purchaseList.isNotEmpty()) {
            for (purchase in purchaseList) {
                purchaseRepository.delete(purchase);
            }
        }
        return false
    }
}