package com.codemark.hookahmix.exception

class ParsingException(message: String?, cause: Throwable?) :
        RuntimeException(message, cause) {
}

class JsoupConnectException(message: String?, cause: Throwable?) : RuntimeException() {
    init {
        println("JsoupConnectException - $message in $cause");
        throw RuntimeException(message, cause);
    }
}

class MixParsingException(message: String?, cause: Throwable?) : RuntimeException(message, cause){
}

class MixParsingWarning(message: String?, cause: Throwable?) : RuntimeException(message, cause){
}