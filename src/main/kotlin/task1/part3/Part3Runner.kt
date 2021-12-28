package task1.part3

fun main(args: Array<String>) {
    val michaelScottQueue: MichaelScottQueue<String> = MichaelScottQueue()
    val arr = arrayOfNulls<Thread>(10)
    for (i in 0..9) {
        arr[i] = Thread(Test3(michaelScottQueue))
        arr[i]!!.start()
    }
    for (i in 0..9) {
        arr[i]!!.join()
    }
    michaelScottQueue.nonSafePrint()
}

internal class Test3(michaelScottQueue: MichaelScottQueue<String>) : Runnable {
    private var michaelScottQueue: MichaelScottQueue<String>

    init {
        this.michaelScottQueue = michaelScottQueue
    }

    override fun run() {
        michaelScottQueue.add(Thread.currentThread().name)
        if (Math.random() > 0.3) {
            michaelScottQueue.remove()
        }
    }
}