package task1.part3

import java.util.concurrent.atomic.AtomicReference


class MichaelScottQueue<T> {
    private val dummy = Node<T?>(null, AtomicReference(null))
    private val head = AtomicReference(dummy)
    private val tail = AtomicReference(dummy)

    internal class Node<T>(var data: T, var next: AtomicReference<Node<T>>)

    fun remove(): T? {
        while (true) {
            val currentHead = head.get()
            val currentTail = tail.get()
            val nextHead = currentHead!!.next.get()
            if (currentHead === currentTail) {
                if (nextHead == null) {
                    throw NoSuchElementException()
                } else {
                    tail.compareAndSet(currentTail, currentTail.next.get())
                }
            } else {
                if (head.compareAndSet(currentHead, nextHead)) {
                    return nextHead!!.data
                }
            }
        }
    }

    fun add(data: T) {
        val newTail = Node<T?>(data, AtomicReference(null))
        while (true) {
            val currentTail = tail.get()
            if (currentTail.next.compareAndSet(null, newTail)) {
                tail.compareAndSet(currentTail, newTail)
                return
            } else {
                tail.compareAndSet(currentTail, currentTail.next.get())
            }
        }
    }

    fun nonSafePrint() {
        var current = head.get()
        while (current != null) {
            println(current.data)
            current = current.next.get()
        }
    }
}