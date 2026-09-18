package seedu.mattchatbot;

/**
 * One thing the chatbot says back, together with whether it is a complaint.
 * <p>
 * A GUI shows an error differently from an ordinary reply, and it cannot tell
 * the two apart from the text alone. Carrying the distinction next to the text
 * saves the GUI from having to guess -- by matching on wording, say, which
 * would quietly stop working the first time a message was reworded.
 *
 * @param text    what to show the user
 * @param isError whether this reply reports something the user got wrong
 */
public record Reply(String text, boolean isError) {
}
