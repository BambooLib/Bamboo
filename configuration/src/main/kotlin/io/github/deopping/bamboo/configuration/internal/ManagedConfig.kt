package io.github.deopping.bamboo.configuration.internal

internal interface ManagedConfig {

    fun reloadIfChanged(): Boolean

}