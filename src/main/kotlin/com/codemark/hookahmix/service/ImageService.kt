package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ImageService @Autowired constructor(
        private val imageRepository: ImageRepository) {

    fun get(id: Long): Image?{
        return imageRepository.getOne(id);
    }

    fun get(name: String): Image?{
        return imageRepository.findByName(name);
    }

    fun add(image: Image): Image? {
        var savedImage = imageRepository.save(image);
        if(savedImage.id == 0L) return null;
        return savedImage;
    }
}