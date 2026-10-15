package gnaix;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import gnaix.task.Task;

/**
 * Represents the result of parsing a user command.
 */
public class ParsedCommand {
    private final Command command;
    private final Task task;
    private final int index;
    private final LocalDate date;
    private final String keyword;
    private final Set<String> tags;
    private final String error;

    private ParsedCommand(Command command, Task task, int index,
                          LocalDate date, String keyword, Set<String> tags,
                          String error) {
        this.command = command;
        this.task = task;
        this.index = index;
        this.date = date;
        this.keyword = keyword;
        this.tags = tags == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(new LinkedHashSet<>(tags));
        this.error = error;
    }

    /**
     * Creates a parsed command that does not require additional data.
     *
     * @param command Command represented by this result.
     * @return Parsed command containing the command.
     */
    public static ParsedCommand of(Command command) {
        return new ParsedCommand(
                command, null, -1, null, null, null, null);
    }

    /**
     * Creates a parsed command containing a task.
     *
     * @param command Command represented by this result.
     * @param task Task created from the user's input.
     * @return Parsed command containing the task.
     */
    public static ParsedCommand forTask(Command command, Task task) {
        return new ParsedCommand(
                command, task, -1, null, null, null, null);
    }

    /**
     * Creates a parsed command containing a task index.
     *
     * @param command Command represented by this result.
     * @param index Task index supplied by the user.
     * @return Parsed command containing the index.
     */
    public static ParsedCommand forIndex(Command command, int index) {
        return new ParsedCommand(
                command, null, index, null, null, null, null);
    }

    /**
     * Creates a parsed command containing a date.
     *
     * @param date Date supplied by the user.
     * @return Parsed command containing the date.
     */
    public static ParsedCommand forDate(LocalDate date) {
        return new ParsedCommand(
                Command.DATE, null, -1, date, null, null, null);
    }

    /**
     * Creates a parsed command containing a search keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return Parsed command containing the keyword.
     */
    public static ParsedCommand forKeyword(String keyword) {
        return new ParsedCommand(
                Command.FIND, null, -1, null, keyword, null, null);
    }

    /**
     * Creates a parsed command containing tags to search for.
     *
     * @param tags Tags that matching tasks must contain.
     * @return Parsed command containing the tags.
     */
    public static ParsedCommand forTags(Set<String> tags) {
        return new ParsedCommand(
                Command.TAG, null, -1, null, null, tags, null);
    }

    /**
     * Creates a parsed command representing an error.
     *
     * @param message Error message describing the problem.
     * @return Parsed command containing the error.
     */
    public static ParsedCommand error(String message) {
        return new ParsedCommand(
                Command.UNKNOWN, null, -1, null, null, null, message);
    }

    /**
     * Returns whether this parsed command contains an error.
     *
     * @return True if an error is present.
     */
    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public Command getCommand() {
        return command;
    }

    public Task getTask() {
        return task;
    }

    public int getIndex() {
        return index;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getKeyword() {
        return this.keyword;
    }

    /**
     * Returns the tags supplied to a tag search.
     *
     * @return Read-only set of search tags.
     */
    public Set<String> getTags() {
        return tags;
    }
}
