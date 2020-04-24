package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.*
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
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt


@org.springframework.stereotype.Component
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
        var newMix: Mix? = null;
        val mixList: MutableList<Mix> = mutableListOf();
        var mixComponentList: MutableList<MixComponent> = mutableListOf();
        //Служебные поля для хранения распознанных данных
        var mixFullTitle: String;
        var mixDescription: String;
        var mixTags: String;
        var mixFullCompositionText: String;
        var originalNamedTobaccoList: MutableList<Tobacco>;
        for (item in mixUrlElements) {
            if (mixParserInfo.dataList.size >= mixCountNeeded) break;
            mixParserInfo.sourceEntriesCount++;
            try {
                newMix = Mix();
                mixComponentList = mutableListOf();
                /** Обрабатываем ссылку на источник микса в БД */
                //Получаем ссылку на описание микса
                newMix.sourceUrl = item.attr("href");
                if (newMix.sourceUrl.isNullOrBlank()) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка получения ссылки на детальный микс из источника"), null);
                }
                //Получаем ссылку на источник микса в БД
                if (mixService.isExistBySourceUrl(newMix.sourceUrl!!)) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Микс со ссылкой ${newMix.sourceUrl} уже существует в БД"), null);
                }
                /** Получаем список табаков в миксе */
                mixFullTitle = item.select("h3").text();
                originalNamedTobaccoList = mutableListOf();
                //Распознаем каждое сочетание "Maker1: Tobacco1, Tobacco2. Maker2: Tobacco3, Tobacco4."
                mixComponentList.addAll(
                        parseMakerAndTobaccoFullText(mixFullTitle.split('.'), originalNamedTobaccoList,
                                newMix, pageNumber)
                );

                /** Формируем наименование микса */
                newMix.title = generateMixTitle(mixComponentList);
                // Проверяем наименование микса в БД
                if (mixService.isExist(newMix.title)) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Микс ${newMix.title} уже существует в БД"), null);
                }

                /**
                 * start parsing mix's page
                 */
                val mixPage: Document = connectPage(newMix.sourceUrl!!)!!;
                val mixContent: Elements = mixPage.select(mixContentElement);

                /** Формируем описание микса*/
                if (mixContent.isEmpty()) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }
                mixDescription = mixContent.text();
                if (mixDescription.isBlank()) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка получения описания микса из источника"), null);
                }
                /** Формируем теги для микса */
                mixTags = generateMixTags(mixComponentList);

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
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка получения композиции табаков в миксе из источника"), null);
                }
                //Распознаем композицию табаков
                val summaryMixComposition = parseMixCompositionText(mixFullCompositionText, originalNamedTobaccoList,
                        mixComponentList, newMix, pageNumber).toInt();
                if (summaryMixComposition != 100) {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка в композиции табаков в миксе"), null);
                }

                /** Формируем крепость и рейтинг переданного микса внутри метода из переданного списка Компонентов*/
                generateMixStrengthAndRating(mixComponentList, newMix);

                /** Заполняем данные микса (наименование заполняется выше) */
                newMix.description = mixDescription;
                newMix.tags = mixTags;
                newMix.components = mixComponentList;

                /** Сохраняем микс в список*/
                val savedMix = mixService.add(newMix);
                if (savedMix != null) {
                    mixParserInfo.dataList.add(savedMix);
                    mixList.add(savedMix);
                } else {
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Ошибка сохранения микса в БД"), null);
                }
            } catch (exc: MixParsingException) {
                mixParserInfo.errorLog.add(exc.message!!);
                continue;
            } catch (exc: Exception) {
                val message = generateErrorMessage(mixComponentList, newMix, pageNumber,
                        "!!--Неизвестная ошибка--!!");
                mixParserInfo.errorLog.add(message + "\n" + exc.message + "\n" + exc.cause);
                continue;
            }
        }
        return mixList;
    }

    /** Расчитывает крепость и рейтинг для переданного микса из списка компонентов */
    private fun generateMixStrengthAndRating(componentList: MutableList<MixComponent>, newMix: Mix) {
        var currentMixRating = 0.0;
        var currentMixStrength = 0.0;
        var currentTobaccoRating: Double;
        var currentTobaccoStrength: Double;
        for (component in componentList) {
            currentTobaccoRating = 0.0;
            currentTobaccoStrength = 0.0;
            if (component.tobaccoRef?.rating != null) currentTobaccoRating = component.tobaccoRef?.rating!!;
            if (component.tobaccoRef?.strength != null) currentTobaccoStrength = component.tobaccoRef?.strength!!;
            currentMixRating += currentTobaccoRating * component.composition!!;
            currentMixStrength += currentTobaccoStrength * component.composition!!;
        }
        newMix.strength = currentMixStrength / 100;
        if (currentMixRating > 0) newMix.rating = currentMixRating / 100;
    }

    /** Распознавание производителя и списка названий табаков, поиск соответствия в БД
     * Заполняем список табаков с оригинальным названием источника в состав микса
     * Возвращает список найденных в БД табаков */
    @Throws(MixParsingException::class)
    fun parseMakerAndTobaccoFullText(makerAndTobaccoFullTitle: List<String>,
                                     originalNamedTobaccoList: MutableList<Tobacco>, newMix: Mix,
                                     pageNumber: Int): MutableList<MixComponent> {
        val mixComponentList: MutableList<MixComponent> = mutableListOf();
        var makerTitle: String;
        var makerList: MutableList<Maker>;
        var newTobacco: Tobacco?;

        for (string in makerAndTobaccoFullTitle) {
            //Получаем имя первого производителя
            val makerCharCount = string.indexOf(":");
            makerTitle = string.substring(0, makerCharCount).trim();
            if (makerTitle.isBlank()) {
                //Ошибка получения наименования производителя
                throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                        "Ошибка получения наименования производителя из источника"), null);
            }

            /** СПРАВОЧНИК ЗАМЕН НАЗВАНИЙ ПРОИЗВОДИТЕЛЕЙ ТАБАКА */
            when (makerTitle) {
                "Darkside" -> makerTitle = "Dark Side";
                "Al Waha" -> makerTitle = "Al-Waha";
                "Al mawardi" -> makerTitle = "Al-Mawardi";
                "Nakhla JTI (Japan Tobacco International)" -> makerTitle = "Nakhla";
                "Nakhla JTI" -> makerTitle = "Nakhla";
                "Sebetli" -> makerTitle = "Serbetli";
                "Serberli" -> makerTitle = "Serbetli";
                "Tabgiers" -> makerTitle = "Tangiers";
            }

            //Проверяем существование производителя в БД
            makerList = makerService.findAllByTitle(makerTitle);
            if (makerList.isEmpty()) {
                throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                        "Производитель $makerTitle не существует в БД"), null);
            }
            //Получаем названия табаков этого производителя (за минусом разделителя - двоеточия)
            val tobaccoTitles = string.substring(makerCharCount + 1).split(',');
            for (title in tobaccoTitles) {
                //Сохраняем оригинальное название табака в список
                originalNamedTobaccoList.add(Tobacco(title = title.trim()));
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
                    throw MixParsingException(generateErrorMessage(mixComponentList, newMix, pageNumber,
                            "Табак ${title.trim()} не существует в БД"), null);
                }
                mixComponentList.add(MixComponent(tobacco = newTobacco));
            }
        }
        return mixComponentList;
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
    fun parseMixCompositionText(mixFullCompositionText: String, originalNamedTobaccoList: MutableList<Tobacco>,
                                mixComponentList: MutableList<MixComponent>, newMix: Mix, pageNumber: Int): Double {
        /** Извлекаем числовые соотношения табаков из строки */
        var startIndexTobaccoComposition: Number;
        var endIndexTobaccoComposition: Number;
        var mixCompositionText: String;
        var mixCompositionValue: Int?;
        var mixSumComposition = 0.0;
        var currentTobacco: Tobacco?;

        //Ищем оригинальное название табака из источника в описании композиции микса
        for (i in 0 until originalNamedTobaccoList.size) {
            mixCompositionValue = 0;
            currentTobacco = originalNamedTobaccoList[i];
            startIndexTobaccoComposition = mixFullCompositionText.indexOf(currentTobacco.title.toLowerCase());
            //Проверяем найден ли Табак в описании композиции
            if (startIndexTobaccoComposition == -1) {
                //Новый warning
                mixParserInfo.warningLog.add(generateWarningMessage(newMix, pageNumber,
                        "В композиции микса не найден компонент ${currentTobacco.title}"));
            } else {
                endIndexTobaccoComposition = mixFullCompositionText.indexOfAny(charArrayOf('.', ',', '\n'), startIndexTobaccoComposition);
                mixCompositionText = mixFullCompositionText.substring(
                        startIndexTobaccoComposition + currentTobacco.title.length, endIndexTobaccoComposition);
                //Получаем соотношение текущего табака через регулярку
                mixCompositionValue = searchNumbersInString(mixCompositionText)?.toInt();
            }
            if (mixCompositionValue != null) {
                mixSumComposition += mixCompositionValue;
                mixComponentList[i].composition = mixCompositionValue;
            }
        }
        //Для случая незаполнения композиции для табаков - заполняем его сами равноценно
        if (mixSumComposition in 0.1..99.9) {
            val listOfNullCompositionTobacco: MutableList<Int> = mutableListOf();
            for (i in 0 until mixComponentList.size) {
                if (mixComponentList[i].composition == 0) {
                    listOfNullCompositionTobacco.add(i);
                }
            }
            if (listOfNullCompositionTobacco.isNotEmpty()) {
                val deltaComposition = (100 - mixSumComposition) / listOfNullCompositionTobacco.size;
                for (tobaccoIndex in listOfNullCompositionTobacco) {
                    mixComponentList[tobaccoIndex].composition = deltaComposition.toInt();
                    mixSumComposition += deltaComposition;
                }
            }
            //Новый warning
            mixParserInfo.warningLog.add(generateWarningMessage(newMix, pageNumber,
                    "Пытаемся восстановить пропорции для проблемных табаков. Результат: " +
                            if (mixSumComposition == 100.0) "успешно" else "ошибка"));
        }
        return mixSumComposition;
    }

    /** Возвращает список тегов черезя запятую для микса путем извлечения вкусов табаков в миксе*/
    fun generateMixTags(componentList: MutableList<MixComponent>): String {
        val mixTasteSet: MutableSet<Taste> = mutableSetOf();
        for (component in componentList) {
            mixTasteSet.addAll(component.tobaccoRef!!.tasteList);
        }
        return mixTasteSet.joinToString { taste -> taste.title };
    }

    /** Формирует наименование микса из названий табаков */
    fun generateMixTitle(componentList: MutableList<MixComponent>): String {
        var mixTitle = "";
        val delimiter = " - ";
        for (component in componentList) {
            mixTitle += component.tobaccoRef?.title + delimiter;
        }
        return mixTitle.substring(0, mixTitle.length - delimiter.length);
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

    /** Формируем текст ошибки распознавания микса */
    private fun generateErrorMessage(componentList: MutableList<MixComponent>, mix: Mix?, pageNumber: Int, message: String?): String {
        var errorMessage = "!-------------New Error-----------!" + System.lineSeparator();
        errorMessage += "Error parsing on $pageNumber page: mix '${mix?.title}' in URL:" +
                "${System.lineSeparator()}${mix?.sourceUrl}" + System.lineSeparator();
        errorMessage += "Message: $message" + System.lineSeparator();
        if (componentList.isNotEmpty()) {
            for (component: MixComponent in componentList) {
                errorMessage += "${component.tobaccoRef}" + System.lineSeparator();
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