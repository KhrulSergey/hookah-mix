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


        var url: URL = URL(target);


        var byteArrayOutputStream = ByteArrayOutputStream();
        var inputStream: InputStream;

        try {
            inputStream = BufferedInputStream(url.openStream());
            var buffer = ByteArray(8192);
            var index = 0;
            while ({index = inputStream.read(buffer); index}() > 0) {
                byteArrayOutputStream.write(buffer, 0, index);
            }

        } catch (e: IOException) {
            throw ImageReadException("Failed to read", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

}