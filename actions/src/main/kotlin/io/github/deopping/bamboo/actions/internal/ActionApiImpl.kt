package io.github.deopping.bamboo.actions.internal

import io.github.deopping.bamboo.actions.ActionApi
import io.github.deopping.bamboo.actions.api.ActionRegistry

internal class ActionApiImpl : ActionApi {

    override fun <C> createRegistry(): ActionRegistry<C> = ActionRegistryImpl()

}