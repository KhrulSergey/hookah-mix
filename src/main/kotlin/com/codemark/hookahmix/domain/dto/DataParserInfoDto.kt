package com.codemark.hookahmix.domain.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Класс для передачи о результатах парсинга
 */
class DataParserInfoDto<T>(
        var dataList: MutableList<T> = mutableListOf(),
        var sourceEntriesCount: Int = 0,
        var warningLog: MutableList<String> = mutableListOf<String>(),
        var errorLog: MutableList<String> = mutableListOf<String>(),
        var status: ParseStatus = ParseStatus.NOT_STARTED){

    override fun toString(): String {
        return "The parse is ${status.title}. There are: ${dataList.size} data parsed from $sourceEntriesCount entries, " +
                "${warningLog.size} warning occurred, ${errorLog.size} errors."
    }
}

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ParseStatus(@JsonProperty("title") val title: String) {
    NOT_STARTED("waiting for start"),
    IN_PROGRESS("data processing"),
    FINISHED("finished"),
    ABORTED("aborted"),
}