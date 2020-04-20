package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.ComponentRepository
import com.codemark.hookahmix.repository.MixRepository
import com.codemark.hookahmix.service.searchBuilder.MixSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class MixService @Autowired constructor(
        private val mixRepository: MixRepository,
        private var tobaccoService: TobaccoService,
        private val componentRepository: ComponentRepository,
        private val mixSearch: MixSearch) {

    //TODO Удалить неиспользуемые методы
    // Отсортировать методы

    fun getAll(): MutableList<Mix> {
        return mixRepository.findAll().toMutableList();
    }

    /** Возвращает список миксов и его компонентов, с проставленными статусами согласно наличия табаков у Пользователя
     *  Также осуществляется поиск по миксам при необходимости*/
    fun getAllForUserWithSearch(user: User, searchQuery: String? = null): MutableList<Mix> {
        val mixesList = if (searchQuery.isNullOrBlank()) {
            getAll();
        } else {
            search(searchQuery);
        }
        val userTobaccosList = tobaccoService.getAllUserTobacco(user);
        val barTobaccos = tobaccoService.getAllUserTobaccoInBar(user);

        var currentTobaccoList: MutableList<Tobacco>;
        var currentTobacco: Tobacco?;
        for (mix in mixesList) {
            currentTobaccoList = mutableListOf();
            //Если все табаки есть в баре у пользователя, то микс со статусом MixSet.MATCH_BAR
            //а табаки со статусом TobaccoStatus.CONTAIN_BAR
            if (barTobaccos.containsAll(mix.tobaccoMixList)) {
                mix.status = MixSet.MATCH_BAR;
                for (component in mix.components) {
                    currentTobacco = component.tobacco!!;
                    currentTobacco.status = TobaccoStatus.CONTAIN_BAR;
                    currentTobaccoList.add(currentTobacco);
                }
            } else {
                var countTobaccoInBarOrWithReplace = 0;
                //Проверяем каждый табак из Микса на наличие в баре и замен
                for (mixComponent in mix.components) {
                    currentTobacco = mixComponent.tobacco!!;
                    if (barTobaccos.contains(currentTobacco)) {
                        currentTobacco.status = TobaccoStatus.CONTAIN_BAR;
                        countTobaccoInBarOrWithReplace++;
                    } else {
                        //Ищем статус табака для указанного юзера (т.е. "в покупках" или "куплен")
                        currentTobacco.status = userTobaccosList
                                .firstOrNull() { tobacco -> tobacco.id == mixComponent.tobacco?.id }?.status
                                ?: TobaccoStatus.NULL_VALUE;
                        //Ищем замены из бара
                        val replacementList = barTobaccos
                                .filter { tobacco -> tobacco.mainTaste?.id == mixComponent.tobacco?.mainTaste?.id };
                        //Если для каждого табака будет замена из бара, то статус микса меняем на "Есть с заменой"
                        if (replacementList.isNotEmpty()) {
                            currentTobacco.replacements = replacementList.toMutableList();
                            countTobaccoInBarOrWithReplace++;
                        }
                    }
                    currentTobaccoList.add(currentTobacco);
                }
                //Если для всех табаков есть замены и/или частично есть в баре,
                // то статус микса "с заменой" иначе "докупить"
                mix.status = if (countTobaccoInBarOrWithReplace == currentTobaccoList.size)
                    MixSet.REPLACEMENT_BAR
                else MixSet.PARTIAL_BAR;
                //Формируем список табаков-компонентов в миксе с заполненными данными
                mix.tobaccoMixList = currentTobaccoList;
            }
        }
        return mixesList;
    }

    fun search(text: String): MutableList<Mix> {
        return mixSearch.searchMixes(text);
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
                                        && i.strength == Integer.parseInt(strength)
                                        && i.tags.contains(taste)
                            }
                            .toList().toMutableList();

                } else {


                    mixes = getAllForUserWithSearch(user)
                    result = mixes.stream()
                            .filter { i ->
                                i.status.title.equals(status)
                                        && i.strength == Integer.parseInt(strength)
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

    fun getOne(title: String): Mix? {
        return mixRepository.findByTitle(title);
    }

    fun add(mix: Mix): Mix? {
        //TODO Решить вопрос с сохранением связных сущностей
        val tobaccoMixList = mix.tobaccoMixList;
        mix.tobaccoMixList = mutableListOf();

        val newMix = mixRepository.save(mix);
        //check mix creation
        if (newMix != null) {
            //fill components of mix
            var component: Component;
            for (tobacco in tobaccoMixList) {
                component = Component()
                component.mix = newMix;
                component.composition = tobacco.composition;
                component.tobacco = tobacco;
                newMix.components.add(component)
                if (saveMixComponent(component) == null) {
                    //TODO rollback addMix operation
                    return null
                };
            }
        }
        return newMix;
    }

    fun saveMixComponent(component: Component): Component? {
        return componentRepository.save(component);
    }
}