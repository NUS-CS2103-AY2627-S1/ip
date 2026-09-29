package emma;

import java.util.ArrayList;
import java.util.List;

/**
 * A single thing the user wants to keep track of, and whether it is done.
 * Subclasses decide the type icon and any extra timing details.
 */
public abstract class Task {

    private final String description;
    private boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description what the user typed.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done or not done.
     *
     * @param isDone true to mark done, false to mark not done.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Checks whether this task has been marked done.
     *
     * @return true if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Checks whether some text appears in this task's description.
     *
     * @param text the text to look for, matched exactly as given.
     * @return true if the description contains that text.
     */
    public boolean hasDescriptionContaining(String text) {
        return description.contains(text);
    }

    /**
     * Checks whether this task and another stand for the same thing, so that the
     * same task is not tracked twice. Tasks match when they are of the same type
     * and have the same description; subclasses with dates of their own also
     * compare those. Whether a task is done is deliberately ignored.
     *
     * @param other the task to compare against.
     * @return true if the two tasks are duplicates of each other.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass() && description.equals(other.description);
    }

    /**
     * Returns the type icon shown in the first bracket.
     *
     * @return task type icon.
     */
    protected abstract String getTypeIcon();

    /**
     * Builds this task's JSON object: the fields every task has, followed by
     * any the subclass passes in. Commas and indenting are handled here, so
     * subclasses never deal with separators.
     *
     * @param extraFields field pairs built with {@link Json#buildField}.
     * @return this task as an indented JSON object.
     */
    protected String buildJsonObject(String... extraFields) {
        assert getTypeIcon().length() == 1
                : "a task type icon must be a single letter, but was: " + getTypeIcon();
        List<String> fields = new ArrayList<>();
        fields.add(Json.buildField("type", getTypeIcon()));
        fields.add(Json.buildField("done", isDone));
        fields.add(Json.buildField("description", description));
        fields.addAll(List.of(extraFields));
        return "  {\n    " + String.join(",\n    ", fields) + "\n  }";
    }

    /**
     * Renders the task as an indented JSON object holding the fields needed to
     * rebuild it. Subclasses with timing of their own override this.
     *
     * @return this task as JSON.
     */
    public String toJson() {
        return buildJsonObject();
    }

    /** Renders the task as "[icon][x] description"; subclasses append their own details. */
    @Override
    public String toString() {
        String statusIcon = isDone ? "x" : " ";
        return "[" + getTypeIcon() + "][" + statusIcon + "] " + description;
    }
}
