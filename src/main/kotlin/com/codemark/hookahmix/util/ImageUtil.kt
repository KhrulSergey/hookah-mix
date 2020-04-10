package com.codemark.hookahmix.util

import org.springframework.stereotype.Component
import java.io.*
import java.net.URL
import java.security.SecureRandom
import java.util.*

@Component
class ImageUtil {

    fun deleteFile(fileFullPath: String): Boolean {
        try {
            val fileToDelete: File = File(fileFullPath);
            if (fileToDelete.isFile) {
                return fileToDelete.delete();
            }
        } catch (exc: IOException) {
            println("Fail to delete file:$exc");
        }
        return false;
    }

    @Throws(IOException::class)
    fun uploadFile(targetUrl: String, targetPath: String): Boolean {
        var result = false;
        try {
            val url = URL(targetUrl);
            val outputStream: OutputStream = FileOutputStream(File(targetPath));
            val inputStream: InputStream
            inputStream = url.openStream()
            val buffer = ByteArray(8192)
            var index = 0
            while ({ index = inputStream.read(buffer); index }() > 0) {
                outputStream.write(buffer, 0, index)
            }
            inputStream.close()
            outputStream.close()
            result = true;
        } catch (exc: IOException) {
            println("Fail to upload file:$exc");
        }
        return result;
    }

    fun generateRandomString(): String {
        val rand = SecureRandom();
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val randomBytes = ByteArray(6);
        rand.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }

}