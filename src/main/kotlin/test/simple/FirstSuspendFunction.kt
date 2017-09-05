package test.simple

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

var count = 0
fun main(args: Array<String>) = runBlocking<Unit> {
    val job =launch(CommonPool) {
        doWorld()
        doWorld()
    }
    println("Hello,") // main function continues while coroutine is delayed
    job.join()
}

suspend fun doWorld(){
    delay(1000L)
    println("World! ${count++}")
}