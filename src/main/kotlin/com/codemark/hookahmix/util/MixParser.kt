package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.exception.JsoupConnectException
import com.codemark.hookahmix.exception.MixParsingException
import com.codemark.hookahmix.exception.MixParsingWarning
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
    var document: Document? = null;

    fun connectPage(target: String = targetUrl): Document? {
        try {
            document = Jsoup.connect(target)
                    .timeout(0)
                    .get();
        } catch (exc: IOException) {
            throw JsoupConnectException("Страницы $target не существует", exc);
        }
        return document;
    }

    fun startParse(document: Document?): String {
        /** Конечный список найденных Миксов*/
        val mixList: MutableList<Mix> = mutableListOf();
        /**Страница со списком из 10 миксов */
        var mixListPage: Document = document!!;
        //Получаем элемент "Далее" для навигации по пагинации
        var nextPageUrl: String = mixListPage.select(nextPageElement).attr("href");
        var pageNumber: Int = 1;

        while (nextPageUrl.isNotBlank() && mixList.size < 50) {
            val mixUrlElements: Elements = mixListPage.select(mixUriElement);
            val newMixList = parseOnePage(mixUrlElements, pageNumber);
            if (newMixList.isNotEmpty()) {
                mixList.addAll(newMixList);
            }
            mixListPage = connectPage(nextPageUrl)!!;
            nextPageUrl = mixListPage.select(nextPageElement).attr("href");
            pageNumber++;
        }
        return ("Добавлено миксов: ${mixList.size}");
    }

    fun parseOnePage(mixUrlElements: Elements, pageNumber: Int): MutableList<Mix> {
        var newMaker: Maker = Maker("");
        var tobaccoList: MutableList<Tobacco> = mutableListOf();
        var mixList: MutableList<Mix> = mutableListOf();
        var newMix: Mix? = null;
        var newTobacco: Tobacco;

        var mixFullTitle: String = "";
        var makerTitle: String = "";
        var mixDescription: String = "";
        var mixFullCompositionText: String = "";

        for (item in mixUrlElements) {
            try {
                newMix = Mix();
                tobaccoList.clear();
                //Получаем ссылку на описание микса
                newMix.mixUrl = item.attr("href");
                mixFullTitle = item.select("h3").text();
                for (string in mixFullTitle.split('.')) {
                    //Получаем имя первого производителя
                    var makerCharCount = string.indexOf(":");
                    makerTitle = string.substring(0, makerCharCount).trim();

                    if (makerTitle.isBlank()) {
                        //TODO Ошибка получения наименования производителя
                        throw MixParsingException(tobaccoList, newMix, pageNumber,
                                "Ошибка получения наименования производителя из источника");
                    }
                    newMaker = Maker(makerTitle);
                    if (false) {
                        //TODO Проверить существование производителя в БД
                        throw MixParsingException(tobaccoList, newMix, pageNumber,
                                "Производитель $makerTitle не существует в БД");
                    }

                    //Получаем названия табаков этого производителя (за минусом двоеточия)
                    var tobaccoTitles = string.substring(makerCharCount + 1).split(',');
                    for (title in tobaccoTitles) {
                        newTobacco = Tobacco();
                        newTobacco.title = title.trim();
                        if (false) {
                            //TODO Проверить существование табака в БД
                            throw MixParsingException(tobaccoList, newMix, pageNumber, "Табак не существует в БД");
                        }
                        newTobacco.maker = newMaker;
                        tobaccoList.add(newTobacco);
                    }
                }
                //Формируем наименование микса
                newMix.title = generateMixTitle(tobaccoList);
                if (false) {
                    //TODO проверить наименование микса в БД
                    throw MixParsingException(tobaccoList, newMix, pageNumber, "Микс уже существует в БД");
                }

                /**
                 * start parsing mix's page
                 */
                var mixPage: Document = connectPage(newMix.mixUrl)!!;
                var mixContent: Elements = mixPage.select(mixContentElement);

                /** Формируем описание микса*/
                if (mixContent.isEmpty()) {
                    //TODO Ошибка получения контента микса
                    throw MixParsingException(tobaccoList, newMix, pageNumber, "Ошибка получения контента микса из источника");
                }
                mixDescription = mixContent.first().text();
                if (mixDescription.isBlank()) {
                    //TODO Ошибка описания микса
                    throw MixParsingException(tobaccoList, newMix, pageNumber, "Ошибка получения контента микса из источника");
                }
                newMix.description = mixDescription;

                /** Формируем композиию табаков в миксе*/
                mixFullCompositionText = "";
                for (i in 1 until mixContent.size) {
                    if (mixContent[i].select("strong").text().indexOf(mixCompositionLabelText) != -1) {
                        mixFullCompositionText = mixContent[i].text();
                        break;
                    }
                }
                if (mixFullCompositionText.isBlank()) {
                    //TODO Ошибка композиции
                    throw MixParsingException(tobaccoList, newMix, pageNumber, "Ошибка получения композиции табаков в миксе из источника");
                }

                /** Извлекаем числовые соотношения табаков из строки */
                var startTobaccoComposition: Number;
                var endTobaccoComposition: Number;
                var mixComposition: String = "";
                var mixSumComposition: Int = 0;
                //Настраиваем регулярку для получения чисел из строки
                val pat: Pattern = Pattern.compile("[-]?[0-9]+(.[0-9]+)?")
                var matcher: Matcher;
                //Ищем название табака в описании композиции микса
                for (tobacco in tobaccoList) {
                    startTobaccoComposition = mixFullCompositionText.indexOf(tobacco.title.toLowerCase());
                    //Проверяем найден ли Табак в описании композиции
                    if (startTobaccoComposition == -1) {
                        //TODO Варнинг композиции
                        MixParsingWarning(newMix, pageNumber, "В композиции микса не найден компонент ${tobacco.title}");
                    } else {
                        endTobaccoComposition = mixFullCompositionText.indexOfAny(charArrayOf('.', ',', '\n'), startTobaccoComposition);
                        mixComposition = mixFullCompositionText.substring(
                                startTobaccoComposition + tobacco.title.length, endTobaccoComposition);
                        //Получаем соотношение текущего табака через регулярку
                        matcher = pat.matcher(mixComposition);
                        if (matcher.find()) {
                            tobacco.composition = matcher.group().toInt();
                            mixSumComposition += tobacco.composition;
                        }
                    }
                }
                //Для случая незаполнения композиции для табаков - заполняем его сами равноценно
                if (mixSumComposition in 1..99) {
                    var listOfNullCompositionTobacco: MutableList<Int> = mutableListOf();
                    for (i in 0 until tobaccoList.size) {
                        if (tobaccoList[i].composition == 0) {
                            listOfNullCompositionTobacco.add(i);
                        }
                    }
                    if (listOfNullCompositionTobacco.isNotEmpty()) {
                        var deltaComposition = (100 - mixSumComposition) / listOfNullCompositionTobacco.size;
                        for (tobaccoIndex in listOfNullCompositionTobacco) {
                            tobaccoList[tobaccoIndex].composition = deltaComposition;
                            mixSumComposition += deltaComposition;
                        }
                    }
                    //TODO Варнинг композиции
                    MixParsingWarning(newMix, pageNumber,
                            "Пытаемся восстановить пропорции для проблемных табаков. Результат: " +
                                    "${if (mixSumComposition == 100) "успешно" else "ошибка"}");
                }
                if (mixSumComposition != 100) {
                    //TODO Ошибка композиции
                    throw MixParsingException(tobaccoList, newMix, pageNumber, "Ошибка в композиции табаков в миксе");
                }

                //TODO Посчитать крепость микса взяв из Табаков из БД

                newMix.tobaccoMixList = tobaccoList;
                mixList.add(newMix);
            } catch (exc: MixParsingException) {
                continue;
            } catch (exc: Exception) {
                MixParsingException(tobaccoList, newMix, pageNumber, "!!--Неизвестная ошибка--!!");
                continue;
            }
        }
        return mixList;
    }

    fun generateMixTitle(tobaccoList: MutableList<Tobacco>): String {
        var mixTitle = "";
        val delimiter = "- ";
        for (tobacco in tobaccoList) {
            mixTitle += tobacco.title + delimiter;
        }
        return mixTitle.substring(0, mixTitle.length - delimiter.length);
    }
}