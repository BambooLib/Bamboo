package io.github.deopping.bamboo.event.api

/**
 * Marks a method as an event handler.
 *
 * Handler methods must be public, accept exactly one [Event] parameter,
 * and return Unit.
 *
 * @property priority the handler's execution priority
 * @property ignoreCanceled whether to skip the handler when the event is canceled
 * @author DeOpping
 * @since 1.0.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
public annotation class EventHandler(
    public val priority: EventPriority = EventPriority.NORMAL,
    public val ignoreCanceled: Boolean = false,
)
