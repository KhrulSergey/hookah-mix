package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.dto.DataParserInfoDto
import com.codemark.hookahmix.domain.dto.ParseStatus
import com.codemark.hookahmix.exception.MakerParsingException
import com.codemark.hookahmix.exception.TobaccoParsingException
import com.codemark.hookahmix.service.ImageService
import com.codemark.hookahmix.service.MakerService
import com.codemark.hookahmix.service.TasteService
import com.codemark.hookahmix.service.TobaccoService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.regex.Pattern
import kotlin.math.roundToInt


@Component
@PropertySource(ignoreResourceNotFound = false, value = ["classpath:parser.properties"])
class TobaccoParser @Autowired constructor(private var imageService: ImageService,
                                           private val tasteService: TasteService,
                                           private val makerService: MakerService,
                                           private val tobaccoService: TobaccoService) {


    @Value("\${url}")
    var targetUrl: String = "";

    @Value("\${selectMakersElements}")
    var makersElements: String = ""

    @Value("\${selectMakerTitle}")
    var makerTitleElement: String = "";

    @Value("\${selectMakerStrength}")
    var makerStrengthElement: String = "";

    @Value("\${selectMakerRating}")
    var makerRatingElement: String = "";

    @Value("\${selectMakerRatingCount}")
    var makerRatingCountElement: String = "";

    @Value("\${selectMakerImage}")
    var makerImageElement: String = "";

    @Value("\${selectMakerFoundingYear}")
    var makerFoundingYearElement: String = "";

    @Value("\${selectMakerDescription}")
    var makerDescriptionElement: String = "";

    @Value("\${tobaccosElements}")
    var tobaccosElements: String = "";

    @Value("\${selectTobaccoDescription}")
    var tobaccoDescriptionElement: String = ""

    @Value("\${selectTobaccoFullDescription}")
    var tobaccoFullDescriptionElement: String = ""

    @Value("\${selectTobaccoTitle}")
    var tobaccoTitleElement: String = "";

    @Value("\${selectTobaccoImage}")
    var tobaccoImageElement: String = "";

    @Value("\${selectTobaccoDetails}")
    var tobaccoDetailsElements: String = "";

    @Value("\${selectTobaccoRating}")
    var tobaccoRatingElement: String = "";

    @Value("\${selectTobaccoRatingCount}")
    var tobaccoRatingCountElement: String = "";

    @Value("\${selectTobaccoTasteList}")
    var tobaccoTasteListElements: String = "";

    var tobaccoRussianTasteElementText = "Русское название:";

    //Модель для хранения и передачи данных о результатах парсинга
    var tobaccoParserInfo: DataParserInfoDto<Tobacco> = DataParserInfoDto(status = ParseStatus.NOT_STARTED);

    /**Открытие URI WEB-страницы*/
    fun connectPage(target: String = targetUrl): Document? {
        var document: Document? = null;
        try {
            document = Jsoup.connect(target)
                    .timeout(0)
                    .get();
        } catch (exc: IOException) {
            val message = "Страницы $target не существует" + System.lineSeparator() +
                    exc.message + System.lineSeparator() + exc.cause
            tobaccoParserInfo.errorLog.add(message);
        }
        return document;
    }

    /** Распознавание ресурса со списком производителей->табаков */
    fun startParse(document: Document?, tobaccoCountNeeded: Int = 10): DataParserInfoDto<Tobacco> {
        tobaccoParserInfo.status = ParseStatus.IN_PROGRESS;
        tobaccoParserInfo.errorLog = mutableListOf();
        tobaccoParserInfo.dataList = mutableListOf();
        tobaccoParserInfo.warningLog = mutableListOf();
        //Итоговый список распознанных Миксов
        tobaccoParserInfo.dataList = mutableListOf();
        tobaccoParserInfo.sourceEntriesCount = 0;

        val makerListElement: Elements = document!!.select(makersElements);
        var makerUrl: String;

        for (makerElement in makerListElement) {
            if (tobaccoParserInfo.dataList.size >= tobaccoCountNeeded) break;
            try {
                makerUrl = makerElement.attr("href");
                //если не нашли ссылку на производителя -> ошибка
                if (makerUrl.isBlank()) {
                    throw MakerParsingException("Не найдена ссылка на производителя ${makerElement.text()}", null);
                }
                //Запуск обработки записей табаков со страницы Производителя
                val newTobaccoList: MutableList<Tobacco> = parseOneMaker(makerUrl, tobaccoCountNeeded);
                if (newTobaccoList.isNotEmpty()) {
                    tobaccoParserInfo.dataList.addAll(newTobaccoList);
                }
            } catch (exc: MakerParsingException) {
                tobaccoParserInfo.errorLog.add(exc.message!!);
            } catch (exc: Exception) {
                tobaccoParserInfo.errorLog.add("Неизвестная ошибка" + exc.message + "вызывана " + exc.cause);
            }
        }
        tobaccoParserInfo.status = ParseStatus.FINISHED;
        return tobaccoParserInfo;
    }

    @Throws(MakerParsingException::class)
    fun parseOneMaker(makerUrl: String, tobaccoCountNeeded: Int): MutableList<Tobacco> {
        var newMaker: Maker?;
        val savedMaker: Maker?;
        val tobaccoList: MutableList<Tobacco> = mutableListOf();
        var makerStrength: Double = 0.0;
        //Открываем детальную страницу производителя
        val makerPage: Document? = connectPage(makerUrl);
        if (makerPage == null) {
            throw MakerParsingException("Ошибка открытия детальной страницы производителя из источника $makerUrl", null);
        }
        /** Обработка данных о производителе */
        //Получаем наименование производителя
        val makerTitle = makerPage.selectFirst(makerTitleElement).text();
        if (makerTitle.isBlank()) {
            throw MakerParsingException("Ошибка получения наименования производителя из источника $makerUrl", null);
        }
        //Получаем крепость табаков производителя
        val attributeStrength = makerPage.selectFirst(makerStrengthElement);
        if (attributeStrength != null) {
            makerStrength = attributeStrength.text().toDouble();
        } else {
            tobaccoParserInfo.warningLog.add("Не получена крепость табака у производителя $makerTitle из источника.");
        }

        /** Проверяем наличие производителя в БД */
        newMaker = makerService.getOne(makerTitle);
        if (newMaker != null) {
            savedMaker = newMaker;
            tobaccoParserInfo.warningLog.add("Производитель ${newMaker.title} уже существует в БД. Исследуем его табаки.");
            println("Maker $savedMaker already exists in DB!, next its tobacco");
        } else {
            /** Заполняем данные о производителе со страницы */
            val makerImageUrl: String;
            var makerFoundingYear: String = "";
            var makerDescription: String = "";
            var makerRating: Double = 0.0;
            var makerRatingCount: Int = 0;
            //Получаем рейтинг производителя
            val attributeRating = makerPage.selectFirst(makerRatingElement);
            val attributeRatingCount = makerPage.selectFirst(makerRatingCountElement);
            if (attributeRating != null && attributeRatingCount != null) {
                makerRatingCount = searchNumbersInString(attributeRatingCount.text())?.roundToInt()!!;
                if (makerRatingCount > 0) {
                    makerRating = attributeRating.text().toDouble();
                }
            } else {
                tobaccoParserInfo.warningLog.add("Не получен рейтинг производителя $makerTitle из источника.");
            }
            //Получаем описание производителя
            val attributeMakerDescription = makerPage.selectFirst(makerDescriptionElement);
            if (attributeMakerDescription == null) {
                tobaccoParserInfo.warningLog.add(
                        "Не получено описание производителя $makerTitle из источника.");
            } else {
                makerDescription = attributeMakerDescription.text();
            }
            //Получаем изображения производителя
            val attributeImageMaker = makerPage.selectFirst(makerImageElement);
            if (attributeImageMaker == null) {
                throw MakerParsingException("Не определена ссылка на логотип у производителя $makerTitle из источника.", null);
            }
            makerImageUrl = attributeImageMaker.attr("style").substring(
                    attributeImageMaker.attr("style").indexOf('(') + 1,
                    attributeImageMaker.attr("style").indexOf(')'));
            //Получаем дату основания производителя
            val attributeFoundingYear = makerPage.selectFirst(makerFoundingYearElement);
            if (attributeFoundingYear != null) {
                makerFoundingYear = attributeFoundingYear.text()
                        .replace(("[^0-9]").toRegex(), "");
                println("Parser, year: ")
            } else {
                tobaccoParserInfo.warningLog.add("Не определен год основания у производителя $makerTitle из источника.");
            }

            /** Сохраняем изображение производителя */
            val makerImageName = imageService.uploadImage(makerImageUrl, makerTitle);
            if (makerImageName.isNullOrBlank()) {
                throw MakerParsingException("Не удалось сохранить файл логотип у производителя $makerTitle на диск.", null);
            }
            val makerImage: Image? = imageService.add(Image(makerImageName));
            if (makerImage == null) {
                val deleteResult = imageService.deleteUploadedFile(makerImageName);
                throw MakerParsingException("Не удалось сохранить логотип производителя $makerTitle в БД. Файл удален с диска: $deleteResult.", null);
            }

            newMaker = Maker();
            newMaker.title = makerTitle;
            newMaker.description = makerDescription;
            newMaker.foundingYear = makerFoundingYear;
            newMaker.image = makerImage;
            newMaker.strength = makerStrength;
            newMaker.rating = makerRating;
            savedMaker = makerService.add(newMaker);
            if (savedMaker == null) {
                throw MakerParsingException("Ошибка сохранения производителя $makerTitle в БД.", null);
            }
            println("Maker $savedMaker was saved, next its tobacco");
        }

        /** Обработка списка табаков */
        val tobaccoElements = makerPage.select(tobaccosElements) ?: throw MakerParsingException(
                "Ошибка получения списка табаков производителя ${savedMaker.title} в БД.", null);
        var tobaccoUrl: String;
        var newTobacco: Tobacco?;
        for (element in tobaccoElements) {
            //Проверяем условия выполнения парсера на кол-во рассмотренных и добавленных табаков
            if (tobaccoList.size >= tobaccoCountNeeded) break;
            tobaccoParserInfo.sourceEntriesCount++;
            try {
                /** Запуск обработки одного табака */
                tobaccoUrl = element.attr("href");
                if (tobaccoUrl.isBlank()) {
                    tobaccoParserInfo.warningLog.add(
                            "Не определена ссылка на страницу табака ${element.text()} у производителя ${savedMaker.title} из источника.");
                }
                newTobacco = parseOneTobacco(tobaccoUrl, savedMaker, makerStrength);
                if (newTobacco != null) {
                    tobaccoList.add(newTobacco);
                }
            } catch (exc: TobaccoParsingException) {
                tobaccoParserInfo.errorLog.add(exc.message!!);
            }
        }
        return tobaccoList;
    }

    @Throws(TobaccoParsingException::class)
    fun parseOneTobacco(tobaccoUrl: String, maker: Maker, strengthOfTobacco: Double): Tobacco? {
        var newTobacco: Tobacco?;
        val savedTobacco: Tobacco?;
        val tobaccoTitle: String;
        var tobaccoDescription: String = "";
        var tobaccoRating: Double? = null;
        var tobaccoRatingCount: Int = 0;
        var tobaccoImageUrl: String = "";
        val tobaccoTasteList: MutableList<Taste>;

        //Открываем детальную страницу табака
        val tobaccoPage: Document = connectPage(tobaccoUrl)
                ?: throw TobaccoParsingException("Ошибка открытия детальной страницы табака из источника $tobaccoUrl", null);
        /** Обработка данных о табаке*/
        val attributeTobaccoTitle = tobaccoPage.selectFirst(tobaccoTitleElement)
                ?: throw TobaccoParsingException("Ошибка получения наименования табака из источника $tobaccoUrl", null);
        tobaccoTitle = attributeTobaccoTitle.text();

        /** Проверяем существование табака в БД */
        newTobacco = tobaccoService.getOne(tobaccoTitle, maker);
        if (newTobacco != null) {
            savedTobacco = null;
            tobaccoParserInfo.warningLog.add("Табак ${newTobacco.title} уже существует в БД. ");
            println("Tobacco  ${newTobacco.title} already exists in DB!")
        } else {
            /** Заполняем данные о табаке со страницы */
            //Получаем описание
            val attributeTobaccoDescription = tobaccoPage.selectFirst(tobaccoDescriptionElement);
            if (attributeTobaccoDescription != null) {
                tobaccoDescription = attributeTobaccoDescription.text();
            } else {
                tobaccoParserInfo.warningLog.add("Не получено описание табака $tobaccoTitle из источника.");
            }
            //Получаем список вкусов
            tobaccoTasteList = parseTobaccoTastes(tobaccoPage, tobaccoTitle);
            //Получаем изображение
            val attributeTobaccoImage = tobaccoPage.selectFirst(tobaccoImageElement);
            if (attributeTobaccoImage != null) {
                tobaccoImageUrl = attributeTobaccoImage.attr("style").substring(
                        attributeTobaccoImage.attr("style").indexOf('(') + 1,
                        attributeTobaccoImage.attr("style").indexOf(')'));
            } else {
                tobaccoParserInfo.warningLog.add("Не получено изображение табака $tobaccoTitle из источника.");
            }
            //Получаем рейтинг табака
            val attributeRating = tobaccoPage.selectFirst(tobaccoRatingElement);
            val attributeRatingCount = tobaccoPage.selectFirst(tobaccoRatingCountElement);
            if (attributeRating != null && attributeRatingCount != null) {
                tobaccoRatingCount = searchNumbersInString(attributeRatingCount.text())?.roundToInt()!!;
                if (tobaccoRatingCount > 0) {
                    tobaccoRating = attributeRating.text().toDouble();
                }
            } else {
                tobaccoParserInfo.warningLog.add("Не получен рейтинг табака $tobaccoTitle из источника.");
            }
            // Сохраняем изображение табака
            val tobaccoImageName = imageService.uploadImage(tobaccoImageUrl, maker.title + "_" + tobaccoTitle);
            if (tobaccoImageName.isNullOrBlank()) {
                throw TobaccoParsingException("Не удалось сохранить изображение для табака $tobaccoTitle на диск.", null);
            }
            val tobaccoImage: Image? = imageService.add(Image(tobaccoImageName));
            if (tobaccoImage == null) {
                val deleteResult = imageService.deleteUploadedFile(tobaccoImageName);
                throw TobaccoParsingException("Не удалось сохранить изображение для табака в БД. Файл удален с диска: $deleteResult.", null);
            }

            /** Сохраняем табак */
            newTobacco = Tobacco();
            newTobacco.title = tobaccoTitle;
            newTobacco.description = tobaccoDescription;
            newTobacco.tasteList = tobaccoTasteList;
            newTobacco.mainTaste = tobaccoTasteList.firstOrNull();
            newTobacco.strength = strengthOfTobacco;
            newTobacco.rating = tobaccoRating;
            newTobacco.image = tobaccoImage;
            newTobacco.maker = maker;
            newTobacco.sourceUrl = tobaccoUrl;

            //Сохраняем табак в БД
            savedTobacco = tobaccoService.addOne(newTobacco);
            if (savedTobacco == null) {
                throw TobaccoParsingException("Ошибка сохранения  табака ${newTobacco.title} в БД", null);
            }
            println("Tobacco ${newTobacco.title} was saved in DB");
        }
        return savedTobacco;
    }

    /** Формируем вкусы со страницы описания табака */
    fun parseTobaccoTastes(tobaccoPage: Document, tobaccoTitle: String): MutableList<Taste> {
        val tobaccoTasteList: MutableList<Taste> = mutableListOf();
        var currentTasteTitle: String? = null;
        val tobaccoTasteTitleList: MutableList<String> = mutableListOf();
        var tasteNameIsTobaccoTitle = false;
        //Ищем список с описанием вкусов табака
        var attributeTobaccoTastes: Elements = tobaccoPage.select(tobaccoTasteListElements);
        if (attributeTobaccoTastes.isNotEmpty()) {
            for (element: Element in attributeTobaccoTastes) {
                if (element.text().isNotBlank()) {
                    //Формируем наименование вкуса
                    currentTasteTitle = element.text().trim();
                    tobaccoTasteTitleList.add(currentTasteTitle);
                }
            }
        } else {
            //Ищем поле с русским названием табака
            attributeTobaccoTastes = tobaccoPage.select(tobaccoFullDescriptionElement);
            if (attributeTobaccoTastes.isNotEmpty()) {
                for (element: Element in attributeTobaccoTastes) {
                    if (element.select("span").text().indexOf(tobaccoRussianTasteElementText) != -1) {
                        //Формируем наименование вкуса из названия табака
                        currentTasteTitle = element.text().substring(tobaccoRussianTasteElementText.length).trim();
                        tasteNameIsTobaccoTitle = true;
                        break;
                    }
                }
            }
            if (currentTasteTitle.isNullOrBlank()) {
                tobaccoParserInfo.warningLog.add("Не получен вкус табака $tobaccoTitle из источника.");
            }else{
                tobaccoTasteTitleList.add(currentTasteTitle);
            }
        }

        /** Формируем список вкусов из БД */
        var tobaccoTaste: Taste?;
        for (i in 0 until tobaccoTasteTitleList.size) {
            //Если присвоили вкусу имя как в названии табака
            if (tasteNameIsTobaccoTitle) {
                //Фильтруем вкусы и пытаемся найти совпадение по имени вкуса и наименованию табака
                tobaccoTaste = tasteService.searchAllByTitle(tobaccoTasteTitleList[i]).firstOrNull();
                if (tobaccoTaste != null) {
                    tobaccoTasteList.add(tobaccoTaste);
                    break;
                }
            }
            //Получаем вкус для табака из БД (существующий или новый)
            tobaccoTaste = tasteService.getOneTastes(tobaccoTasteTitleList[i]);
            if (tobaccoTaste == null) {
                //Если вкус не найден -> добавляем
                tobaccoTaste = tasteService.addTaste(Taste(tobaccoTasteTitleList[i]));
                if (tobaccoTaste == null) {
                    throw TobaccoParsingException("Ошибка сохранения вкуса $currentTasteTitle для табака $tobaccoTitle в БД.", null);
                }
            }
            tobaccoTasteList.add(tobaccoTaste);
        }
        return tobaccoTasteList;
    }

    /** Поиск первого числа в заданной строке */
    private fun searchNumbersInString(text: String): Double? {
        //Настраиваем регулярку для получения чисел из строки
        val pattern: Pattern = Pattern.compile("[-]?[0-9]+(.[0-9]+)?")
        val matcher = pattern.matcher(text);
        var value: Double? = null;
        if (matcher.find()) {
            value = matcher.group().toDouble();
        }
        return value;
    }
}