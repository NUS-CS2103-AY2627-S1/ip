package panda.exception;

/**
 * Signals that a task-creation command has no description.
 *
 * Keeps missing-description errors consistent for every task type.
 */
public class EmptyDescriptionException extends PandaException {
    /**
     * Creates a panda-themed missing-description error.
     *
     * @param taskType the kind of task being created.
     */
    public EmptyDescriptionException(String taskType) {
        super("OOPS!!! This panda needs " + getArticleFor(taskType) + " " + taskType
                + " description before it can get to work.");
    }

    /**
     * Chooses the correct indefinite article for the task type.
     *
     * Keeps generated error messages grammatically readable.
     *
     * @param taskType the task type used in the message.
     * @return `an` for vowel-starting types, otherwise `a`.
     */
    private static String getArticleFor(String taskType) {
        return "aeiou".indexOf(Character.toLowerCase(taskType.charAt(0))) >= 0 ? "an" : "a";
    }
}
