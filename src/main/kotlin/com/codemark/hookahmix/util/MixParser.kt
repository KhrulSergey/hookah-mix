package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.domain.dto.DataParserInfoDto
import com.codemark.hookahmix.domain.dto.ParseStatus
import com.codemark.hookahmix.exception.MixParsingException
import com.codemark.hookahmix.service.MakerService
import com.codemark.hookahmix.service.MixService
import com.codemark.hookahmix.service.TasteService
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
                                       private var mixService: MixService,
                                       private var tasteService: TasteService) {


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
            //Запуск обработки одной страницы с миксами
            parseOnePage(mixUrlElements, pageNumber, mixCountNeeded);
            mixListPage = connectPage(nextPageUrl)!!;
            nextPageUrl = mixListPage.select(nextPageElement).attr("href");
            pageNumber++;
        }
        mixParserInfo.status = ParseStatus.FINISHED;
        return mixParserInfo;
    }

    fun parseOnePage(mixUrlElements: Elements, pageNumber: Int, mixCountNeeded: Int): MutableList<Mix> {
        var tobaccoList: MutableList<Tobacco> = mutableListOf();
        var newMix: Mix? = null;
        val mixList: MutableList<Mix> = mutableListOf();
        //Поля для хранения распознанных данных
        var mixFullTitle: String;
        var mixDescription: String;
        var mixTags: String;
        var mixFullCompositionText: String;
        for (item in mixUrlElements) {
            if (mixParserInfo.dataList.size >= mixCountNeeded) break;
            mixParserInfo.sourceEntriesCount++;
            try {
                newMix = Mix();
                tobaccoList = mutableListOf();
                /** Обрабатываем ссылку на источник микса в БД */
                //Получаем ссылку на описание микса
                newMix.sourceUrl = item.attr("href");
                if (newMix.sourceUrl.isNullOrBlank()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения ссылки на детальный микс из источника"), null);
                }
                //Получаем ссылку на источник микса в БД
                if (mixService.isExistBySourceUrl(newMix.sourceUrl!!)) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Микс со ссылкой ${newMix.sourceUrl} уже существует в БД"), null);
                }
                /** Получаем список табаков в миксе */
                mixFullTitle = item.select("h3").text();
                //Распознаем каждое сочетание "Maker1: Tobacco1, Tobacco2. Maker2: Tobacco3, Tobacco4."
                tobaccoList.addAll(parseMakerAndTobaccoFullText(mixFullTitle.split('.'), newMix, pageNumber));

                /** Формируем наименование микса */
                newMix.title = generateMixTitle(tobaccoList);
                // Проверяем наименование микса в БД
                if (mixService.isExist(newMix.title)) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Микс ${newMix.title} уже существует в БД"), null);
                }

                /**
                 * start parsing mix's page
                 */
                val mixPage: Document = connectPage(newMix.sourceUrl!!)!!;
                val mixContent: Elements = mixPage.select(mixContentElement);

                /** Формируем описание микса*/
                if (mixContent.isEmpty()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }
                mixDescription = mixContent.text();
                if (mixDescription.isBlank()) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }

                /** Формируем теги для микса */
                mixTags = generateMixTags(tobaccoList);

                /** Формируем композиию табаков в миксе*/
                mixFullCompositionText = "";
                //Поиск описания композиции в источнике
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
                //Распознаем композицию табаков
                if (parseMixCompositionText(mixFullCompositionText, tobaccoList, newMix, pageNumber) != 100) {
                    throw MixParsingException(generateErrorMessage(tobaccoList, newMix, pageNumber,
                            "Ошибка в композиции табаков в миксе"), null);
                }

                /** Заполняем данные микса (наименование выше) */
                newMix.description = mixDescription;
                newMix.tags = mixTags;
                newMix.tobaccoMixList = tobaccoList;

                /** Сохраняем микс в список*/
                val savedMix = mixService.add(newMix);
                if (savedMix != null) {
                    mixParserInfo.dataList.add(savedMix);
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

    /** Распознавание производителя и списка названий табаков, поиск соответствия в БД
     * Заполняем список табаков с оригинальным названием источника в состав микса
     * Возвращает список найденных в БД табаков */
    @Throws(MixParsingException::class)
    fun parseMakerAndTobaccoFullText(makerAndTobaccoFullTitle: List<String>, newMix: Mix, pageNumber: Int): MutableList<Tobacco> {
        var makerTitle: String;
        var makerList: MutableList<Maker>;
        var newTobacco: Tobacco? = null;
        val existedTobaccoList: MutableList<Tobacco> = mutableListOf();
        val originalTobaccoList: MutableList<Tobacco> = mutableListOf();

        for (string in makerAndTobaccoFullTitle) {
            //Получаем имя первого производителя
            val makerCharCount = string.indexOf(":");
            makerTitle = string.substring(0, makerCharCount).trim();
            if (makerTitle.isBlank()) {
                //Ошибка получения наименования производителя
                throw MixParsingException(generateErrorMessage(existedTobaccoList, newMix, pageNumber,
                        "Ошибка получения наименования производителя из источника"), null);
            }

            /** СПРАВОЧНИК ЗАМЕН НАЗВАНИЙ ПРОИЗВОДИТЕЛЕЙ ТАБАКА */
            when (makerTitle) {
                "Darkside" -> makerTitle = "Dark Side";
                "Al Waha" -> makerTitle = "Al-Waha";
                "Al mawardi" -> makerTitle = "Al-Mawardi";
                "Nakhla JTI (Japan Tobacco International)"-> makerTitle = "Nakhla";
                "Nakhla JTI"-> makerTitle = "Nakhla";
                "Sebetli"-> makerTitle = "Serbetli";
                "Serberli"-> makerTitle = "Serbetli";
                "Tabgiers"-> makerTitle = "Tangiers";
            }

            //Проверяем существование производителя в БД
            makerList = makerService.findAllByTitle(makerTitle);
            if (makerList.isEmpty()) {
                throw MixParsingException(generateErrorMessage(existedTobaccoList, newMix, pageNumber,
                        "Производитель $makerTitle не существует в БД"), null);
            }
            //Получаем названия табаков этого производителя (за минусом разделителя - двоеточия)
            val tobaccoTitles = string.substring(makerCharCount + 1).split(',');
            for (title in tobaccoTitles) {
                //Сохраняем оригинальное название табака в список
                originalTobaccoList.add(Tobacco(title = title.trim()));
                /** Проверяем существование табака в БД */
                //Поиск точного совпадения названия табака по всем найденным производителям
                newTobacco = findExactTobaccoInMakerListFromTitle(title.trim(), makerList);
                if (newTobacco == null) {
                    //Поиск частичного совпадения названия табака по всем найденным производителям
                    newTobacco = findSimilarTobaccoInMakerListFromTitle(title.trim(), makerList);
                    if (newTobacco == null) {
                        //Поиск совпадения названия табака из Микса по названиям вкусов
                        newTobacco = findTobaccoInMakerListFromTasteTitle(title.trim(), makerList);
                    }
                    //Если нашли табак по нечеткому совпадению, то считаем микс неоригинальным
                    if (newTobacco != null) newMix.isOriginal = false;
                }
                if (newTobacco == null) {
                    throw MixParsingException(generateErrorMessage(existedTobaccoList, newMix, pageNumber,
                            "Табак ${title.trim()} не существует в БД"), null);
                }
                existedTobaccoList.add(newTobacco);
            }
        }
        newMix.tobaccoMixList = originalTobaccoList;
        return existedTobaccoList;
    }

    /** Поиск табака в БД по точному совпадения названия по всем найденным производителям */
    private fun findExactTobaccoInMakerListFromTitle(tobaccoTitle: String, makerList: MutableList<Maker>): Tobacco? {
        var newTobacco: Tobacco? = null;
        for (maker in makerList) {
            newTobacco = tobaccoService.getOne(tobaccoTitle, maker);
            if (newTobacco != null) break;
        }
        return newTobacco;
    }

    /** Поиск табака в БД по частичному совпадению названия табака по всем найденным производителям */
    private fun findSimilarTobaccoInMakerListFromTitle(tobaccoTitle: String, makerList: MutableList<Maker>): Tobacco? {
        var newTobacco: Tobacco? = null;
        for (maker in makerList) {
            newTobacco = tobaccoService.searchAllByTitle(tobaccoTitle, maker).firstOrNull();
            if (newTobacco != null) break;
        }
        return newTobacco;
    }

    /** Поиск табака в БД по его вкусу по всем найденным производителям */
    private fun findTobaccoInMakerListFromTasteTitle(tasteTitle: String, makerList: MutableList<Maker>): Tobacco? {
        var newTobacco: Tobacco? = null;
        val tobaccoTasteList = tasteService.searchAllByTitle(tasteTitle);
        for (tobaccoTaste in tobaccoTasteList) {
            for (maker in makerList) {
                newTobacco = tobaccoService.searchAllByTaste(tobaccoTaste, maker).firstOrNull();
                if (newTobacco != null) break;
            }
            if (newTobacco != null) break;
        }
        return newTobacco;
    }


    /** Извлекаем числовые соотношения табаков из строки описания mixFullCompositionText
     * Дополнительно формируем крепость микса на основе соотношений табаков в миксе */
    fun parseMixCompositionText(mixFullCompositionText: String, existedTobaccoList: MutableList<Tobacco>,
                                newMix: Mix, pageNumber: Int): Int {
        /** Извлекаем числовые соотношения табаков из строки */
        var startTobaccoComposition: Number;
        var endTobaccoComposition: Number;
        var mixComposition: String;
        var mixSumComposition = 0;
        var mixStrength = 0.0;
        var currentTobacco: Tobacco?;
        val originalNamedTobaccoList: MutableList<Tobacco> = newMix.tobaccoMixList;
        //Настраиваем регулярку для получения чисел из строки
        val pat: Pattern = Pattern.compile("[-]?[0-9]+(.[0-9]+)?")
        var matcher: Matcher;
        //Ищем оригинальное название табака из источника в описании композиции микса
        for (i in 0 until originalNamedTobaccoList.size) {
            existedTobaccoList[i].composition = 0;
            currentTobacco = originalNamedTobaccoList[i];
            startTobaccoComposition = mixFullCompositionText.indexOf(currentTobacco.title.toLowerCase());
            //Проверяем найден ли Табак в описании композиции
            if (startTobaccoComposition == -1) {
                //Новый warning
                mixParserInfo.warningLog.add(generateWarningMessage(newMix, pageNumber,
                        "В композиции микса не найден компонент ${currentTobacco.title}"));
            } else {
                endTobaccoComposition = mixFullCompositionText.indexOfAny(charArrayOf('.', ',', '\n'), startTobaccoComposition);
                mixComposition = mixFullCompositionText.substring(
                        startTobaccoComposition + currentTobacco.title.length, endTobaccoComposition);
                //Получаем соотношение текущего табака через регулярку
                matcher = pat.matcher(mixComposition);
                if (matcher.find()) {
                    existedTobaccoList[i].composition = matcher.group().toInt();
                    mixSumComposition += existedTobaccoList[i].composition;
                    // Дополнительно формируем крепость микса
                    mixStrength += existedTobaccoList[i].strength * existedTobaccoList[i].composition / 100;
                }
            }
        }
        //Для случая незаполнения композиции для табаков - заполняем его сами равноценно
        if (mixSumComposition in 1..99) {
            val listOfNullCompositionTobacco: MutableList<Int> = mutableListOf();
            for (i in 0 until existedTobaccoList.size) {
                if (existedTobaccoList[i].composition == 0) {
                    listOfNullCompositionTobacco.add(i);
                }
            }
            if (listOfNullCompositionTobacco.isNotEmpty()) {
                val deltaComposition = (100 - mixSumComposition) / listOfNullCompositionTobacco.size;
                for (tobaccoIndex in listOfNullCompositionTobacco) {
                    existedTobaccoList[tobaccoIndex].composition = deltaComposition;
                    mixStrength += existedTobaccoList[tobaccoIndex].strength * deltaComposition / 100;
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

    /** Возвращает список тегов черезя запятую для микса путем извлечения вкусов табаков в миксе*/
    fun generateMixTags(tobaccoList: MutableList<Tobacco>): String {
        val mixTasteSet: MutableSet<Taste> = mutableSetOf();
        for (tobacco in tobaccoList) {
            mixTasteSet.addAll(tobacco.tasteList);
        }
        return mixTasteSet.joinToString { taste -> taste.title };
    }

    /** Формирует наименование микса из названий табаков */
    fun generateMixTitle(tobaccoList: MutableList<Tobacco>): String {
        var mixTitle = "";
        val delimiter = " - ";
        for (tobacco in tobaccoList) {
            mixTitle += tobacco.title + delimiter;
        }
        return mixTitle.substring(0, mixTitle.length - delimiter.length);
    }

    /** Формируем текст ошибки распознавания микса */
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

    /** Формируем текст предупреждения распознавания микса */
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