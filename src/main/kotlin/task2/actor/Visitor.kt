package task2.actor

import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.Props
import akka.japi.pf.ReceiveBuilder
import task2.action.LibraryCommands.APPROVE_GIVING_BOOK_FOR_HOME
import task2.action.LibraryCommands.APPROVE_GIVING_BOOK_FOR_LIBRARY
import task2.action.LibraryCommands.REJECT_GIVING_BOOK
import task2.action.VisitorCommands.RETURN_BOOK
import task2.model.Book
import task2.model.BookRequest
import task2.model.BookResponse


class Visitor (
    private val library: ActorRef
) : AbstractActor() {
    private val booksForLibrary: MutableMap<String?, Book?> = HashMap()
    private val booksForHome: MutableMap<String, Book> = HashMap()
    override fun createReceive(): Receive {
        return ReceiveBuilder.create()
            .match(BookRequest::class.java, this::handleBookRequest)
            .match(BookResponse::class.java, this::handleBookResponse)
            .build()
    }

    private fun handleBookRequest(request: BookRequest) {
        val command: String = request.command
        println("Visitor(" + self().path().name() + "): tell visitor to " + command)
        if (RETURN_BOOK == command) {
            returnBook(request.name)
            return
        }
        requestBook(command, request.name)
    }

    private fun handleBookResponse(response: BookResponse) {
        when (response.command) {
            APPROVE_GIVING_BOOK_FOR_LIBRARY -> {
                booksForLibrary[response.book!!.name] = response.book
                println("Visitor(" + self().path().name() + "): took book for library")
            }
            APPROVE_GIVING_BOOK_FOR_HOME -> {
                booksForHome[response.book!!.name] = response.book
                println("Visitor(" + self().path().name() + "): took book for home")
            }
            REJECT_GIVING_BOOK -> {
                println("Visitor(" + self().path().name() + "): unable to take a book")
            }
            else -> {
                println("Visitor(" + self().path().name() + "): unknown command for visitor handler")
            }
        }
    }

    private fun requestBook(command: String?, bookName: String?) {
        library.tell(BookRequest(command!!, bookName!!), self)
    }

    private fun returnBook(bookName: String) {
        if (booksForLibrary.containsKey(bookName)) {
            library.tell(BookResponse(RETURN_BOOK, booksForLibrary[bookName]), ActorRef.noSender())
            booksForLibrary.remove(bookName)
            println("Visitor(" + self().path().name() + "): book returned from library")
        } else if (booksForHome.containsKey(bookName)) {
            library.tell(BookResponse(RETURN_BOOK, booksForLibrary[bookName]), ActorRef.noSender())
            booksForHome.remove(bookName)
            println("Visitor(" + self().path().name() + "): book returned from home")
        } else {
            println("Visitor(" + self().path().name() + "): unable to return book that you don't have")
        }
    }

    companion object {
        fun props(library: ActorRef?): Props {
            return Props.create(Visitor::class.java, library)
        }
    }
}