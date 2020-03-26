package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.MixSet
import com.codemark.hookahmix.domain.TobaccoStatus
import com.codemark.hookahmix.domain.User
import com.codemark.hookahmix.repository.ComponentRepository
import com.codemark.hookahmix.repository.MixRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import kotlin.streams.toList

@Service
class MixService @Autowired constructor(
        private val mixRepository: MixRepository,
        private val componentRepository: ComponentRepository) {


    fun showAllMixes(user: User): MutableList<Mix> {

        var mixesList = mixRepository.findAll();

        for (mix in mixesList) {

            for (item in mix.tobaccoMixList) {

                var component=
                        componentRepository.getCompositionInComponent(mix.mixesId, item.tobaccosId)
//                println("Mix: " + mix.title)
//                println("Component: " + component.componentsId)
//                println("Tobacco: " + item.tobaccosId)
                item.composition = component;
//                println("Item: " + item.tobaccosId + ", component: " + component.composition)

            }

            if (user.tobaccos.containsAll(mix.tobaccoMixList)) {

                mix.status = MixSet.MATCH_BAR;

                for (tobacco in mix.tobaccoMixList) {
                    tobacco.status = TobaccoStatus.CONTAIN_BAR;
                }

            } else {

                var isTobaccoInBar: Boolean = mix.tobaccoMixList.stream()
                        .anyMatch { i -> user.tobaccos.stream()
                                .anyMatch { f -> StringUtils.pathEquals(
                                        i.title, f.title) } };

                if (isTobaccoInBar) {

                    var existReplacements: Boolean = false;
                    for (mixTobacco in mix.tobaccoMixList) {
                        mixTobacco.replacements = ArrayList();
                        mixTobacco.status = TobaccoStatus.PURCHASES;

//                        println("Mix: " + mix.title)

                        for (userTobacco in user.tobaccos) {

                            if (mixTobacco.tobaccosId.equals(userTobacco.tobaccosId)) {
                                mixTobacco.status = TobaccoStatus.CONTAIN_BAR;


                            } else {
                                if (mixTobacco.taste?.taste.equals(userTobacco.taste?.taste)) {
                                    existReplacements = true;
                                    userTobacco.status = TobaccoStatus.CONTAIN_BAR;
                                    mixTobacco.replacements.add(userTobacco);

                                } else {
                                    if (mixTobacco.status == null ||
                                            !mixTobacco.status.equals(TobaccoStatus.CONTAIN_BAR)) {
                                        mixTobacco.status = TobaccoStatus.PURCHASES;
                                    }
                                }
                            }
                        }
                    }

                    if (existReplacements) {
                        mix.status = MixSet.REPLACEMENT_BAR
                    } else {
                        mix.status = MixSet.PARTIAL_BAR
                    }

                } else {

                    var existReplacements: Boolean = false;
                    for (mixTobacco in mix.tobaccoMixList) {

                        mixTobacco.replacements = ArrayList();
                        mixTobacco.status = TobaccoStatus.PURCHASES;

                        for (userTobacco in user.tobaccos) {

                            if (mixTobacco.taste?.taste.equals(userTobacco.taste?.taste)) {
                                existReplacements = true;
                                mixTobacco.replacements.add(userTobacco);

                            } else {
                                if (mixTobacco.status == null ||
                                        !mixTobacco.status.equals(TobaccoStatus.CONTAIN_BAR)) {
                                    mixTobacco.status = TobaccoStatus.PURCHASES;

                                }
                            }
                        }
                    }

                    if (existReplacements) {
                        mix.status = MixSet.REPLACEMENT_BAR
                    } else {
                        mix.status = MixSet.PARTIAL_BAR
                    }
                }
            }
        }
        return mixesList;
    }

    fun generateMixCount(user: User, status: String?, strength: String?, taste: String?): Int {

        var mixes: MutableList<Mix> = mutableListOf()

        var count: Int = 0
        var result: MutableList<Mix> = mutableListOf()

        if (status != null && status.isNotEmpty()) {

            if (strength != null && strength.isNotEmpty()) {

                if (taste != null && taste.isNotEmpty()) {

                    mixes = showAllMixes(user)
                    result = mixes.stream()
                            .filter { i -> i.status.title.equals(status)
                                    && i.strength == Integer.parseInt(strength)
                                    && i.tags.contains(taste) }
                            .toList().toMutableList()

                } else {


                    mixes = showAllMixes(user)
                    result = mixes.stream()
                            .filter { i -> i.status.title.equals(status)
                                    && i.strength == Integer.parseInt(strength) }
                            .toList().toMutableList()

                }
            } else {

                mixes = showAllMixes(user)

                result = mixes.stream()
                        .filter { i -> i.status.title.equals(status) }
                        .toList().toMutableList()

            }
        }

        count = result.size;
        return count;

    }

}