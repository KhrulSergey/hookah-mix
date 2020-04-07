package com.codemark.hookahmix.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.SecureRandom
import java.util.*

@Component
class ImageUtil {

    @Value("\${uploadDirectoryPath}")
    var uploadDirectoryPath: String = ""

    @Value("\${uploadPath}")
    var uploadPath: String = ""

    @Value("\${imageExtensionName}")
    var imageExtension: String = ""

    fun getFileDirectory(): Path {
        var uploadFullPath = Paths.get(uploadDirectoryPath + uploadPath);
        Files.createDirectories(uploadFullPath);
        return uploadFullPath;
    }

    @Throws(IOException::class)
    fun uploadImage(targetUrl: String, targetTitle: String): String {
        return uploadFile(targetUrl, targetTitle, imageExtension);
    }

    @Throws(IOException::class)
    fun deleteFile(fileName: String): Boolean {
        var fileToDelete: File = File(getFileDirectory().toString() + "/" + fileName);
        if (fileToDelete.isFile) {
            return fileToDelete.delete();
        }
        return false
    }

    @Throws(IOException::class)
    private fun uploadFile(targetUrl: String, targetFileTitle: String, targetExtension: String): String {
        var url = URL(targetUrl);
        var targetFileName = targetFileTitle + "_" + generateRandomString() + targetExtension;
        var outputStream: OutputStream = FileOutputStream(File(getFileDirectory().toString() + "/" + targetFileName));
        var inputStream: InputStream
        inputStream = url.openStream()
        var buffer = ByteArray(8192)
        var index = 0
        while ({ index = inputStream.read(buffer); index }() > 0) {
            outputStream.write(buffer, 0, index)
        }
        inputStream.close()
        outputStream.close()
        return targetFileName;
    }

    private fun generateRandomString(): String {
        var rand = SecureRandom();
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val randomBytes = ByteArray(6);
        rand.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }

}