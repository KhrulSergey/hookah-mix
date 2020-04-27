package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.MixComponentRepository
import com.codemark.hookahmix.repository.MixRepository
import com.codemark.hookahmix.service.searchBuilder.MixSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class MixService @Autowired constructor(
        private val mixRepository: MixRepository,
        private var tobaccoService: TobaccoService,
        private val mixComponentRepository: MixComponentRepository,
        private val mixSearch: MixSearch) {

    /** Параметр, ограничивающий список записей  более данного значения */
    @Value("\${mixRatingLimit}")
    var mixRatingLimitValue: Double = 0.0;

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): MutableList<Mix> {
        return mixRepository.findAll().toMutableList();
    }

    fun getAllLimitByRating(ratingLimit: Double = mixRatingLimitValue): MutableList<Mix> {
        return mixRepository.findAllByRatingAfterOrderByRatingDesc(ratingLimit).toMutableList();
    }

    /** Возвращает список миксов и его компонентов, с проставленными статусами согласно наличия табаков у Пользователя
     *  Также осуществляется поиск по миксам при необходимости*/
    fun getAllForUserWithSearch(user: User, searchQuery: String? = null): MutableList<Mix> {
        val mixesList = if (searchQuery.isNullOrBlank()) {
            getAllLimitByRating(mixRatingLimitValue);
        } else {
            searchByTagsAndLimitRating(searchQuery, mixRatingLimitValue);
        }

        val userTobaccosList = tobaccoService.getAllUserTobacco(user);
        val barTobaccos = tobaccoService.getAllUserTobaccoInBar(user);
        var currentTobacco: Tobacco?;
        var replacementList: MutableList<Tobacco>;
        for (mix in mixesList) {
            //Если все табаки есть в баре у пользователя, то микс со статусом MixSet.MATCH_BAR
            //а табаки со статусом TobaccoStatus.CONTAIN_BAR
            if (barTobaccos.containsAll(mix.tobaccoMixList)) {
                mix.status = MixStatus.MATCH_BAR;
                for (component in mix.components) {
                    component.tobaccoRef?.status = TobaccoStatus.CONTAIN_BAR;
                }
            } else {
                mix.countTobaccoForPurchase = mix.components.size;
                var countTobaccoInBarOrWithReplace = 0;
                //Проверяем каждый табак из Микса на наличие в баре и замен
                for (mixComponent in mix.components) {
                    currentTobacco = mixComponent.tobaccoRef!!;
                    if (barTobaccos.contains(currentTobacco)) {
                        currentTobacco.status = TobaccoStatus.CONTAIN_BAR;
                        countTobaccoInBarOrWithReplace++;
                        mix.countTobaccoForPurchase--;
                    } else {
                        //Ищем статус табака для указанного юзера (т.е. "в покупках" или "куплен")
                        currentTobacco.status = userTobaccosList
                                .firstOrNull() { tobacco -> tobacco.id == currentTobacco.id }?.status
                                ?: TobaccoStatus.NULL_VALUE;
                        //Ищем замены из бара
                        replacementList = barTobaccos.filter { tobacco ->
                            tobacco.mainTaste?.id == currentTobacco.mainTaste?.id
                        }.toMutableList();
                        //Если для  табака есть замена из бара,
                        // то увеличиваем счетчик для проверки статус микса на "Есть с заменой"
                        if (replacementList.isNotEmpty()) {
                            mixComponent.tobaccoReplacements = replacementList;
                            countTobaccoInBarOrWithReplace++;
                        }
                    }
                }
                //Если для всех табаков есть замены и/или частично есть в баре,
                // то статус микса - "с заменой" иначе статус микса - "докупить"
                mix.status =
                        if (countTobaccoInBarOrWithReplace == mix.components.size)
                            MixStatus.REPLACEMENT_BAR
                        else
                            MixStatus.PARTIAL_BAR;
            }
        }
        return mixesList;
    }

    fun searchByTagsAndLimitRating(text: String, ratingLimit: Double = mixRatingLimitValue): MutableList<Mix> {
        return mixSearch.searchTagsInMixes(text, ratingLimit);
    }

    fun generateMixCount(user: User, status: String?, strength: String?, taste: String?): Int {

        val mixes: MutableList<Mix>;

        val count: Int;
        var result: MutableList<Mix> = mutableListOf();

        if (status != null && status.isNotEmpty()) {

            if (strength != null && strength.isNotEmpty()) {

                if (taste != null && taste.isNotEmpty()) {

                    mixes = getAllForUserWithSearch(user);
                    result = mixes.stream()
                            .filter { i ->
                                i.status.title.equals(status)
                                        && i.strength == strength.toDouble()
                                        && i.tags.contains(taste)
                            }
                            .toList().toMutableList();
                } else {

                    mixes = getAllForUserWithSearch(user)
                    result = mixes.stream()
                            .filter { i ->
                                i.status.title.equals(status)
                                        && i.strength == strength.toDouble()
                            }
                            .toList().toMutableList()
                }
            } else {

                mixes = getAllForUserWithSearch(user)

                result = mixes.stream()
                        .filter { i -> i.status.title.equals(status) }
                        .toList().toMutableList()

            }
        }
        count = result.size;
        return count;
    }

    fun isExist(title: String): Boolean {
        return mixRepository.existsByTitle(title);
    }

    fun isExistBySourceUrl(sourceUrl: String): Boolean {
        return mixRepository.existsBySourceUrl(sourceUrl);
    }

    fun findOne(title: String): Mix? {
        return mixRepository.findByTitle(title);
    }

    fun getOne(mixId: Long): Mix? {
        return mixRepository.findById(mixId).orElse(null);
    }

    fun add(mix: Mix): Mix? {
        //TODO Решить вопрос с сохранением связных сущностей
        val componentMixList = mix.components;
        mix.tobaccoMixList = mutableListOf();

        val newMix = mixRepository.save(mix);
        //check mix creation
        if (newMix != null) {
            //fill components of mix
            for (component in componentMixList) {
                component.mixRef = newMix;
                if (saveMixComponent(component) == null) {
                    //TODO rollback addMix operation
                    return null
                };
            }
        }
        return newMix;
    }

    fun saveMixComponent(mixComponent: MixComponent): MixComponent? {
        return mixComponentRepository.save(mixComponent);
    }
}