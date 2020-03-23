package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Mix
import org.springframework.stereotype.Component

@Component
class MixComparator: Comparator<Mix> {
//    override fun compare(o1: Mix, o2: Mix): Int {
//
//        if (o1.status.equals("MATCH_BAR")) {
//            if (o2.status.equals("MATCH_BAR")) {
//                return 0;
//            }
//            return 1;
//        } else if (o2.equals("MATCH_BAR")) {
//            return 1;
//        }
//        return o1.status.compareTo(o2.status);
//    }

    override fun compare(o1: Mix, o2: Mix): Int {
        return o1.status.ordinal.compareTo(o2.status.ordinal)
    }

}