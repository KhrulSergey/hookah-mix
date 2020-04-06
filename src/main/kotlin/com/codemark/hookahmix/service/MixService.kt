package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.ComponentRepository
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.MixRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.stream.Collectors
import kotlin.streams.toList

@Service
class MixService @Autowired constructor(
        private val mixRepository: MixRepository,
        private var tobaccoService: TobaccoService,
        private var makerService: MakerService,
        private val makerRepository: MakerRepository,
        private val componentRepository: ComponentRepository) {

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): List<Mix>{
        return mixRepository.findAll();
    }

    fun showAllMixes(user: User): MutableList<Mix> {

        var mixesList = mixRepository.findAll();

        var barTobaccos =
                makerService.getTobaccosInBar(user).stream()
                        .flatMap { i -> i.tobaccos.stream() }
                        .collect(Collectors.toList())

        var purchasesTobacco = tobaccoService.findAllPurchases(user);

        for (mix in mixesList) {

            for (item in mix.tobaccoMixList) {
                item.mixesMaker = makerRepository.getOne(item.maker!!.makersId)
            }

            if (barTobaccos.containsAll(mix.tobaccoMixList)) {

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
                        mixTobacco.replacements = ArrayList()
                        mixTobacco.status = TobaccoStatus.PURCHASES

                        for (userTobacco in barTobaccos) {

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

                                        if (purchasesTobacco.contains(mixTobacco)) {
                                            mixTobacco.status = TobaccoStatus.IN_PURCHASES
                                        } else {
                                            mixTobacco.status = TobaccoStatus.PURCHASES
                                        }

//                                        mixTobacco.status = TobaccoStatus.PURCHASES;
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

                        for (userTobacco in barTobaccos) {

                            if (mixTobacco.taste?.taste.equals(userTobacco.taste?.taste)) {
                                existReplacements = true;
                                mixTobacco.replacements.add(userTobacco);

                            } else {
                                if (mixTobacco.status == null ||
                                        !mixTobacco.status.equals(TobaccoStatus.CONTAIN_BAR)) {

                                    if (purchasesTobacco.contains(mixTobacco)) {
                                        mixTobacco.status = TobaccoStatus.IN_PURCHASES
                                    } else {
                                        mixTobacco.status = TobaccoStatus.PURCHASES
                                    }
//                                    mixTobacco.status = TobaccoStatus.PURCHASES

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

    fun getOne(title: String): Mix {
        return mixRepository.findByTitle(title);
    }

    fun isExist(title: String): Boolean {
        return mixRepository.existsByTitle(title);
    }

    fun add(mix: Mix): Mix? {
        var newMix = Mix()
        //TODO check mix content
        newMix.title = mix.title;
        newMix.tags = mix.tags;
        newMix.rating = mix.rating;
        newMix.description = mix.description;
        newMix.strength = mix.strength;
        newMix = mixRepository.save(newMix);
        //check mix creation
        if (newMix.mixesId == 0L) return null;
        //fill components of mix
        var component: Component;
        for (tobacco in newMix.tobaccoMixList) {
            component = Component()
            component.mix = newMix;
            component.tobacco = tobacco;
            component.composition = tobacco.composition;
            newMix.components.add(component)
            tobacco.components.add(component)
            if(!saveMixComponent(component)) return null;
        }
        return newMix;
    }

    fun saveMixComponent (component: Component): Boolean{
        return (componentRepository.save(component).componentsId != 0L);
    }
}