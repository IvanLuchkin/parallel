package task1.part1

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference


class Mutex(
    private val waitingThreads: LinkedBlockingQueue<Runnable>
) {
    private val currentThread = AtomicReference<Runnable>()

    fun customLock() {
        if (Thread.currentThread() == currentThread.get()) {
            throw RuntimeException("You can't lock mutex 2 or more times")
        }
        while (!currentThread.compareAndSet(null, Thread.currentThread())) {
            Thread.yield()
        }
        println("Mutex took by: " + Thread.currentThread().name)
    }

    fun customUnlock() {
        if (Thread.currentThread() != currentThread.get()) {
            throw RuntimeException("You can't call unlock when you don't have lock")
        }
        println("Mutex unlocked by: " + Thread.currentThread().name)
        currentThread.set(null)
    }

    fun customWait() {
        val thread = Thread.currentThread()
        if (thread != currentThread.get()) {
            throw RuntimeException("You should lock mutex before use of wait method")
        }
        waitingThreads.put(thread)
        println("Waiting: " + Thread.currentThread().name)
        customUnlock()
        while (waitingThreads.contains(thread)) {
            Thread.yield()
        }
        customLock()
        println("No waiting any more: " + Thread.currentThread().name)
    }

    fun customNotify() {
        waitingThreads.take()
        println("Notify: " + Thread.currentThread().name)
    }

    fun customNotifyAll() {
        waitingThreads.clear()
        println("Notify all: " + Thread.currentThread().name)
    }
}