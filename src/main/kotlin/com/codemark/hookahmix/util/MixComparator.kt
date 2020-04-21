package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.MixStatus
import org.springframework.stereotype.Component

/** Сравнение миксов в зависимости от их статуса и количества табака на "докупить" */
@Component
class MixComparator: Comparator<Mix> {
    override fun compare(o1: Mix, o2: Mix): Int {
        return if (o1.status == MixStatus.PARTIAL_BAR && o1.status == o2.status){
            o1.countTobaccoForPurchase.compareTo(o2.countTobaccoForPurchase)
        }
        else {
            o1.status.ordinal.compareTo(o2.status.ordinal);
        }
    }
}