package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.PurchaseRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserTobaccosRepository
import com.codemark.hookahmix.service.searchBuilder.TobaccoSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/** Сервис для работы с каталогом Табаков и "Мои табаки" для пользователя */
@Service
class TobaccoService @Autowired constructor(
        private val tobaccoRepository: TobaccoRepository,
        private val purchaseRepository: PurchaseRepository,
        private val userTobaccosRepository: UserTobaccosRepository,
        private val tobaccoSearch: TobaccoSearch,
        private val makerService: MakerService,
        private val tasteService: TasteService,
        private val purchaseService: PurchaseService) {

    /** Параметр, ограничивающий список записей  более данного значения */
    @Value("\${tobaccoRatingLimit}")
    var tobaccoRatingLimitValue: Double = 0.0;

//<editor-fold desc="ПУБЛИЧНЫЕ МЕТОДЫ">

    //<editor-fold desc="Получение записей">
    fun getOne(id: Long): Tobacco {
        return tobaccoRepository.getOne(id);
    }

    /** Возвращает табак совпадающий точно по наименованию и производителю */
    fun getOne(title: String, maker: Maker): Tobacco? {
        return tobaccoRepository.findByTitleIgnoreCaseAndMaker(title, maker);
    }

    fun getAll(): List<Tobacco> {
        return tobaccoRepository.findAll();
    }

    //TODO преобразовать метод и искать по ID maker
    fun getAllByMaker(makerTitle: String): List<Tobacco> {
        return tobaccoRepository.findAllByMaker(makerTitle);
    }

    /** Поиск табака с частично совпадающим наименованием и заданным производителем */
    fun searchAllByTitle(title: String, maker: Maker): MutableList<Tobacco> {
        val tobaccoList: MutableList<Tobacco> = mutableListOf();
        for (str in title.split(" ", "-", "_", ".", ",")) {
            tobaccoList.addAll(tobaccoRepository.findAllByTitleContainingIgnoreCaseAndMaker(str, maker));
        }
        return tobaccoList;
    }

    /** Поиск табака с частично совпадающим наименованием и заданным производителем */
    fun searchAllByTaste(taste: Taste, maker: Maker): MutableList<Tobacco> {
        return tobaccoRepository.findAllByTasteAndMaker(taste.id, maker.id);
    }

    /** Возвращает список ВСЕХ производителей с вложенным списком их табаков.
     * В табаках проставлен статус для указанного пользователя
     * @param user - Пользователь, для которого формируем статусы табаков
     */
    fun getMakersAndStatusTobaccosInCatalogForUser(user: User, searchQuery: String? = null): MutableList<Maker> {
        val makerList = if (searchQuery.isNullOrBlank()) {
            makerService.getAllSortedByTitle();
        } else {
            searchMakerCatalog(searchQuery);
        }

        //Переприсваиваем статусы табаков для списка "Мои табаки" (мой бар и корзина)
        val userTobaccos = getAllUserTobacco(user);
        getFilledStatusTobaccoMakerList(makerList.toMutableSet(), userTobaccos);
        return makerList;
    }

    fun getMakersAndStatusTobaccosInCatalogWithLimitRatingForUser(user: User, searchQuery: String? = null): MutableSet<Maker> {
        val tobaccoList = if (searchQuery.isNullOrBlank()) {
            getAllByLimitRatingAndSortedByTitle(tobaccoRatingLimitValue);
        } else {
            searchTobaccoCatalogByTitleAndLimitRating(searchQuery, tobaccoRatingLimitValue);
        }
        //Переприсваиваем статусы табаков для списка "Мои табаки" (мой бар и корзина)
        val userTobaccos = getAllUserTobacco(user);
        getFilledStatusTobaccoList(tobaccoList, userTobaccos);
        //Формируем "обертку" табаков в список производителей
        return createListOfMakersForTobaccosList(tobaccoList);
    }


    /** Возвращает список производителей с вложенным списком их табаков, из наличия в баре у пользователя.
     * В табаках проставлен статус для указанного пользователя
     * @param user - Пользователь, для которого формируем статусы табаков
     */
    fun getMakersAndStatusTobaccosInBarForUser(user: User, searchQuery: String? = null): MutableSet<Maker> {
        val userTobaccos = if (searchQuery.isNullOrBlank()) {
            getAllUserTobaccoInBar(user);
        } else {
            searchBarTobacco(searchQuery, user);
        }
        //Формируем "обертку" табаков в список производителей
        return createListOfMakersForTobaccosList(userTobaccos);
    }

    /** Возвращает список всех пользовательских табаков с любыми статусами */
    fun getAllUserTobacco(user: User): MutableList<Tobacco> {
        val userTobaccoList: MutableList<Tobacco> = mutableListOf()
        for (userTobaccoRelation in user.userTobaccos) {
            userTobaccoRelation.tobacco?.status = userTobaccoRelation.status;
            userTobaccoList.add(userTobaccoRelation.tobacco!!);
        }
        return userTobaccoList
    }

    /** Возвращает список всех пользовательских табаков со статусом "В баре" */
    fun getAllUserTobaccoInBar(user: User): MutableList<Tobacco> {
        val userTobaccoList: MutableList<Tobacco> = mutableListOf();
        for (userTobaccoRelation in user.userTobaccos
                .filter { userTobacco -> userTobacco.status == TobaccoStatus.CONTAIN_BAR }) {
            userTobaccoRelation.tobacco?.status = userTobaccoRelation.status;
            userTobaccoList.add(userTobaccoRelation.tobacco!!);
        }
        return userTobaccoList;
    }

    /** Возвращает список всех пользовательских табаков с статусом "В корзине" (покупках) */
    fun getAllUserTobaccoInCheckout(user: User): MutableList<Tobacco> {
        val userTobaccoList: MutableList<Tobacco> = mutableListOf()
        for (userTobaccoRelation in user.userTobaccos
                .filter { userTobacco -> userTobacco.status == TobaccoStatus.IN_CHECKOUT }) {
            userTobaccoRelation.tobacco?.status = userTobaccoRelation.status;
            userTobaccoList.add(userTobaccoRelation.tobacco!!);
        }
        return userTobaccoList
    }
    //</editor-fold>

    //<editor-fold desc="Добавление записей">
    /** Добавляет Табак в каталог Табаков */
    fun addOne(tobacco: Tobacco): Tobacco? {
        //TODO Решить вопрос с сохранением связных сущностей
        val tasteListForTobacco = tobacco.tasteList;
        tobacco.tasteList = mutableListOf();
        //TODO check Tobacco content or just save what come
        val newTobacco = tobaccoRepository.save(tobacco);
        if (newTobacco != null) {
            for (tobaccoTaste in tasteListForTobacco) {
                if (!tasteService.saveTobaccoTaste(tobaccoTaste, newTobacco)) return null;
            }
        }
        return newTobacco;
    }

    /** Добавление табака в бар */
    fun addOneInBar(tobaccoId: Long, user: User): Tobacco? {
        val tobacco: Tobacco? = tobaccoRepository.findById(tobaccoId).orElse(null);
        var userTobaccoInBar: UserTobacco? = null;
        if (tobacco != null) {
            //Поиск существующих записей в UserTobacco.
            userTobaccoInBar = userTobaccosRepository.findAllByUserAndTobacco(user, tobacco).firstOrNull();
            if (userTobaccoInBar != null) {
                if (userTobaccoInBar.status == TobaccoStatus.IN_CHECKOUT) {
                    //Если табак был в покупках и переходит в бар, значит его купили ->
                    // добавить в заказы и перезатереть статус в UserTobacco
                    purchaseService.addOne(Purchase(user, tobacco));
                }
            } else {
                userTobaccoInBar = UserTobacco(user, tobacco);
            }
            userTobaccoInBar.status = TobaccoStatus.CONTAIN_BAR;
            userTobaccoInBar = userTobaccosRepository.save(userTobaccoInBar);
        }
        return if (userTobaccoInBar != null) tobacco else null;
    }

    /** Добавление табака в корзину (покупки) */
    fun addOneInCheckout(tobaccoId: Long, user: User): Tobacco? {
        val tobacco: Tobacco? = tobaccoRepository.findById(tobaccoId).orElse(null);
        var userTobaccoInBar: UserTobacco? = null;
        if (tobacco != null) {
            //Поиск существующих записей в UserTobacco.
            userTobaccoInBar = userTobaccosRepository.findAllByUserAndTobacco(user, tobacco).firstOrNull();
            if (userTobaccoInBar != null) {
                //Если табак был в баре и его пытаются добавить в покупки -> запретить действие
                if (userTobaccoInBar.status == TobaccoStatus.CONTAIN_BAR) {
                    return null;
                }
            } else {
                userTobaccoInBar = UserTobacco(user, tobacco);
            }
            userTobaccoInBar.status = TobaccoStatus.IN_CHECKOUT;
            userTobaccoInBar = userTobaccosRepository.save(userTobaccoInBar);
        }
        return if (userTobaccoInBar != null) tobacco else null
    }

    /** Добавить табак в последние покупки */
    fun addOneInLatestPurchases(tobaccoId: Long, user: User): Boolean {
        val tobacco: Tobacco? = tobaccoRepository.findById(tobaccoId).orElse(null);
        if (tobacco != null) {
            //Поиск существующих записей в UserTobacco.
            if (!purchaseRepository.isExistLatestPurchases(user.id, tobacco.id)) {
                purchaseRepository.addInLatestPurchases(user.id, tobacco.id);
            }
            return true
        }
        return false
    }

    //</editor-fold>

    //<editor-fold desc="Удаление записей">
    /** Удаление табака из списка пользовательских табаков */
    fun deleteOneFromUserTobaccos(user: User, tobaccoId: Long): Boolean {
        val userTobacco: UserTobacco? =
                userTobaccosRepository.findAllByUserIdAndTobaccoId(user.id, tobaccoId).firstOrNull();
        if (userTobacco != null) {
            userTobaccosRepository.delete(userTobacco);
            return true;
        }
        return false;
    }

    /** Удалить табак из последних покупок */
    fun deleteOneFromLatestPurchases(user: User, tobaccoId: Long): Boolean {
        val tobacco: Tobacco? = tobaccoRepository.findById(tobaccoId).orElse(null);
        if (tobacco != null) {
            //Поиск существующих записей в UserTobacco.
            if (!purchaseRepository.isExistLatestPurchases(user.id, tobacco.id)) {
                purchaseRepository.deleteOneFromLatestPurchases(user.id, tobacco.id);
            }
            return true
        }
        return false
    }
    //</editor-fold>

    //<editor-fold desc="Поиск по записям">

    fun searchMakerCatalog(text: String): MutableList<Maker> {
        return tobaccoSearch.searchMakerCatalog(text);
    }

    fun searchTobaccoCatalogByTitleAndLimitRating(text: String, ratingLimit: Double): MutableList<Tobacco> {
        return tobaccoSearch.searchTobaccoCatalogByTitleAndLimitRating(text, ratingLimit);
    }

    fun searchBarTobacco(text: String, user: User): MutableList<Tobacco> {
        return searchBarTobaccoUser(text, getAllUserTobaccoInBar(user));
    }

    fun searchBarTobaccoUser(text: String, tobaccoList: MutableList<Tobacco>): MutableList<Tobacco> {
        return tobaccoSearch.searchBarTobacco(text, tobaccoList);
    }

    //</editor-fold>
//</editor-fold>

//<editor-fold desc="ВНУТРЕННИЕ МЕТОДЫ">

    private fun getAllByLimitRatingAndSortedByTitle(ratingLimit: Double = tobaccoRatingLimitValue): MutableList<Tobacco> {
        return tobaccoRepository.findAllByRatingAfterOrderByMaker(ratingLimit);
    }

    /** Вспомогательная функция-обертка для создания списка производителей
     * из переданного списка табаков tobaccoList
     */
    private fun createListOfMakersForTobaccosList(tobaccoList: MutableList<Tobacco>): MutableSet<Maker> {
        var makerList: MutableSet<Maker> = mutableSetOf();
        var currentMaker: Maker?;
        for (tobacco in tobaccoList) {
            currentMaker = makerList.firstOrNull { maker: Maker -> maker.id == tobacco.maker?.id }
            if (currentMaker == null) {
                currentMaker = tobacco.maker!!;
                currentMaker.tobaccos = mutableSetOf();
                makerList.add(currentMaker);
            }
            currentMaker.tobaccos.add(tobacco);
        }
        makerList = makerList.sortedBy(Maker::title).toMutableSet();
        for (maker in makerList) {
            maker.tobaccos = maker.tobaccos.sortedBy { tobacco -> tobacco.title }.toMutableSet();
        }
        return makerList;
    }

    /** Вспомогательная функция для заполнения статусов табака
     * из userTobaccoList извлекаем табаки и их статусы присваиваем соотв. табакам из tobaccoList
     */
    private fun getFilledStatusTobaccoList(tobaccoList: MutableList<Tobacco>, userTobaccoList: MutableList<Tobacco>) {
        //Идем по списку переданных табаков
        for (tobacco in userTobaccoList) {
            tobaccoList.firstOrNull { value: Tobacco -> value.id == tobacco.id }
                    ?.status = tobacco.status;
        }
    }

    /** Вспомогательная функция для заполнения статусов табака
     * из tobaccoList извлекаем табаки и их статусы присваиваем соотв. табакам из makerList.tobaccos
     */
    private fun getFilledStatusTobaccoMakerList(makerList: MutableSet<Maker>, tobaccoList: MutableList<Tobacco>) {
        //Идем по списку переданных табаков
        for (tobacco in tobaccoList) {
            makerList.firstOrNull { maker: Maker -> maker.id == tobacco.maker?.id }?.tobaccos
                    ?.firstOrNull { value: Tobacco -> value.id == tobacco.id }
                    ?.status = tobacco.status;
        }
        for (maker in makerList) {
            maker.tobaccos = maker.tobaccos.sortedBy { tobacco -> tobacco.title }.toMutableSet();
        }
    }

    /**
     * Подсчет времени выполнения переданной в параметре функции
     * @return Время выполнения in nanoseconds
     */
    private final fun countRunTime(observerFunction: () -> Any): Long {
        val start = System.nanoTime();
        observerFunction();
        val finish = System.nanoTime();
        return finish - start;
    }
//</editor-fold>
}

