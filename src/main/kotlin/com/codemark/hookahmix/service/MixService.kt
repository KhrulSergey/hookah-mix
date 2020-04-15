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
                //Проверяем каждый табак из Микса на наличие в баре и замен
                for (mixComponent in mix.components) {
                    currentTobacco = mixComponent.tobacco!!;
                    if (barTobaccos.contains(currentTobacco)) {
                        currentTobacco.status = TobaccoStatus.CONTAIN_BAR;
                    } else {
                        //Ищем статус табака для указанного юзера
                        currentTobacco.status = userTobaccosList
                                .firstOrNull() { tobacco -> tobacco.id == mixComponent.tobacco?.id }?.status
                                ?: TobaccoStatus.NULL_VALUE;
                        //Ищем замены из бара
                        val replacementList = barTobaccos
                                .filter { tobacco -> tobacco.taste?.id == mixComponent.tobacco?.taste?.id };
                        //Если для каждого табака будет замена из бара, то статус микса меняем на "Есть с заменой"
                        if (replacementList.isNotEmpty()) {
                            currentTobacco.replacements = replacementList.toMutableList();
                            mix.status = MixSet.REPLACEMENT_BAR;
                        } else {
                            //Иначе статус - "Нужно докупить" для микса
                            mix.status = MixSet.PARTIAL_BAR;
                        }
                    }
                    currentTobaccoList.add(currentTobacco);
                }
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

    fun getOne(title: String): Mix {
        return mixRepository.findByTitle(title);
    }

    fun isExist(title: String): Boolean {
        return mixRepository.existsByTitle(title);
    }

    fun add(mix: Mix): Mix? {
        var newMix = Mix()
        //TODO check mix content or just save what come
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
        for (tobacco in mix.tobaccoMixList) {
            component = Component()
            component.mix = newMix;
            component.tobacco = tobacco;
            component.composition = tobacco.composition;
            newMix.components.add(component)
//            tobacco.components.add(component)
            if (!saveMixComponent(component)) {
                //TODO rollback addMix operation
                return null
            };
        }
        return newMix;
    }

    fun saveMixComponent(component: Component): Boolean {
        return (componentRepository.save(component).componentsId != 0L);
    }
}