package io.github.deopping.bamboo.geometry.internal.validation

internal object VectorValidation {
    fun requirePositiveScale(scale: Double) {
        require(scale > 0.0) {
            "Cannot set the length of a zero-length vector."
        }
    }
}
