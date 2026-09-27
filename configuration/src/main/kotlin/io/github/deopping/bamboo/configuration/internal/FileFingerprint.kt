package io.github.deopping.bamboo.configuration.internal

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.security.MessageDigest

data class FileFingerprint(
    val exists: Boolean,
    val size: Long,
    val lastModified: FileTime?,
    val contentHash: String?
) {

    companion object {

        val NON_EXISTING
            get() = FileFingerprint(
                exists = false,
                size = -1L,
                lastModified = null,
                contentHash = null
            )

        fun capture(
            path: Path,
            includeContentHash: Boolean = false
        ): FileFingerprint {
            if (!Files.exists(path)) {
                return NON_EXISTING
            }

            val hash =
                if (includeContentHash) hash(path)
                else null

            return FileFingerprint(
                exists = true,
                size = Files.size(path),
                lastModified = Files.getLastModifiedTime(path),
                contentHash = hash
            )
        }

        private fun hash(path: Path): String {
            val digest = MessageDigest.getInstance("SHA-256")

            Files.newInputStream(path).use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                while (true) {
                    val read = input.read(buffer)

                    if (read == -1) {
                        break
                    }

                    digest.update(buffer, 0, read)
                }
            }

            return digest.digest()
                .joinToString("") { "%02x".format(it) }
        }

    }

}
