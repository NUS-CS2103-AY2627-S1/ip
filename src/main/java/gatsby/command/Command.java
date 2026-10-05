package gatsby.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

import gatsby.exception.DuplicateTaskException;
import gatsby.exception.EmptyMarkingException;
import gatsby.exception.EmptyPayloadException;
import gatsby.exception.GatsbyException;
import gatsby.exception.InvalidTaskException;
import gatsby.model.Task;
import gatsby.model.TaskList;
import gatsby.storage.Storage;
import gatsby.ui.Ui;

/**
 * An executable action requested by the user.
 *
 * Concrete commands gradually take responsibility for one user action. This
 * base class gives Gatsby a common execution protocol and an exit signal.
 */
public abstract class Command {
    /** The character reserved for separating fields in the save file. */
    private static final String FIELD_SEPARATOR = "|";

    /** Creates a command. */
    public Command() {
    }

    /**
     * Performs this command using Gatsby's collaborators.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui) throws GatsbyException;

    /**
     * Checks whether this command ends the current Gatsby session.
     *
     * @return false for ordinary commands
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Adds a newly created task, saves it, and prints Gatsby's standard success
     * response for task-creation commands.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @param task the newly created task
     */
    protected void addTask(TaskList tasks, Ui ui, Task task) throws DuplicateTaskException {
        assert tasks != null : "A task-creation command requires a task list.";
        assert ui != null : "A task-creation command requires a UI handler.";
        assert task != null : "A task-creation command must create a task before adding it.";
        if (tasks.containsEquivalent(task)) {
            throw new DuplicateTaskException(" OOPS! You already have a task with the same details.");
        }
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "A successful task addition must increase the list size by one.";
        assert tasks.get(previousSize) == task : "A newly created task must be appended to the task list.";
        Storage.save(tasks.asList());
        ui.printLine(" Got it. I've added this task:");
        ui.printLine("  " + task);
        ui.printLine(" Now you have " + tasks.size()
                + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }

    /**
     * Validates a description or date fragment shared by task-creation commands.
     *
     * @param text the candidate text
     * @param errorMessage the message shown when the text is empty
     * @return the trimmed, valid text
     * @throws EmptyPayloadException when the text is empty or contains the field separator
     */
    protected String requireText(String text, String errorMessage) throws EmptyPayloadException {
        String trimmedText = text == null ? "" : text.strip();
        if (trimmedText.isEmpty()) {
            throw new EmptyPayloadException(errorMessage);
        }
        if (trimmedText.contains(FIELD_SEPARATOR)) {
            throw new EmptyPayloadException(" OOPS! Please leave out the \"" + FIELD_SEPARATOR
                    + "\" character; I use it to separate fields in my save file.");
        }
        assert !trimmedText.isEmpty() : "Validated text must not be empty.";
        assert !trimmedText.contains(FIELD_SEPARATOR)
                : "Validated text must not contain the storage field separator.";
        return trimmedText;
    }

    /**
     * Splits a payload around a case-insensitive keyword such as {@code /by}.
     *
     * @param payload the text to split
     * @param keyword the keyword to split on
     * @param errorMessage the message shown when the keyword is absent
     * @return the text before and after the keyword
     * @throws EmptyPayloadException when the keyword does not appear
     */
    protected String[] splitOnKeyword(String payload, String keyword, String errorMessage)
            throws EmptyPayloadException {
        String safePayload = payload == null ? "" : payload;
        String[] parts = safePayload.split("(?i)(?<!\\S)" + Pattern.quote(keyword) + "(?!\\S)", -1);
        if (parts.length < 2) {
            throw new EmptyPayloadException(errorMessage);
        }
        if (parts.length > 2) {
            throw new EmptyPayloadException(" OOPS! The \"" + keyword
                    + "\" parameter can only be specified once.");
        }
        return new String[] {parts[0].strip(), parts[1].strip()};
    }

    /**
     * Parses a user-facing date-time using Gatsby's documented format.
     *
     * @param value the date-time entered by the user
     * @param description the kind of date-time being parsed
     * @return the parsed date-time
     * @throws EmptyPayloadException when the date-time is malformed or impossible
     */
    protected LocalDateTime parseDateTime(String value, String description) throws EmptyPayloadException {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("uuuu-MM-dd HHmm")
                .toFormatter(Locale.ROOT)
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalDateTime.parse(value.strip(), formatter);
        } catch (DateTimeParseException e) {
            throw new EmptyPayloadException(" OOPS! Please enter a valid " + description
                    + " in the format yyyy-MM-dd HHmm (for example, 2019-12-02 1800).");
        }
    }

    /**
     * Resolves a one-based user task number to the corresponding zero-based index.
     *
     * @param tasks the current task list
     * @param payload the text entered after a task-selection command
     * @return the zero-based index of the selected task
     * @throws InvalidTaskException when the payload is not a valid task number
     */
    protected int parseTaskIndex(TaskList tasks, String payload) throws InvalidTaskException {
        String normalizedPayload = payload == null ? "" : payload.strip();
        if (!normalizedPayload.matches("[0-9]+")) {
            throw new InvalidTaskException(" OOPS! \"" + normalizedPayload
                    + "\" isn't a task number! :(");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(normalizedPayload);
        } catch (NumberFormatException e) {
            throw new InvalidTaskException(" OOPS! \"" + normalizedPayload
                    + "\" is too large to be a task number! :(");
        }
        if (tasks.isEmpty()) {
            throw new InvalidTaskException(" OOPS! Your list is empty, so there's no task "
                    + taskNumber + "!");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new InvalidTaskException(" OOPS! There's no task " + taskNumber
                    + "! Pick a number from 1 to " + tasks.size() + ".");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must map to a valid zero-based index.";
        return taskIndex;
    }

    /**
     * Marks or unmarks one task and reports whether its state actually changed.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @param payload the selected task number
     * @param isMarking true to mark the task, false to unmark it
     * @throws GatsbyException when the task number is missing or invalid
     */
    protected void updateTaskStatus(TaskList tasks, Ui ui, String payload, boolean isMarking)
            throws GatsbyException {
        String normalizedPayload = payload == null ? "" : payload.strip();
        if (normalizedPayload.isEmpty()) {
            throw new EmptyMarkingException(isMarking
                    ? " OOPS! We can't be marking nothing as done!"
                    : " OOPS! We can't be marking nothing as undone!");
        }

        Task task = tasks.get(parseTaskIndex(tasks, normalizedPayload));
        boolean wasAlreadyInState = task.isDone() == isMarking;
        if (isMarking) {
            task.markDone();
        } else {
            task.markUndone();
        }
        assert task.isDone() == isMarking : "Task status must match the requested mark or unmark operation.";
        Storage.save(tasks.asList());

        if (wasAlreadyInState) {
            ui.printLine(isMarking
                    ? " That one was already done, but sure:"
                    : " That one wasn't done yet, but sure:");
        } else {
            ui.printLine(isMarking
                    ? " Nice! I've marked this task as done:"
                    : " OK, I've marked this task as not done yet:");
        }
        ui.printLine("  " + task);
    }

    /**
     * Prints the current task count with correct singular/plural wording.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     */
    protected void printTaskCount(TaskList tasks, Ui ui) {
        ui.printLine(" Now you have " + tasks.size()
                + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
