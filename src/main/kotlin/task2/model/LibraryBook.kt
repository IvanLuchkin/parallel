package task2.model

data class LibraryBook(
    override var name: String,
    var available: Boolean,
    val availableForHome: Boolean,
    val availableForLibrary: Boolean
) : Book(name)
