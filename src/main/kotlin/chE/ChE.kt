package chE

import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun main() {
    println("\n\n===== E.2.1 =====")

    log("main() started...")
//    launchInGlobalScope()
//    log("launchInGlobalScope() executed")
//    runBlockingExample()
//    log("runBlockingExample() executed")
//    Thread.sleep(2000L)
    yieldExample()
    log("yieldExample() executed")
    Thread.sleep(2000L)
    log("main() terminated...")

}

// ===== E.2.1 =====
fun now() = ZonedDateTime.now().toLocalTime().truncatedTo((ChronoUnit.MILLIS))

fun log(msg:String) = println("${now()}:${Thread.currentThread()}: $msg")

fun launchInGlobalScope() {
    GlobalScope.launch {
        log("coroutine started.......")
    }
}

fun runBlockingExample() {
    runBlocking {
        launch {
            log("GlobalScope.launch started.......")
        }
    }
}

fun yieldExample() {
    runBlocking {
        launch {
            log("1")
            yield()
            log("3")
            yield()
            log("5")
        }
        log("after first launch...")
        launch {
            log("2")
            delay(500L)
            log("4")
            delay(500L)
            log("6")
        }
        log("after second launch...")
    }
}