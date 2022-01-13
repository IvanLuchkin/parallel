package task2

import akka.actor.ActorRef
import akka.actor.ActorSystem
import task2.action.VisitorCommands.REQUEST_BOOK_FOR_LIBRARY
import task2.action.VisitorCommands.RETURN_BOOK
import task2.actor.Library
import task2.actor.Visitor
import task2.model.BookRequest
import task2.model.LibraryBook
import java.util.concurrent.ConcurrentHashMap


private const val BOOK_1 = "BOOK_1"
private const val BOOK_2 = "BOOK_2"
private const val BOOK_3 = "BOOK_3"

fun main(args: Array<String>) {
    val akkaSystem = ActorSystem.create("library-system")
    val library = akkaSystem.actorOf(Library.props(books), "library")
    val threads = arrayOfNulls<Thread>(1)
    for (i in 0..0) {
        threads[i] = Thread(TestVisitor(akkaSystem, library))
        threads[i]!!.start()
    }
    for (i in 0..0) {
        threads[i]!!.join()
    }
    akkaSystem.terminate()
}

val books: Map<String?, LibraryBook?>
    get() {
        val books: MutableMap<String?, LibraryBook?> = ConcurrentHashMap()
        books[BOOK_1] = LibraryBook(BOOK_1, true, true, true)
        books[BOOK_2] = LibraryBook(BOOK_2, true, false, true)
        books[BOOK_3] = LibraryBook(BOOK_3, true, true, false)
        return books
    }

internal class TestVisitor(private val akkaSystem: ActorSystem, private val library: ActorRef) : Runnable {
    override fun run() {
        val threadName = Thread.currentThread().name
        val visitor = akkaSystem.actorOf(Visitor.props(library), threadName)
        visitor.tell(BookRequest(REQUEST_BOOK_FOR_LIBRARY, BOOK_1), ActorRef.noSender())
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        visitor.tell(BookRequest(RETURN_BOOK, BOOK_1), ActorRef.noSender())
    }
}