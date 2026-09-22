package steph;

import steph.task.Event;

/**
 * The result of parsing an {@code event} command: the {@link Event} it
 * describes, and whether the command carried a trailing {@code /force} flag
 * asking the clash check to be skipped.
 *
 * @param event   The event built from the command's name/from/to text.
 * @param isForce True if the command ended with {@code /force}.
 */
public record ParsedEvent(Event event, boolean isForce) {
}
