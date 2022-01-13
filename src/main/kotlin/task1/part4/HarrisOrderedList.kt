package task1.part4

import java.util.*
import java.util.concurrent.atomic.AtomicReference


class HarrisOrderedList<T : Comparable<T>?> {
    private val head = Node<T?>(null, AtomicReference(null))

    internal class Node<T>(var data: T, var next: AtomicReference<Node<T>?>)

    fun remove(data: T): Boolean {
        require(!Objects.isNull(data)) { "Argument should not be null" }
        var prevEl: Node<T?>? = head
        while (Objects.nonNull(prevEl!!.next.get())) {
            val currEl = prevEl.next.get()
            val nextEl = currEl!!.next.get()
            if (currEl.data!!.compareTo(data) == 0) {
                if (currEl.next.compareAndSet(nextEl, null) && prevEl.next.compareAndSet(currEl, nextEl)) {
                    return true
                }
            } else {
                prevEl = currEl
            }
        }
        return false
    }

    fun add(data: T) {
        require(!Objects.isNull(data)) { "Argument should not be null" }
        val newEl = Node<T?>(data, AtomicReference(null))
        var currentEl: Node<T?>? = head
        while (true) {
            val nextEl = currentEl!!.next.get()
            if (Objects.nonNull(nextEl)) {
                if (nextEl!!.data!! >= data) {
                    newEl.next = AtomicReference(nextEl)
                    if (currentEl.next.compareAndSet(nextEl, newEl)) {
                        return
                    }
                } else {
                    currentEl = nextEl
                }
            } else if (currentEl.next.compareAndSet(null, newEl)) {
                return
            }
        }
    }

    operator fun contains(data: T): Boolean {
        var currentEl = head.next.get()
        while (Objects.nonNull(currentEl)) {
            if (currentEl!!.data!!.compareTo(data) == 0) {
                return true
            }
            currentEl = currentEl.next.get()
        }
        return false
    }

    fun nonSafePrint() {
        var current = head.next.get()
        while (Objects.nonNull(current)) {
            println(current!!.data)
            current = current.next.get()
        }
    }
}