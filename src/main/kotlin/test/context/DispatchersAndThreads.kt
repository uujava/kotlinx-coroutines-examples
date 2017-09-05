package test.context

import kotlinx.coroutines.experimental.*
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) = runBlocking<Unit> {
    val jobs = arrayListOf<Job>()
    jobs += launch(Unconfined) { // not confined -- will work with main thread
        println("      'Unconfined': I'm working in thread ${Thread.currentThread().name} $this ${this.coroutineContext}")
    }
    jobs += launch(coroutineContext) { // context of the parent, runBlocking coroutine
        println("'coroutineContext': I'm working in thread ${Thread.currentThread().name} ${Thread.currentThread().threadGroup}  $this ${this.coroutineContext}")
    }
    jobs += launch(CommonPool) { // will get dispatched to ForkJoinPool.commonPool (or equivalent)
        println("      'CommonPool': I'm working in thread ${Thread.currentThread().name}   ${Thread.currentThread().threadGroup} $this ${this.coroutineContext}")
    }
    jobs += launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
        println("          'newSTC': I'm working in thread ${Thread.currentThread().name}   ${Thread.currentThread().threadGroup} $this ${this.coroutineContext}")
    }
    jobs.forEach { it.join() }

    val parentJob = Job()
    // parent-child
    val request = launch(newSingleThreadContext("RequestThread") + parentJob) {
        println("request: spawns 3 jobs in context ${this.coroutineContext}!")
        val job1 = launch(CommonPool) {
            println("job1: I have my own context and execute independently ${this.coroutineContext}!")
            delay(1000)
            println("job1: I am not affected by cancellation of the request ${this.coroutineContext}")
        }
        // and the other inherits the parent context
        val job2 = launch(coroutineContext) {
            println("job2: I am a child of the request coroutine in context ${this.coroutineContext}")
            delay(1000)
            println("job2: I will not execute this line if my parent request is cancelled ")
        }
        // and the other inherits the parent context but in common pool
        val job3 = launch(coroutineContext + CommonPool) {
            println("job3: I am a child of the request coroutine ${this.coroutineContext}")
            delay(1000)
            println("job3: I will not execute this line if my parent request is cancelled")
        }
        job1.join()
        job2.join()
        job3.join()
    }

    val job4 = launch(newSingleThreadContext("Job4Thread") + parentJob) {
        println("job4: I am a child of the parent Job ${this.coroutineContext}")
        delay(1000)
        println("job4: I will not execute this line if my parent request is cancelled")
    }

    delay(500)
    parentJob.cancel() // cancel processing of the request and job4
    println("main: cancel ${this.coroutineContext}")
    delay(1000) // delay a second to see what happens
    println("main: Who has survived request cancellation? ${this.coroutineContext}")
}
