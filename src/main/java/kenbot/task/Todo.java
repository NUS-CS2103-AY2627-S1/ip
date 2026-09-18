package kenbot.task;

import kenbot.KenbotException;

/** Represents a task without a date or time. */
public class Todo extends Task {

    /**
     * Creates a to-do task.
     *
     * @param description the task description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a to-do from the text typed after the command word.
     *
     * @param argument the description the user typed
     * @return the new to-do
     * @throws KenbotException if the description is missing or blank
     */
    public static Todo of(String argument) throws KenbotException {
        // Tags come off first, so the checks below judge the description alone
        // and never mistake a trailing tag for one.
        Tag.TaggedText split = Tag.splitTrailingTags(argument);
        if (split.text().isBlank()) {
            throw new KenbotException("Need a description for that. Like:\n"
                    + "  todo read book");
        }

        Todo todo = new Todo(split.text().trim());
        split.tags().forEach(todo::addTag);
        return todo;
    }

    /**
     * Returns the letter a to-do is saved under.
     *
     * @return {@code "T"}
     */
    @Override
    protected String getTypeCode() {
        return "T";
    }
}
