package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.dto.DataParserInfoDto
import com.codemark.hookahmix.domain.dto.ParseStatus
import com.codemark.hookahmix.exception.MixParsingException
import com.codemark.hookahmix.service.MakerService
import com.codemark.hookahmix.service.MixService
import com.codemark.hookahmix.service.TobaccoService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt


@Component
@PropertySource("classpath:parser-mixes.properties")
class MixParser @Autowired constructor(private var tobaccoService: TobaccoService,
                                       private var makerService: MakerService,
                                       private var mixService: MixService) {


    @Value("\${sourceMixesUrl}")
    var targetUrl: String = "";

    @Value("\${selectMixURI}")
    var mixUriElement: String = ""

    @Value("\${selectMixContent}")
    var mixContentElement: String = "";

    var mixCompositionLabelText: String = "Соотношение табаков";

    @Value("\${selectNextPage}")
    var nextPageElement: String = "";

    //Модель для хранения и передачи данных о результатах парсинга
    var mixParserInfo: DataParserInfoDto<Mix> = DataParserInfoDto(status = ParseStatus.NOT_STARTED);

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
            mixParserInfo.errorLog.add(message);
        }
        return document;
    }

    /** Распознавание ресурса со списком миксов */
    fun startParse(document: Document?, mixCountNeeded: Int): DataParserInfoDto<Mix> {
        mixParserInfo.status = ParseStatus.IN_PROGRESS;
        mixParserInfo.errorLog = mutableListOf();
        mixParserInfo.dataList = mutableListOf();
        mixParserInfo.warningLog = mutableListOf();
        //Итоговый список распознанных Миксов
        mixParserInfo.dataList = mutableListOf();
        mixParserInfo.sourceEntriesCount = 0;

        /**Страница со списком из 10 миксов */
        var mixListPage: Document = document!!;
        //Получаем элемент "Далее" для навигации по пагинации
        var nextPageUrl: String = mixListPage.select(nextPageElement).attr("href");
        var pageNumber = 1;

        /**Проверяем режим остановки парсера */
        while (nextPageUrl.isNotBlank() && mixParserInfo.dataList.size < mixCountNeeded) {
            val mixUrlElements: Elements = mixListPage.select(mixUriElement);
            mixParserInfo.sourceEntriesCount += mixUrlElements.size;
            //Запуск обработки одной страницы с миксами
            val newMixList = parseOnePage(mixUrlElements, pageNumber);
            if (newMixList.isNotEmpty()) {
                mixParserInfo.dataList.addAll(newMixList);
            }
            mixListPage = connectPage(nextPageUrl)!!;
            nextPageUrl = mixListPage.select(nextPageElement).attr("href");
            pageNumber++;
        }
        mixParserInfo.status = ParseStatus.FINISHED;
        return mixParserInfo;
    }

    fun parseOnePage(mixUrlElements: Elements, pageNumber: Int): MutableList<Mix> {
        var newMaker: Maker?;
        var newTobacco: Tobacco?;
        val tobaccoList: MutableList<Tobacco> = mutableListOf();
        var newMix: Mix? = null;
        val mixList: MutableList<Mix> = mutableListOf();
        //Поля для хранения распознанных данных
        var mixFullTitle: String;
        var makerTitle: String;
        var mixDescription: String;
        var mixFullCompositionText: String;
        for (item in mixUrlElements) {
            try {
                newMix = Mix();
                tobaccoList.clear();
                //Получаем ссылку на описание микса
                newMix.sourceUrl = item.attr("href");
                mixFullTitle = item.select("h3").text();
                for (string in mixFullTitle.split('.')) {
                    //Получаем имя первого производителя
                    val makerCharCount = string.indexOf(":");
                    makerTitle = string.substring(0, makerCharCount).trim();
                    if (makerTitle.isBlank()) {
                        //Ошибка получения наименования производителя
                        throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                                "Ошибка получения наименования производителя из источника"), null);
                    }
                    //Проверяем существование производителя в БД
                    newMaker = makerService.getOne(makerTitle);
                    if (newMaker == null) {
                        throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                                "Производитель $makerTitle не существует в БД"), null);
                    }

                    //Получаем названия табаков этого производителя (за минусом двоеточия)
                    val tobaccoTitles = string.substring(makerCharCount + 1).split(',');
                    for (title in tobaccoTitles) {
                        //Проверить существование табака в БД
                        newTobacco = tobaccoService.getOne(title.trim(), newMaker);
                        if (newTobacco == null) {
                            throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                                    "Табак ${title.trim()} не существует в БД"), null);
                        }
                        tobaccoList.add(newTobacco);
                    }
                }
                //Формируем наименование микса
                newMix.title = generateMixTitle(tobaccoList);
                /** Проверяем наименование микса в БД */
                if (mixService.isExist(newMix.title)) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Микс ${newMix.title} уже существует в БД"), null);
                }

                /**
                 * start parsing mix's page
                 */
                val mixPage: Document = connectPage(newMix.sourceUrl)!!;
                val mixContent: Elements = mixPage.select(mixContentElement);

                /** Формируем описание микса*/
                if (mixContent.isEmpty()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }
                mixDescription = mixContent.first().text();
                if (mixDescription.isBlank()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }


                /** Формируем композиию табаков в миксе*/
                mixFullCompositionText = "";
                for (i in 1 until mixContent.size) {
                    if (mixContent[i].select("strong").text().indexOf(mixCompositionLabelText) != -1) {
                        mixFullCompositionText = mixContent[i].text();
                        break;
                    }
                }
                if (mixFullCompositionText.isBlank()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения композиции табаков в миксе из источника"), null);
                }

                if (parseMixCompositionText(mixFullCompositionText, tobaccoList, newMix, pageNumber) != 100) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка в композиции табаков в миксе"), null);
                }

                newMix.description = mixDescription;
                newMix.tobaccoMixList = tobaccoList;
                val savedMix = mixService.add(newMix);
                if (savedMix != null) {
                    mixList.add(savedMix);
                } else {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка сохранения микса в БД"), null);
                }
            } catch (exc: MixParsingException) {
                mixParserInfo.errorLog.add(exc.message!!);
                continue;
            } catch (exc: Exception) {
                val message = generateErrorMessage(tobaccoList, newMix, pageNumber,
                        "!!--Неизвестная ошибка--!!");
                mixParserInfo.errorLog.add(message + "\n" + exc.message + "\n" + exc.cause);
                continue;
            }
        }
        return mixList;
    }

    /** Извлекаем числовые соотношения табаков из строки описания mixFullCompositionText*/
    fun parseMixCompositionText(mixFullCompositionText: String, tobaccoList: MutableList<Tobacco>,
                                newMix: Mix, pageNumber: Int): Int {
        /** Извлекаем числовые соотношения табаков из строки */
        var startTobaccoComposition: Number;
        var endTobaccoComposition: Number;
        var mixComposition: String;
        var mixSumComposition = 0;
        var mixStrength = 0.0;
        //Настраиваем регулярку для получения чисел из строки
        val pat: Pattern = Pattern.compile("[-]?[0-9]+(.[0-9]+)?")
        var matcher: Matcher;
        //Ищем название табака в описании композиции микса
        for (tobacco in tobaccoList) {
            startTobaccoComposition = mixFullCompositionText.indexOf(tobacco.title.toLowerCase());
            //Проверяем найден ли Табак в описании композиции
            if (startTobaccoComposition == -1) {
                //Новый warning
                mixParserInfo.warningLog.add(generateWarningMessage(newMix, pageNumber,
                        "В композиции микса не найден компонент ${tobacco.title}"));
            } else {
                endTobaccoComposition = mixFullCompositionText.indexOfAny(charArrayOf('.', ',', '\n'), startTobaccoComposition);
                mixComposition = mixFullCompositionText.substring(
                        startTobaccoComposition + tobacco.title.length, endTobaccoComposition);
                //Получаем соотношение текущего табака через регулярку
                matcher = pat.matcher(mixComposition);
                if (matcher.find()) {
                    tobacco.composition = matcher.group().toInt();
                    mixSumComposition += tobacco.composition;
                    mixStrength += tobacco.strength * tobacco.composition / 100;
                }
            }
        }

        //Для случая незаполнения композиции для табаков - заполняем его сами равноценно
        if (mixSumComposition in 1..99) {
            val listOfNullCompositionTobacco: MutableList<Int> = mutableListOf();
            for (i in 0 until tobaccoList.size) {
                if (tobaccoList[i].composition == 0) {
                    listOfNullCompositionTobacco.add(i);
                }
            }
            if (listOfNullCompositionTobacco.isNotEmpty()) {
                val deltaComposition = (100 - mixSumComposition) / listOfNullCompositionTobacco.size;
                for (tobaccoIndex in listOfNullCompositionTobacco) {
                    tobaccoList[tobaccoIndex].composition = deltaComposition;
                    mixStrength +=  tobaccoList[tobaccoIndex].strength * deltaComposition / 100;
                    mixSumComposition += deltaComposition;
                }
            }
            //Новый warning
            mixParserInfo.warningLog.add(generateWarningMessage(newMix, pageNumber,
                    "Пытаемся восстановить пропорции для проблемных табаков. Результат: " +
                            if (mixSumComposition == 100) "успешно" else "ошибка"));
        }

        //Заполняем Крепость микса из данных о табаках
        newMix.strength = mixStrength.roundToInt();
        return mixSumComposition;
    }

    fun parseMixDetailPage(mixUrlElements: Elements, pageNumber: Int): MutableList<Mix> {
        return mutableListOf();
    }

    fun generateMixTitle(tobaccoList: MutableList<Tobacco>): String {
        var mixTitle = "";
        val delimiter = "- ";
        for (tobacco in tobaccoList) {
            mixTitle += tobacco.title + delimiter;
        }
        return mixTitle.substring(0, mixTitle.length - delimiter.length);
    }

    private fun generateErrorMessage(tobaccoList: MutableList<Tobacco>?, mix: Mix?, pageNumber: Int, message: String?): String {
        var errorMessage = "!-------------New Error-----------!" + System.lineSeparator();
        errorMessage += "Error parsing on $pageNumber page: mix '${mix?.title}' in URL:" +
                "${System.lineSeparator()}${mix?.sourceUrl}" + System.lineSeparator();
        errorMessage += "Message: $message" + System.lineSeparator();
        if (tobaccoList != null) {
            for (tobacco: Tobacco in tobaccoList) {
                errorMessage += "$tobacco" + System.lineSeparator();
            }
        }
        return errorMessage;
    }

    private fun generateWarningMessage(mix: Mix?, pageNumber: Int, message: String?): String {
        var warningMessage = "!-------------New Warning-----------!" + System.lineSeparator();
        if (mix != null) {
            warningMessage += "Problem on $pageNumber page: ${mix.title} in URL:" +
                    "${System.lineSeparator()}${mix.sourceUrl}" + System.lineSeparator();
        }
        warningMessage += "Message: $message" + System.lineSeparator();
        return warningMessage;
    }
}