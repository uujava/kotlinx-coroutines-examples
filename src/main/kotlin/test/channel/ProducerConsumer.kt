package test.channel

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking


fun numbers() = produce<Int>(CommonPool) {
    var x = 1
    while (true) {
        send(x++)
    }
}

fun squares(numbers: ReceiveChannel<Int>) = produce<Int>(CommonPool) {
    numbers.consumeEach { send(it * it) }
}

fun squareChannel(): Channel<Int> {
    val channel = Channel<Int>()
    launch(CommonPool) {
        repeat(5) { channel.send(it * it) }
        channel.close()
    }
    return channel
}


fun main(args: Array<String>) = runBlocking<Unit> {
    for (x in squareChannel()) println("for loop: $x")
    val numbers = numbers()
    repeat(5) { println("number: ${numbers.receive()}") }
    val squares = squares(numbers)
    repeat(5) { println("square: ${squares.receive()}") }
    numbers.cancel()
    squares.cancel()
}