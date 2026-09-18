package aster;

/**
 * Aster's reply to one message, in a form a graphical interface can show.
 *
 * <p>The message holds every line Aster says in reply, joined by line breaks. Unlike
 * the text interface, it carries no dividers, because a graphical interface frames each
 * reply in its own way.
 *
 * <p>A reply that contains an error says so, so that an interface can show it apart from
 * ordinary replies. The flag records whether an error was reported while the reply was
 * produced, rather than anything read from the wording, so a reply that only explains
 * that nothing needed doing is never mistaken for one.
 *
 * @param message the reply to show, with its lines separated by {@code \n}.
 * @param isExit whether the conversation is over, so that no further message should be
 *     taken.
 * @param isError whether the reply contains an error, including a change that was made
 *     but could not be saved.
 */
public record Response(String message, boolean isExit, boolean isError) {
}
