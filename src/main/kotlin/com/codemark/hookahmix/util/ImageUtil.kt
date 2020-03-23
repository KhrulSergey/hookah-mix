package com.codemark.hookahmix.util

import com.codemark.hookahmix.exception.ImageReadException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.*
import java.net.URL
import java.util.*

@Component
class ImageUtil {

    @Value("\${uploadPath}")
    var uploadPath: String = ""

    fun save(target: String): ByteArray {

        var imagesFolder = File(uploadPath)
        if (!imagesFolder.exists()) {
            imagesFolder.mkdir()
        }
        var fileName: String = uploadPath + "/" + UUID.randomUUID().toString() + ".jpg"

        var url = URL(target);


        var outputStream: OutputStream = FileOutputStream(File(fileName))
        var inputStream: InputStream

        try {
            inputStream = url.openStream()
            var buffer = ByteArray(8192)
            var index = 0
            while ({index = inputStream.read(buffer); index}() > 0) {
                outputStream.write(buffer, 0, index)
            }
        } catch (e: IOException) {
            throw ImageReadException("Failed to read", e)
        }

        inputStream.close()
        outputStream.close()

        return Base64.getEncoder().encode(fileName.toByteArray())
    }

}