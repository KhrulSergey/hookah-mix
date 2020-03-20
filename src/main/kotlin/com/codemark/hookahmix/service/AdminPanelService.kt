package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.*
import com.codemark.hookahmix.repository.*
import com.codemark.hookahmix.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminPanelService @Autowired constructor(private val makerRepository: MakerRepository,
                                               private val tasteRepository: TasteRepository,
                                               private val tobaccoRepository: TobaccoRepository,
                                               private val fileRepository: FileRepository,
                                               private val mixRepository: MixRepository,
                                               private val componentRepository: ComponentRepository,
                                               private val imageUtil: ImageUtil) {


    fun addTobacco(title: String, maker: String,
                   description: String, taste: String,
                   strength: Double, image: String, tags: String) {

        var findMaker: Maker = makerRepository.findByTitle(maker)

        var findTaste: Taste = tasteRepository.findByTaste(taste)

        var newTobacco = Tobacco(title, description, strength)
        newTobacco.maker = findMaker
        newTobacco.taste = findTaste

        var tobaccoImage = Image()
        tobaccoImage.image = imageUtil.save(image)
        fileRepository.save(tobaccoImage)
        newTobacco.image = tobaccoImage

        tobaccoRepository.save(newTobacco)


    }

    fun addMix(title: String, tags: String,
               description: String, strength: String) {

        var mix = Mix()
        mix.title = title
        mix.tags = tags
        mix.rating = 0
        mix.description = description
        mix.strength = Integer.parseInt(strength)

        mixRepository.save(mix)
    }

    fun addComponentMix(mixTitle: String, makerTitle: String,
                        tobaccoTitle: String, composition: Int) {

        var mix = mixRepository.findByTitle(mixTitle)
        var tobacco = tobaccoRepository.findOneByTitleAndMaker(tobaccoTitle, makerTitle)

        var component = Component()
        component.mix = mix
        component.tobacco = tobacco
        component.composition = composition

        mix.components.add(component)
        tobacco.components.add(component)

        componentRepository.save(component)

    }
}