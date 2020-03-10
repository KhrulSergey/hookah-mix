package com.codemark.hookahmix.util

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.domain.Maker
import com.codemark.hookahmix.domain.Taste
import com.codemark.hookahmix.domain.Tobacco
import com.codemark.hookahmix.exception.ParsingException
import com.codemark.hookahmix.repository.FileRepository
import com.codemark.hookahmix.repository.MakerRepository
import com.codemark.hookahmix.repository.TasteRepository
import com.codemark.hookahmix.repository.TobaccoRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.io.IOException


@Component
@PropertySource(ignoreResourceNotFound = true, value = ["parser.properties"])
class TobaccoParser @Autowired constructor(private var tobaccoRepository: TobaccoRepository,
                                           private var makerRepository: MakerRepository,
                                           private var tasteRepository: TasteRepository,
                                           private var fileRepository: FileRepository,
                                           private var imageUtil: ImageUtil){


    @Value("\${url}")
    var targetUrl: String = "";
    @Value("\${makersElements}")
    var makersElements: String = ""
    @Value("\${selectStrength}")
    var selectStrength: String = "";
    @Value("\${selectMakerImage}")
    var selectMakerImage: String = "";
    @Value("\${selectFoundingYear}")
    var selectFoundingYEar: String = "";
    @Value("\${selectMakerDescription}")
    var selectMakerDescription: String = "";

    @Value("\${tobaccosElements}")
    var tobaccosElements: String = "";
    @Value("\${selectTobaccoDescription}")
    var selectTobaccoDescription: String = ""
    @Value("\${selectTobaccoTitle}")
    var selectTobaccoTitle: String = "";
    @Value("\${selectTobaccoImage}")
    var selectTobaccoImage: String = "";
    @Value("\${selectTobaccoTaste}")
    var selectTobaccoTaste: String = "";


    var makerTitle: String = "";
    var makerImageUrl: String = "";
    var makerFoundingYear: String = "";
    var makerDescription: String = "";

    var tobaccoTitle: String = "";
    var tobaccoDescription: String = "";
    var tobaccoImageUrl: String = "";
    var tobaccoStrength: Double = 0.0;
    var tobaccoTaste: String = "";


    var document: Document? = null;

    fun connectPage(): Document? {

       try {
           document = Jsoup.connect(targetUrl)
                   .timeout(0)
                   .get();
       } catch (e: IOException) {
           throw ParsingException("Connection failed", e);
       }

        return document;
    }

    fun startParse(document: Document): Unit {

        var makerUrl: String = "";

        var count: Int = 0;

        var makerElement: Elements = document.select(makersElements);

        for (item in makerElement) {
            ++count;
            makerTitle = item.text();
            println("Parser, Item: $makerTitle");
            println("Parser, count: $count");

            makerUrl = item.attr("href");
            println("Parser, URL: $makerUrl");

            /**
             * start parsing maker's page
             */

            var makerPage: Document = Jsoup.connect(makerUrl)
                    .timeout(0)
                    .get();


            var attributeStrength =
                    makerPage.selectFirst(selectStrength);

            if (attributeStrength != null) {
                tobaccoStrength = attributeStrength.text().toDouble();
                println("Parser, strength: $tobaccoStrength");
            }


            var attributeImageMaker = makerPage.selectFirst(selectMakerImage);
            if (attributeImageMaker != null) {
                makerImageUrl = attributeImageMaker.attr("style").substring(
                        attributeImageMaker.attr("style").indexOf('(') + 1,
                        attributeImageMaker.attr("style").indexOf(')'));

                println("Parser, image: $makerImageUrl");
            }


            var attributeFoundingYear = makerPage.selectFirst(selectFoundingYEar);
            if (attributeFoundingYear != null) {
                makerFoundingYear = attributeFoundingYear.text()
                        .replace(("[^0-9]").toRegex(), "");
                println("Parser, year: $makerFoundingYear")
            }


            var attributeMakerDescription = makerPage.selectFirst(selectMakerDescription);
            if (attributeMakerDescription != null) {
                makerDescription = attributeMakerDescription.text();
                println("Parser, description: $makerDescription")
            }

            var maker: Maker = Maker();
            maker.title = makerTitle;
            maker.foundingYear = makerFoundingYear;
            maker.description = makerDescription;

            var makerImage = Image();
            makerImage.image = imageUtil.save(makerImageUrl);
            fileRepository.save(makerImage);
            maker.image = makerImage;
            println("Maker almost ready...")
            makerRepository.save(maker);
            println("Maker was saved, but tobacco waiting...")

            /**
             * start parse tobacco's page (oh, shit)
             */

            var tobaccoElements = makerPage.select(tobaccosElements);

            var tobaccoCount: Int = 0;
            for (index in tobaccoElements) {
                tobaccoCount++;
                println("Tobacco count: $tobaccoCount");

                var tobaccoUrl = index.attr("href");
                println("Tobacco Url: $tobaccoUrl");

                var tobaccoPage: Document = Jsoup.connect(tobaccoUrl)
                        .timeout(0)
                        .get();

                var attributeTobaccoDescription = tobaccoPage.selectFirst(selectTobaccoDescription);
                if (attributeTobaccoDescription != null) {
                    tobaccoDescription = attributeTobaccoDescription.text();
                    println("Tobacco Description: $tobaccoDescription");
                }


                var attributeTobaccoTitle = tobaccoPage.selectFirst(selectTobaccoTitle);
                if (attributeTobaccoTitle != null) {
                    tobaccoTitle = attributeTobaccoTitle.text();
                    println("Tobacco title: $tobaccoTitle");
                }


                var attributeTobaccoImage = tobaccoPage.selectFirst(selectTobaccoImage);
                if (attributeTobaccoImage != null) {
                    tobaccoImageUrl = attributeTobaccoImage.attr("style").substring(
                            attributeTobaccoImage.attr("style").indexOf('(') + 1,
                            attributeTobaccoImage.attr("style").indexOf(')'));

                    println("Tobacco image: $tobaccoImageUrl");
                }


                var attributeTobaccoTaste = tobaccoPage.selectFirst(selectTobaccoTaste);
                var taste = Taste();


                if (attributeTobaccoTaste != null) {
                    if (attributeTobaccoTaste.children().size > 1) {
                        tobaccoTaste = "Нет моновкуса"
                        taste.taste = tobaccoTaste;
                        tasteRepository.save(taste);
                        println("Tobacco taste: $tobaccoTaste");

                    } else {
                        tobaccoTaste = attributeTobaccoTaste.text();
                        taste.taste = tobaccoTaste;
                        tasteRepository.save(taste);
                        println("Tobacco taste: $tobaccoTaste");
                    }
                }

                var tobacco = Tobacco(
                        tobaccoTitle,
                        tobaccoDescription,
                        tobaccoStrength
                );
                tobacco.maker = maker;

                var tobaccoImage = Image();
                tobaccoImage.image = imageUtil.save(tobaccoImageUrl);
                fileRepository.save(tobaccoImage);
                tobacco.image = tobaccoImage;

                tobacco.taste = taste;

                tobacco.maker = maker;
                println("Maker was added to tobacco");
                maker.tobaccos.add(tobacco);
                println("Tobacco was added to maker");
                tobaccoRepository.save(tobacco);
                println("Tobacco was saved");
            }

            makerRepository.save(maker);
            println("Maker was saved");
            tobaccoCount = 0

        }
    }

}