package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.PurchaseRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import com.codemark.hookahmix.repository.UserTobaccosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/** Сервис для работы с каталогом Табаков и "Мои табаки" для пользователя */
@Service
class TobaccoService @Autowired constructor(
        private val tobaccoRepository: TobaccoRepository,
        private val purchaseRepository: PurchaseRepository,
        private val makerService: MakerService,
        private val userTobaccosRepository: UserTobaccosRepository) {

//<editor-fold desc="ПУБЛИЧНЫЕ МЕТОДЫ">

    //<editor-fold desc="Получение записей">
    fun getOne(id: Long): Tobacco {
        return tobaccoRepository.getOne(id);
    }

    fun getOne(title: String, maker: Maker): Tobacco? {
        return tobaccoRepository.findByTitleAndMaker(title, maker);
    }

    fun getAll(): List<Tobacco> {
        return tobaccoRepository.findAll();
    }

    //TODO преобразовать метод и искать по ID maker
    fun getAllByMaker(makerTitle: String): List<Tobacco> {
        return tobaccoRepository.findAllByMaker(makerTitle);
    }

    /** Возвращает список ВСЕХ производителей с вложенным списком их табаков.
     * В табаках проставлен статус для указанного пользователя
     * @param user - Пользователь, для которого формируем статусы табаков
     */
    fun getMakersAndStatusTobaccosInCatalogForUser(user: User): MutableList<Maker> {
        val makerList = makerService.getAllSortedByTitle();
        //Фича отключена т.к. в списках табаков статус TobaccoStatus.PURCHASED не нужен бизнесу
        //val userPurchasedTobaccos = getAllUserLatestPurchasedTobacco(user);
        //getFilledStatusTobaccoMakerList(makerList, userPurchasedTobaccos);

        //Переприсваиваем статусы табаков для списка "Мои табаки" (мой бар и корзина)
        val userTobaccos = getAllUserTobacco(user);
        getFilledStatusTobaccoMakerList(makerList, userTobaccos);
        return makerList;
    }

    /** Возвращает список производителей с вложенным списком их табаков, из наличия в баре у пользователя.
     * В табаках проставлен статус для указанного пользователя
     * @param user - Пользователь, для которого формируем статусы табаков
     */
    fun getMakersAndStatusTobaccosInBarForUser(user: User): MutableSet<Maker> {
        val userTobaccos = getAllUserTobaccoInBar(user);
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
    fun getAllUserTobaccoInPurchases(user: User): MutableList<Tobacco> {
        val userTobaccoList: MutableList<Tobacco> = mutableListOf()
        for (userTobaccoRelation in user.userTobaccos
                .filter { userTobacco -> userTobacco.status == TobaccoStatus.IN_PURCHASES }) {
            userTobaccoRelation.tobacco?.status = userTobaccoRelation.status;
            userTobaccoList.add(userTobaccoRelation.tobacco!!);
        }
        return userTobaccoList
    }

    /** Возвращает список всех купленных (заказанных) табаков Пользователем */
    fun getAllFromLatestPurchases(user: User): MutableSet<Tobacco> {
        var purchasesTobaccos = tobaccoRepository.findLatestPurchases(user.id);
        return purchasesTobaccos.sortedBy { tobacco ->  tobacco.title}.toMutableSet();
    }
    //</editor-fold>

    //<editor-fold desc="Добавление записей">
    /** Добавляет Табак в каталог Табаков */
    fun addOne(tobacco: Tobacco): Tobacco? {
        //TODO check Tobacco content or just save what come
        val newTobacco = tobaccoRepository.save(tobacco);
        if (newTobacco.id == 0L) return null;
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
                if (userTobaccoInBar.status == TobaccoStatus.IN_PURCHASES) {
                    //Если табак был в покупках и переходит в бар, значит его купили -> добавить в заказы и перезатереть статус
                    addOneInLatestPurchases(tobaccoId, user);
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
    fun addOneInPurchases(tobaccoId: Long, user: User): Tobacco? {
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
            userTobaccoInBar.status = TobaccoStatus.IN_PURCHASES;
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
//</editor-fold>

//<editor-fold desc="ВНУТРЕННИЕ МЕТОДЫ">
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
        for (maker in makerList){
            maker.tobaccos = maker.tobaccos.sortedBy { tobacco -> tobacco.title }.toMutableSet();
        }
        return makerList;
    }

    /** Вспомогательная функция для заполнения статусов табака
     * из tobaccoList извлекаем табаки и их статусы присваиваем соотв. табакам из makerList.tobaccos
     */
    private fun getFilledStatusTobaccoMakerList(makerList: MutableList<Maker>, tobaccoList: MutableList<Tobacco>) {
        //Идем по списку переданных табаков
        for (tobacco in tobaccoList) {
            makerList.firstOrNull { maker: Maker -> maker.id == tobacco.maker?.id }?.tobaccos
                    ?.firstOrNull { value: Tobacco -> value.id == tobacco.id }
                    ?.status = tobacco.status;
        }
        for (maker in makerList){
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

