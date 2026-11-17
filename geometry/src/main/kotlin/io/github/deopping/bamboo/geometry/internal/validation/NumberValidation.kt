package io.github.deopping.bamboo.geometry.internal.validation

internal object NumberValidation {
    fun requireFinite(
        value: Double,
        type: String,
    ) {
        require(value.isFinite()) {
            "$type must be finite."
        }
    }

    fun requireFiniteNonZero(
        value: Double,
        name: String,
    ) {
        require(value.isFinite() && value != 0.0) {
            "$name must be finite and non-zero."
        }
    }

    fun requireFiniteNonNegative(
        value: Double,
        name: String,
    ) {
        require(value.isFinite() && value >= 0.0) {
            "$name must be finite and non-negative."
        }
    }

    fun requireFiniteAndBetween(
        value: Double,
        min: Double,
        max: Double,
        type: String,
    ) {
        require(value.isFinite() && value in min..max) {
            "$type must be finite and between $min and $max"
        }
    }
}
