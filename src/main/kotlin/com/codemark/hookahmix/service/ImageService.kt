package com.codemark.hookahmix.service

import com.codemark.hookahmix.domain.Image
import com.codemark.hookahmix.repository.ImageRepository
import com.codemark.hookahmix.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@Service
class ImageService @Autowired constructor(
        private val imageRepository: ImageRepository,
        private var imageUtil: ImageUtil) {

    @Value("\${uploadDirectoryPath}")
    var uploadDirectoryPath: String = ""

    @Value("\${uploadPath}")
    var uploadPath: String = ""

    @Value("\${imageExtensionName}")
    var imageExtension: String = ""

    fun get(id: Long): Image? {
        return imageRepository.getOne(id);
    }

    fun get(name: String): Image? {
        return imageRepository.findByName(name);
    }

    fun add(image: Image): Image? {
        val savedImage = imageRepository.save(image);
        if (savedImage.id == 0L) return null;
        return savedImage;
    }

    fun getFileDirectory(): Path {
        val uploadFullPath = Paths.get(uploadDirectoryPath + uploadPath);
        Files.createDirectories(uploadFullPath);
        return uploadFullPath;
    }

    fun uploadImage(targetUrl: String, targetTitle: String): String? {
        val targetFileName = targetTitle + "_" + imageUtil.generateRandomString() + imageExtension;
        val targetPath = getFileDirectory().toString() + "/" + targetFileName;
        if (imageUtil.uploadFile(targetUrl, targetPath))
            return targetFileName;
        return null;
    }


    fun deleteUploadedFile(fileName: String): Boolean {
        val targetPath = getFileDirectory().toString() + "/" + fileName;
        return imageUtil.deleteFile(targetPath);
    }
}