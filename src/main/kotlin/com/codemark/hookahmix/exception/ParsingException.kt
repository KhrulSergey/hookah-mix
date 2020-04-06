package com.codemark.hookahmix.exception

class ParsingException(message: String?, cause: Throwable?) :
        RuntimeException(message, cause) {
}

/**Ошибки открытия URL страницы с сервисом JSOUP*/
class JsoupConnectException(message: String?, cause: Throwable?) : RuntimeException(message, cause){
}

/**Ошибки обработки данных по распознаванию Мисков */
class MixParsingException(message: String?, cause: Throwable?) : RuntimeException(message, cause){
}

/**Предупреждение обработки данных по распознаванию Мисков */
class MixParsingWarning(message: String?, cause: Throwable?) : RuntimeException(message, cause){
}