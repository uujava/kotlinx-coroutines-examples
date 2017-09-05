package test.simple

import kotlinx.coroutines.experimental.*
import kotlin.concurrent.thread

fun main(args: Array<String>) {
//    thread { // - cause compile time error at delay
    launch(CommonPool){ // create new coroutine in common thread pool
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main function continues while coroutine is delayed
    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
}