package task1.part4

fun main(args: Array<String>) {
    val harrisOrderedList = HarrisOrderedList<String>()
    val arr = arrayOfNulls<Thread>(10)
    for (i in 0..9) {
        arr[i] = Thread(Test4(harrisOrderedList))
        arr[i]!!.start()
    }
    for (i in 0..9) {
        arr[i]!!.join()
    }
    println("Contains: " + harrisOrderedList.contains("Thread-1"))
    harrisOrderedList.nonSafePrint()
}

internal class Test4(private val harrisOrderedList: HarrisOrderedList<String>) : Runnable {
    override fun run() {
        val currThreadName = Thread.currentThread().name
        harrisOrderedList.add(currThreadName)
        if (currThreadName == "Thread-4") {
            println("Remove: " + harrisOrderedList.remove(currThreadName))
        }
    }
}