package com.codemark.hookahmix.exception

import com.codemark.hookahmix.domain.Mix
import com.codemark.hookahmix.domain.Tobacco
import java.io.IOException

class ParsingException(message: String?, cause: Throwable?) :
        RuntimeException(message, cause) {
}

class JsoupConnectException(message: String?, cause: Throwable?) : RuntimeException() {
    init {
        println("JsoupConnectException - $message in $cause");
        throw RuntimeException(message, cause);
    }
}

class MixParsingException(tobaccoList: MutableList<Tobacco>?, mix: Mix?, pageNumber: Int, message: String?) : Exception() {
    init {
        println("!-------------New Error-----------!")
        if (mix != null) {
            println("Error parsing on $pageNumber page: '${mix.title}' in URL:\n${mix.mixUrl}");
        }
        println("Message: $message")
        if (tobaccoList != null) {
            for (tobacco: Tobacco in tobaccoList) {
                println(tobacco);
            }
        }
    }
}

class MixParsingWarning(mix: Mix?, pageNumber: Int, message: String?) : Exception() {
    init {
//        println("!-------------New Warning-----------!")
//        if (mix != null) {
//            println("Problem on $pageNumber page: ${mix.title} in URL:\n${mix.mixUrl}");
//        }
//        println("Message: $message")
    }
}