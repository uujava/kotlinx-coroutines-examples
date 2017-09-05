package test.composition

import kotlinx.coroutines.experimental.*
import kotlin.system.measureTimeMillis

suspend fun foo(): Int {
    delay(1000L)
    return 1
}

suspend fun bar(): Int {
    delay(1000L)
    return 2
}

fun asyncFoo() = async(CommonPool){ foo() }
fun asyncBar() = async(CommonPool){ foo() }

suspend fun doSeq() {
    val one = foo()
    val two = bar()
    println("The answer is ${one + two}")
}

suspend fun doAsync() {
    val one = async(CommonPool){ foo() }
    val two = async(CommonPool) { bar() }
    println("The answer is ${one.await() + two.await()}")
}

suspend fun doLazyAsync() {
    val one = async(CommonPool, CoroutineStart.LAZY){ foo() }
    val two = async(CommonPool, CoroutineStart.LAZY) { bar() }
    println("The answer is ${one.await() + two.await()}")
}

fun callAsyncFoAsyncBar():Int{
    val foo = asyncFoo()
    val bar = asyncBar()
    return runBlocking { foo.await() + bar.await() }
}

fun main(args: Array<String>) = runBlocking<Unit> {
    println("doSeq Completed in ${measureTimeMillis { doSeq() }} ms")
    println("doAsync Completed in ${measureTimeMillis { doAsync() }} ms")
    println("doLazyAsync Completed in ${measureTimeMillis { doLazyAsync() }} ms")
    println("callAsyncFoAsyncBar foo Completed in ${measureTimeMillis { callAsyncFoAsyncBar() }} ms")
}