package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.ComponentRepository
import com.codemark.hookahmix.repository.MixRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminPanelService @Autowired constructor(private val makerService: MakerService,
                                               private val tasteService: TasteService,
                                               private val tobaccoService: TobaccoService,
                                               private val imageService: ImageService,
                                               private val mixRepository: MixRepository,
                                               private val componentRepository: ComponentRepository) {

    //TODO Перенести методы в соответ. сервисы

    fun addTobacco(title: String, makerTitle: String,
                   description: String, tasteTitle: String,
                   strength: Double, image: String, tags: String) {

        val findMaker: Maker = makerService.getOne(makerTitle)!!;

        val findTaste: Taste = tasteService.getOne(tasteTitle)!!;

        val newTobacco = Tobacco(title, description, strength);
        newTobacco.maker = findMaker;
        newTobacco.taste = findTaste;

        val tobaccoImage = Image();
        tobaccoImage.name = imageService.uploadImage(image, makerTitle);
        imageService.add(tobaccoImage);
        newTobacco.image = tobaccoImage;

        tobaccoService.addOne(newTobacco);
    }

    fun addMix(title: String, tags: String,
               description: String, strength: String) {

        val mix = Mix();
        mix.title = title;
        mix.tags = tags;
        mix.rating = 0;
        mix.description = description;
        mix.strength = Integer.parseInt(strength);

        mixRepository.save(mix);
    }

    fun addComponentMix(mixTitle: String, makerTitle: String,
                        tobaccoTitle: String, composition: Int) {

        val mix = mixRepository.findByTitle(mixTitle);
        val maker = makerService.getOne(makerTitle);
        val tobacco = tobaccoService.getOne(tobaccoTitle, maker!!);

        val component = Component();
        component.mix = mix;
        component.tobacco = tobacco;
        component.composition = composition;

        mix.components.add(component);
//        tobacco!!.components.add(component);

        componentRepository.save(component);
    }
}