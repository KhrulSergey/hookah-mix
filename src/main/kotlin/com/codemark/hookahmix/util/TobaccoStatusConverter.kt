package com.codemark.hookahmix.util
import com.codemark.hookahmix.domain.TobaccoStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class TobaccoStatusConverter: AttributeConverter<TobaccoStatus, String> {

    override fun convertToDatabaseColumn(value: TobaccoStatus): String {
        return value.code;
    }

    override fun convertToEntityAttribute(value: String): TobaccoStatus {
        return  TobaccoStatus.fromCode(value);
    }
}