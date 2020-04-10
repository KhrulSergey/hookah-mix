package com.codemark.hookahmix.util
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/** Утилита для конвертирования наименования файла в полный путь (ссылку) на сервере*/
@Converter(autoApply = false)
@Component
class ImagePathConverter: AttributeConverter<String, String> {

    @Value("\${hostProtocol}")
    var hostProtocol: String = ""

    @Value("\${hostname}")
    var hostname: String = ""

    @Value("\${uploadPath}")
    var uploadPath: String = ""

    /** Возвращает URI хранилища на сервере */
    fun getFileWebPath(): String {
        val uploadFullPath = hostProtocol + hostname + uploadPath;
        return uploadFullPath;
    }

    override fun convertToDatabaseColumn(value: String): String {
        return value;
    }

    override fun convertToEntityAttribute(value: String): String {
        return  getFileWebPath() + value;
    }
}