package io.github.deopping.bamboo.database.api

class DatabaseException : RuntimeException {

    constructor(message: String, throwable: Throwable?) : super(message, throwable)
    constructor(message: String) : super(message)
    constructor(throwable: Throwable) : super(throwable)

}