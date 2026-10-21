package io.github.deopping.bamboo.database.internal

import io.github.deopping.bamboo.database.api.DatabaseException
import java.sql.SQLException
import java.util.Collections
import java.util.IdentityHashMap

internal object DatabaseFailures {

    private const val MAX_CAUSE_DEPTH = 8

    private val uriUserInfo = Regex(
        """(?i)\b([a-z][a-z0-9+.-]*://)[^\s/?#@]*@"""
    )

    private val sensitiveAssignment = Regex(
        """
            (["']? \b [A-Za-z0-9_.-]*
            (?: user(?:name)? | password | passwd | pwd | token | secret
            | api[-_]?key | access[-_]?key | authorization | credential
            )
            [A-Za-z0-9_.-]* ["']? \s* [=:] \s*
            )
            (?:"[^"]*" | '[^']*' | [^&\s,;)\]}]+)
            """.trimIndent(),
        setOf(
            RegexOption.IGNORE_CASE,
            RegexOption.COMMENTS
        )
    )

    private val bearerToken = Regex(
        """(?i)\b(Bearer\s+)[A-Za-z0-9._~+/-]+=*"""
    )

    /**
     * Creates an exception with a sanitized message.
     */
    fun create(message: String): DatabaseException =
        DatabaseException(redact(message))

    /**
     * Creates an exception with a sanitized message and diagnostic cause.
     *
     * The original throwable is not retained. Its type, stack trace, and
     * selected non-sensitive diagnostic information are preserved instead.
     */
    fun create(
        message: String,
        cause: Throwable?
    ): DatabaseException {
        val exception = DatabaseException(redact(message))

        if (cause != null) {
            exception.initCause(snapshot(cause))
        }

        return exception
    }

    /**
     * Converts an unexpected failure into a safe database exception.
     */
    fun from(cause: Throwable): DatabaseException =
        create(describe(cause), cause)

    /**
     * Attaches a safe diagnostic snapshot of a potentially
     * credential-bearing suppressed exception.
     */
    fun addSuppressed(
        target: Throwable,
        failure: Throwable
    ) {
        if (target !== failure) {
            target.addSuppressed(snapshot(failure))
        }
    }

    private fun redact(message: String): String {
        var result = message

        result = uriUserInfo.replace(result) {
            "${it.groupValues[1]}<redacted>@"
        }

        result = sensitiveAssignment.replace(result) {
            "${it.groupValues[1]}<redacted>"
        }

        result = bearerToken.replace(result) {
            "${it.groupValues[1]}<redacted>"
        }

        return result
    }

    private fun snapshot(source: Throwable): Throwable {
        val diagnostic = SanitizedDatabaseCause(describe(source))
        diagnostic.stackTrace = source.stackTrace
        return diagnostic
    }

    private fun describe(source: Throwable): String {
        val seen = Collections.newSetFromMap(
            IdentityHashMap<Throwable, Boolean>()
        )

        val chain = mutableListOf<String>()
        var current: Throwable? = source

        while (current != null &&
            chain.size < MAX_CAUSE_DEPTH &&
            seen.add(current)
        ) {
            chain += describeType(current)
            current = current.cause
        }

        val suppressedTypes = source.suppressed
            .take(3)
            .map { it.javaClass.name }

        return buildString {
            append("Underlying failure; exception messages omitted: ")
            append(chain.joinToString(" <- "))

            if (suppressedTypes.isNotEmpty()) {
                append("; suppressed failure types: ")
                append(suppressedTypes.joinToString(", "))
            }
        }
    }

    private fun describeType(failure: Throwable): String =
        when (failure) {
            is SQLException ->
                "${failure.javaClass.name} " +
                        "(SQLState=${failure.sqlState ?: "unknown"}, " +
                        "vendorCode=${failure.errorCode})"

            else -> failure.javaClass.name
        }

    private class SanitizedDatabaseCause(
        message: String
    ) : RuntimeException(message)

}