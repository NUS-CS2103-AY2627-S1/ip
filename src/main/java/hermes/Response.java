package hermes;

/**
 * What Hermes has to say in reply to one line of input.
 *
 * <p>The reply on its own is not enough for a caller that must close itself
 * once the conversation is over. Letting such a caller recognise the parting
 * word for itself would put a second copy of the command vocabulary outside
 * the parser, so the decision is carried back with the text instead.
 *
 * <p>Whether the reply reports a problem travels the same way. The window shows
 * complaints differently from ordinary replies, and working that out from the
 * wording would break the first time a message was reworded.
 *
 * @param text what to show the user.
 * @param isExit whether Hermes should stop after this reply.
 * @param isError whether the reply explains why the input could not be carried out.
 */
public record Response(String text, boolean isExit, boolean isError) {
}
