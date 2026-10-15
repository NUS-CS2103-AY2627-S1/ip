package surveyprogram.processor;

/**
 * Contains the dialogue and continuation state produced by one command.
 *
 * @param response dialogue to show to the user
 * @param shouldContinue whether the application should accept another command
 * @param replyType visual category of the reply
 */
public record CommandResult(String response, boolean shouldContinue, ReplyType replyType) {
    /**
     * Creates a result that uses the unchanged default reply style.
     *
     * @param response dialogue to show to the user
     * @param shouldContinue whether the application should accept another command
     */
    public CommandResult(String response, boolean shouldContinue) {
        this(response, shouldContinue, ReplyType.DEFAULT);
    }
}
