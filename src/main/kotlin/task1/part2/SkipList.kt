package task1.part2

import java.util.*
import java.util.concurrent.atomic.AtomicReference


class SkipList<T : Comparable<T>?>(private val height: Int, private val p: Double) {
    private val head: Node<T?>

    internal class Node<T>(var data: T, val right: AtomicReference<Node<T>?>, var down: Node<T>?)

    init {
        var element = Node<T?>(null, AtomicReference(null), null)
        head = element
        for (i in 0 until height - 1) {
            val newElementHead = Node<T?>(null, AtomicReference(null), null)
            element.down = newElementHead
            element = newElementHead
        }
    }

    fun remove(data: T): Boolean {
        require(!Objects.isNull(data)) { "Argument should not be null" }
        var currEl: Node<T?>? = head
        var currentLevel = height
        var towerUnmarked = true
        while (currentLevel > 0) {
            val rightEl = currEl!!.right.get()
            if (Objects.nonNull(rightEl) && rightEl!!.data!!.compareTo(data) == 0) {
                val afterRightEl = rightEl.right.get()
                if (towerUnmarked) {
                    var towerEl = rightEl
                    while (Objects.nonNull(towerEl)) {
                        towerEl!!.right.compareAndSet(towerEl.right.get(), null)
                        towerEl = towerEl.down
                    }
                    towerUnmarked = false
                }
                currEl.right.compareAndSet(rightEl, afterRightEl)
            }
            if (Objects.nonNull(rightEl) && rightEl!!.data!! < data) {
                currEl = rightEl
            } else {
                currEl = currEl.down
                currentLevel--
            }
        }
        return !towerUnmarked
    }

    fun add(data: T): Boolean {
        require(!Objects.isNull(data)) { "Argument should not be null" }
        val prev: MutableList<Node<T?>?> = ArrayList()
        val prevRight: MutableList<Node<T?>?> = ArrayList()
        var currEl: Node<T?>? = head
        val levelOfTower = generateHeight()
        var currentLevel = height
        while (currentLevel > 0) {
            val rightEl = currEl!!.right.get()
            if (currentLevel <= levelOfTower) {
                if (Objects.isNull(rightEl) || rightEl!!.data!! >= data) {
                    prev.add(currEl)
                    prevRight.add(rightEl)
                }
            }
            if (Objects.nonNull(rightEl) && rightEl!!.data!! < data) {
                currEl = rightEl
            } else {
                currEl = currEl.down
                currentLevel--
            }
        }
        var downEl: Node<T?>? = null
        for (i in prev.indices.reversed()) {
            val newEl = Node(data, AtomicReference(prevRight[i]), null)
            if (Objects.nonNull(downEl)) {
                newEl.down = downEl
            }
            if (!prev[i]!!.right.compareAndSet(prevRight[i], newEl) && i == prev.size - 1) {
                return false
            }
            downEl = newEl
        }
        return true
    }

    operator fun contains(data: T): Boolean {
        var currEl: Node<T?>? = head
        while (Objects.nonNull(currEl)) {
            val rightEl = currEl!!.right.get()
            currEl = if (Objects.nonNull(currEl.data) && currEl.data!!.compareTo(data) == 0) {
                return true
            } else if (Objects.nonNull(rightEl) && rightEl!!.data!! <= data) {
                rightEl
            } else {
                currEl.down
            }
        }
        return false
    }

    fun nonSafePrint() {
        var curr: Node<T?>? = head
        while (Objects.nonNull(curr!!.down)) {
            curr = curr.down
        }
        curr = curr.right.get()
        while (Objects.nonNull(curr)) {
            println(curr!!.data)
            curr = curr.right.get()
        }
    }

    private fun generateHeight(): Int {
        var lvl = 1
        while (lvl < height && Math.random() < p) {
            lvl++
        }
        return lvl
    }
}