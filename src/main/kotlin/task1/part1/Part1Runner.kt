package task1.part1

import java.util.concurrent.LinkedBlockingQueue

fun main(args: Array<String>) {
    val listOfWaitingThreads = LinkedBlockingQueue<Runnable>()
    val mutex1 = Mutex(listOfWaitingThreads)
    val mutex2 = Mutex(listOfWaitingThreads)

    for (i in 0..9) {
        if (i % 2 == 0) {
            Thread(Test1(mutex1)).start()
        } else {
            Thread(Test1(mutex2)).start()
        }
    }

    for (i in 0..9) {
        Thread.sleep(200)
        mutex1.customNotify()
    }

/*    for (i in 0..9) {
        Thread.sleep(200)
        mutex1.customNotifyAll()
    }*/
}

internal class Test1(
    private val mutex: Mutex
) : Runnable {
    override fun run() {
        try {
            mutex.customLock()
            mutex.customWait()
        } catch (ex: InterruptedException) {
            System.err.println(ex.message)
        } finally {
            mutex.customUnlock()
        }
    }
}