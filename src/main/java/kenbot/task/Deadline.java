package kenbot.task;

import kenbot.KenbotException;
import kenbot.Parser;

/** Represents a task that must be completed by a specified time. */
public class Deadline extends Task {
    private final TaskDate by;

    /**
     * Creates a deadline task.
     *
     * @param description the task description
     * @param by when the task is due
     */
    public Deadline(String description, TaskDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline from the text typed after the command word.
     *
     * @param argument the description and deadline, separated by {@code /by}
     * @return the new deadline
     * @throws KenbotException if either part is missing or blank
     */
    public static Deadline of(String argument) throws KenbotException {
        // Tags come off before /by is looked for, so this method never has to
        // know that a date might be followed by a tag.
        Tag.TaggedText split = Tag.splitTrailingTags(argument);
        Parser.requireAtMostOne(split.text(), " /by ");
        String[] parts = split.text().split(" /by ", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new KenbotException(
                    "Deadlines need a description and a /by. Like:\n"
                    + "  deadline return book /by 2019-10-15");
        }
        Deadline deadline = new Deadline(parts[0].trim(), TaskDate.of(parts[1]));
        split.tags().forEach(deadline::addTag);
        return deadline;
    }

    /**
     * Returns the letter a deadline is saved under.
     *
     * @return {@code "D"}
     */
    @Override
    protected String getTypeCode() {
        return "D";
    }

    /**
     * Returns the due date as it is shown on screen.
     *
     * @return {@code (by: ...)}, with a leading space
     */
    @Override
    protected String getDisplayDetails() {
        return " (by: " + by + ")";
    }

    /**
     * Returns the due date as the save file holds it.
     *
     * @return the one field a deadline adds after its description
     */
    @Override
    protected String getStorableDetails() {
        // by.toStorable() rather than by on its own: a plain + would use
        // toString(), whose display format cannot be read back in.
        return by.toStorable();
    }
}
