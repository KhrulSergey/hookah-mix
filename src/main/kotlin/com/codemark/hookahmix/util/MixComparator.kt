package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Mix
import org.springframework.stereotype.Component

@Component
class MixComparator: Comparator<Mix> {

    override fun compare(o1: Mix, o2: Mix): Int {
        return o1.status.ordinal.compareTo(o2.status.ordinal)
    }

}