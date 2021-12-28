package task1.part2

fun main(args: Array<String>) {
    val skipList = SkipList<String>(16, 0.5)
    val arr = arrayOfNulls<Thread>(10)

    for (i in 0..9) {
        arr[i] = Thread(Part2(skipList))
        arr[i]!!.start()
    }

    for (i in 0..9) {
        arr[i]!!.join()
    }

    println("Contains: " + skipList.contains("Thread-1"))
    skipList.nonSafePrint()
}

internal class Part2(private val skipList: SkipList<String>) : Runnable {
    override fun run() {
        val currThreadName = Thread.currentThread().name
        while (!skipList.add(currThreadName)) {
            println("Add $currThreadName: false")
        }
        println("Add $currThreadName: true")
        if (currThreadName == "Thread-4") {
            println("Remove " + currThreadName + ": " + skipList.remove(currThreadName))
        }
    }
}