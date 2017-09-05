package test.simple
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    val delay = 1000000000L
    runBlocking<Unit> {
        val tests = arrayOf(1, 2, 10, 100, 1000, 10000, 1000000)
        tests.forEach {
            var countALot = 0
            val jobs = List(it) { // create a lot of coroutines and list their jobs
                launch(CommonPool) {
                    delay(delay, TimeUnit.NANOSECONDS)
                    countALot++ // not thread safe, just for test
                }
            }
            val ts = System.nanoTime()
            jobs.forEach { it.join() } // wait for all jobs to complete
            val overhead = (System.nanoTime() - ts - delay) / 1000000L
            println("Total: $it counter: $countALot overhead: $overhead ms")
        }
    }
}