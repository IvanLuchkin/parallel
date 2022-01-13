package task2.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import task2.action.LibraryCommands.APPROVE_GIVING_BOOK_FOR_HOME
import task2.action.LibraryCommands.APPROVE_GIVING_BOOK_FOR_LIBRARY
import task2.action.LibraryCommands.REJECT_GIVING_BOOK
import task2.action.VisitorCommands.REQUEST_BOOK_FOR_HOME
import task2.action.VisitorCommands.REQUEST_BOOK_FOR_LIBRARY
import task2.action.VisitorCommands.RETURN_BOOK
import task2.model.Book
import task2.model.BookRequest
import task2.model.BookResponse
import task2.model.LibraryBook
import kotlin.String


class Library(
    books: Map<String, LibraryBook>
) : AbstractActor() {
    private val books: Map<String, LibraryBook>

    init {
        this.books = books
    }

    override fun createReceive(): Receive {
        return ReceiveBuilder.create()
            .match(BookRequest::class.java, this::handleBookRequest)
            .match(BookResponse::class.java, this::handleBookResponse)
            .build()
    }

    private fun handleBookRequest(request: BookRequest) {
        val visitor = sender
        val bookName: String = request.name
        if (!books.containsKey(bookName) || !books[bookName]!!.available) {
            println("Library: rejected to give book (no book)")
            rejectGivingBook(visitor)
        }
        val book: LibraryBook? = books[bookName]
        when (request.command) {
            REQUEST_BOOK_FOR_LIBRARY -> {
                if (!book!!.availableForLibrary) {
                    println("Library: rejected to give book for library")
                    rejectGivingBook(visitor)
                } else {
                    println("Library: approved to give book for library")
                    book.available = false
                    approveGivingBook(visitor, APPROVE_GIVING_BOOK_FOR_LIBRARY, book)
                }
            }
            REQUEST_BOOK_FOR_HOME -> {
                if (!book!!.availableForHome) {
                    println("Library: rejected to give book for home")
                    rejectGivingBook(visitor)
                } else {
                    println("Library: approved to give book for home")
                    book.available = false
                    approveGivingBook(visitor, APPROVE_GIVING_BOOK_FOR_HOME, book)
                }
            }
            else -> {
                println("Library: unknown command")
            }
        }
    }

    private fun handleBookResponse(response: BookResponse) {
        val command: String = response.command
        val bookName: String = response.book!!.name
        if (RETURN_BOOK == command && books.containsKey(bookName)) {
            val book: LibraryBook? = books[bookName]
            book!!.available = true
            println("Library: book returned")
        } else {
            println("Library: unknown command for library handler")
        }
    }

    private fun approveGivingBook(visitor: ActorRef, command: String, book: Book?) {
        visitor.tell(BookResponse(command, book!!), ActorRef.noSender())
    }

    private fun rejectGivingBook(visitor: ActorRef) {
        visitor.tell(BookResponse(REJECT_GIVING_BOOK, null), ActorRef.noSender())
    }

    companion object {
        fun props(books: Map<String?, LibraryBook?>?): Props {
            return Props.create(Library::class.java, books)
        }
    }
}