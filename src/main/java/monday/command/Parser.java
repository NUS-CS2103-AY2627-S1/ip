package monday.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import monday.exception.MondayException;
import monday.storage.Storage;
import monday.task.Deadline;
import monday.task.Event;
import monday.task.Task;
import monday.task.TaskList;
import monday.task.Todo;
import monday.ui.Ui;
import monday.util.DateTimeFormat;

/**
 * Interprets user commands and applies them to the task list.
 */
public class Parser {
    /**
     * Processes one user command.
     *
     * @param command command entered by the user.
     * @param tasks task list to update or display.
     * @param ui user interface used to display results.
     * @return true if Monday should continue running, false otherwise.
     * @throws MondayException if the command is invalid.
     */
    public boolean executeCommand(String command, TaskList tasks, Ui ui) throws MondayException {
        if (command == null) {
            throw new MondayException("Please enter a command.");
        }

        command = command.trim();
        if (command.equals("bye")) {
            Storage.saveTask(tasks);
            ui.showResponse("Bye. Hope to see you again soon!");
            return false;
        }

        if (command.equals("list")) {
            ui.showTaskList(tasks);
            return true;
        }

        if (hasCommandWord(command, "mark")) {
            markTask(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "unmark")) {
            unmarkTask(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "delete")) {
            deleteTask(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "todo")) {
            addTodo(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "deadline")) {
            addDeadline(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "event")) {
            addEvent(command, tasks, ui);
            return true;
        }

        if (hasCommandWord(command, "find")) {
            findTask(command, tasks, ui);
            return true;
        }

        throw new MondayException("Please tell me your task or which task to mark/unmark.");
    }

    /**
     * Marks a specified task as completed.
     * Users should enter the command in the following format:
     * {@code mark <task number>}.
     *
     * @param command command containing the task number.
     * @param tasks task list containing the task.
     * @param ui user interface used to display the result.
     * @throws MondayException if the task number is invalid.
     */
    private void markTask(String command, TaskList tasks, Ui ui) throws MondayException {
        int index = getTaskIndex(command, "mark", tasks,
                "Please tell me which task number to mark.",
                "Please tell me a valid task number to mark.");

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            Storage.saveTask(tasks);
        } catch (MondayException e) {
            restoreCompletionStatus(task, wasDone);
            throw e;
        }
        ui.showResponse("Nice! I've marked this task as done:", "  " + tasks.get(index));
    }

    /**
     * Marks a specified task as not completed.
     * Users should enter the command in the following format:
     * {@code unmark <task number>}.
     *
     * @param command command containing the task number.
     * @param tasks task list containing the task.
     * @param ui user interface used to display the result.
     * @throws MondayException if the task number is invalid.
     */
    private void unmarkTask(String command, TaskList tasks, Ui ui) throws MondayException {
        int index = getTaskIndex(command, "unmark", tasks,
                "Please tell me which task number to unmark.",
                "Please tell me a valid task number to unmark.");

        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            Storage.saveTask(tasks);
        } catch (MondayException e) {
            restoreCompletionStatus(task, wasDone);
            throw e;
        }
        ui.showResponse("OK, I've marked this task as not done yet:", "  " + tasks.get(index));
    }

    /**
     * Removes a specified task from the task list.
     * Users should enter the command in the following format:
     * {@code delete <task number>}.
     *
     * @param command command containing the task number.
     * @param tasks task list containing the task.
     * @param ui user interface used to display the result.
     * @throws MondayException if the task number is invalid.
     */
    private void deleteTask(String command, TaskList tasks, Ui ui) throws MondayException {
        int index = getTaskIndex(command, "delete", tasks,
                "Please tell me which task number to delete.",
                "Please tell me a valid task number to delete.");

        Task deletedTask = tasks.remove(index);
        try {
            Storage.saveTask(tasks);
        } catch (MondayException e) {
            tasks.add(index, deletedTask);
            throw e;
        }
        ui.showResponse("Noted. I've removed this task:", "  " + deletedTask,
                "Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Adds a todo task to the task list.
     * Users should enter the command in the following format:
     * {@code todo <description> /notes <notes>}.
     *
     * @param command command containing the todo description.
     * @param tasks task list to update.
     * @param ui user interface used to display the result.
     * @throws MondayException if the description is missing.
     */
    private void addTodo(String command, TaskList tasks, Ui ui) throws MondayException {
        int notesIndex = findNotesDirective(command);
        int taskDetailsEnd = notesIndex == -1 ? command.length() : notesIndex;
        String description = command.substring("todo".length(), taskDetailsEnd).trim();
        if (description.isEmpty()) {
            throw new MondayException("Please tell me your todo task.");
        }

        validateDescription(description);
        String notes = parseNotes(command, notesIndex);
        addTask(new Todo(description, notes), tasks);
        showAddedTask(tasks, ui);
    }

    /**
     * Adds a deadline task to the task list.
     * Users should enter the command in the following format:
     * {@code deadline <description> /by <deadline> /notes <notes>}.
     *
     * @param command command containing the deadline details.
     * @param tasks task list to update.
     * @param ui user interface used to display the result.
     * @throws MondayException if the deadline details are invalid.
     */
    private void addDeadline(String command, TaskList tasks, Ui ui) throws MondayException {
        if (command.length() == "deadline".length()) {
            throw new MondayException("Please tell me your task.");
        }

        int notesIndex = findNotesDirective(command);
        int taskDetailsEnd = notesIndex == -1 ? command.length() : notesIndex;
        String taskDetails = command.substring(0, taskDetailsEnd);

        int byIndex = findDirective(taskDetails, "/by");
        if (byIndex == -1) {
            throw new MondayException("Please use the '/by' command.");
        }

        if (findDirective(taskDetails.substring(byIndex + "/by".length()), "/by") != -1) {
            throw new MondayException("Please specify '/by' only once.");
        }

        String description = command.substring("deadline".length(), byIndex).trim();
        if (description.isEmpty()) {
            throw new MondayException("Please tell me your task.");
        }
        validateDescription(description);

        String deadline = taskDetails.substring(byIndex + "/by".length()).trim();
        if (deadline.isEmpty()) {
            throw new MondayException("Please tell me your deadline.");
        }

        String[] dateTimeParts = deadline.split("\\s+");
        if (dateTimeParts.length > 2) {
            throw new MondayException("Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        }

        try {
            LocalDate date = LocalDate.parse(dateTimeParts[0], DateTimeFormat.INPUT_DATE.getFormatter());
            LocalTime time = parseOptionalTime(dateTimeParts);
            String notes = parseNotes(command, notesIndex);
            addTask(new Deadline(description, date, time, notes), tasks);
            showAddedTask(tasks, ui);
        } catch (DateTimeParseException e) {
            throw new MondayException("Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        }
    }

    /**
     * Adds an event task to the task list.
     * Users should enter the command in the following format:
     * {@code event <description> /from <start time> /to <end time> /notes <notes>}.
     *
     * @param command command containing the event details.
     * @param tasks task list to update.
     * @param ui user interface used to display the result.
     * @throws MondayException if the event details are invalid.
     */
    private void addEvent(String command, TaskList tasks, Ui ui) throws MondayException {
        if (command.length() == "event".length()) {
            throw new MondayException("Please tell me your task.");
        }

        int notesIndex = findNotesDirective(command);
        int taskDetailsEnd = notesIndex == -1 ? command.length() : notesIndex;
        String taskDetails = command.substring(0, taskDetailsEnd);

        int fromIndex = findDirective(taskDetails, "/from");
        int toIndex = findDirective(taskDetails, "/to");
        if (fromIndex == -1 || toIndex == -1) {
            throw new MondayException("Please use the '/from' and '/to' commands.");
        }

        if (findDirective(taskDetails.substring(fromIndex + "/from".length()), "/from") != -1
                || findDirective(taskDetails.substring(toIndex + "/to".length()), "/to") != -1) {
            throw new MondayException("Please specify '/from' and '/to' only once each.");
        }

        if (fromIndex > toIndex) {
            throw new MondayException("The '/from' command needs to come before the '/to' command.");
        }

        String description = taskDetails.substring("event".length(), fromIndex).trim();
        if (description.isEmpty()) {
            throw new MondayException("Please tell me your task.");
        }
        validateDescription(description);

        String startPeriod = taskDetails.substring(fromIndex + "/from".length(), toIndex).trim();
        if (startPeriod.isEmpty()) {
            throw new MondayException("Please tell me your start time.");
        }

        String endPeriod = taskDetails.substring(toIndex + "/to".length()).trim();
        if (endPeriod.isEmpty()) {
            throw new MondayException("Please tell me your end time.");
        }

        String[] startParts = startPeriod.split("\\s+");
        String[] endParts = endPeriod.split("\\s+");
        if (startParts.length > 2 || endParts.length > 2) {
            throw new MondayException("Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        }

        try {
            LocalDate startDate = LocalDate.parse(startParts[0], DateTimeFormat.INPUT_DATE.getFormatter());
            LocalTime startTime = parseOptionalTime(startParts);

            LocalDate endDate = LocalDate.parse(endParts[0], DateTimeFormat.INPUT_DATE.getFormatter());
            LocalTime endTime = parseOptionalTime(endParts);

            validateEventRange(startDate, startTime, endDate, endTime);
            String notes = parseNotes(command, notesIndex);
            addTask(new Event(description, startDate, startTime, endDate, endTime, notes), tasks);
            showAddedTask(tasks, ui);
        } catch (DateTimeParseException e) {
            throw new MondayException("Please use the format dd/MM/yyyy or dd/MM/yyyy HHmm.");
        }
    }

    /**
     * Finds and displays tasks whose descriptions contain a keyword.
     * Users should enter the command in the following format:
     * {@code find <keyword>}.
     *
     * @param command command containing the keyword
     * @param tasks task list to search
     * @param ui user interface used to display the matching tasks
     * @throws MondayException if the keyword is missing
     */
    private void findTask(String command, TaskList tasks, Ui ui) throws MondayException {
        String keyword = command.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new MondayException("Please tell me what to find.");
        }

        TaskList matchingTasks = tasks.find(keyword);
        ui.showMatchingTaskList(matchingTasks);
    }

    /**
     * Returns a zero-based task index from a command containing a task number.
     *
     * @param command command containing the task number.
     * @param commandWord command word at the start of the command.
     * @param tasks task list used to validate the task number.
     * @param missingNumberMessage message displayed when a number is absent.
     * @param invalidNumberMessage message displayed when a number is invalid.
     * @return zero-based index of the requested task.
     * @throws MondayException if the task number is invalid.
     */
    private int getTaskIndex(String command, String commandWord, TaskList tasks,
            String missingNumberMessage, String invalidNumberMessage)
            throws MondayException {
        String numberText = command.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new MondayException(missingNumberMessage);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new MondayException("Please enter a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MondayException(invalidNumberMessage);
        }

        return taskNumber - 1;
    }

    /**
     * Parses an optional time from a date-and-time input.
     *
     * @param dateTimeParts date and optional time parts.
     * @return parsed time, or null when no time was supplied.
     */
    private LocalTime parseOptionalTime(String[] dateTimeParts) {
        if (dateTimeParts.length == 1) {
            return null;
        }

        return LocalTime.parse(dateTimeParts[1], DateTimeFormat.INPUT_TIME.getFormatter());
    }

    /**
     * Returns whether a command begins with the supplied command word.
     */
    private boolean hasCommandWord(String command, String commandWord) {
        return command.equals(commandWord)
                || (command.startsWith(commandWord)
                && command.length() > commandWord.length()
                && Character.isWhitespace(command.charAt(commandWord.length())));
    }

    /**
     * Returns the index of a standalone command directive, or -1 if it is absent.
     */
    private int findDirective(String command, String directive) {
        int index = command.indexOf(directive);
        while (index != -1) {
            int afterDirective = index + directive.length();
            boolean startsAfterWhitespace = index == 0 || Character.isWhitespace(command.charAt(index - 1));
            boolean endsBeforeWhitespace = afterDirective == command.length()
                    || Character.isWhitespace(command.charAt(afterDirective));
            if (startsAfterWhitespace && endsBeforeWhitespace) {
                return index;
            }
            index = command.indexOf(directive, afterDirective);
        }
        return -1;
    }

    /**
     * Returns the index of the optional notes directive after ensuring it occurs at most once.
     *
     * @param command command that may contain a notes directive.
     * @return index of the notes directive, or -1 if notes were not supplied.
     * @throws MondayException if the notes directive occurs more than once.
     */
    private int findNotesDirective(String command) throws MondayException {
        int notesIndex = findDirective(command, "/notes");
        if (notesIndex != -1
                && findDirective(command.substring(notesIndex + "/notes".length()), "/notes") != -1) {
            throw new MondayException("Please specify '/notes' only once.");
        }
        return notesIndex;
    }

    /**
     * Extracts and validates notes from a command.
     *
     * @param command command containing the notes text.
     * @param notesIndex index of the notes directive, or -1 if it is absent.
     * @return validated notes, or an empty string if notes were not supplied.
     * @throws MondayException if a notes directive has no text or uses unsupported characters.
     */
    private String parseNotes(String command, int notesIndex) throws MondayException {
        if (notesIndex == -1) {
            return "";
        }

        String notes = command.substring(notesIndex + "/notes".length()).trim();
        if (notes.isEmpty()) {
            throw new MondayException("Please tell me your notes.");
        }
        validateNotes(notes);
        return notes;
    }

    /**
     * Rejects description characters that cannot be represented by the save format.
     */
    private void validateDescription(String description) throws MondayException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new MondayException("Task descriptions cannot contain '|', newlines, or carriage returns.");
        }
    }

    /**
     * Rejects notes characters that cannot be represented by the save format.
     */
    private void validateNotes(String notes) throws MondayException {
        if (notes.contains("|") || notes.contains("\n") || notes.contains("\r")) {
            throw new MondayException("Task notes cannot contain '|', newlines, or carriage returns.");
        }
    }

    /**
     * Adds and persists a new task, undoing the addition if persistence fails.
     */
    private void addTask(Task task, TaskList tasks) throws MondayException {
        tasks.add(task);
        try {
            Storage.saveTask(tasks);
        } catch (MondayException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
    }

    /**
     * Restores a task's completion status after a failed save operation.
     */
    private void restoreCompletionStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Validates that an event's end is strictly after its start.
     */
    private void validateEventRange(LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime) throws MondayException {
        LocalTime effectiveStartTime = startTime == null ? LocalTime.MIN : startTime;
        LocalTime effectiveEndTime = endTime == null ? LocalTime.MIN : endTime;
        boolean endsBeforeStart = endDate.isBefore(startDate)
                || (endDate.equals(startDate) && !effectiveEndTime.isAfter(effectiveStartTime));
        if (endsBeforeStart) {
            throw new MondayException("The event end must be after its start.");
        }
    }

    /**
     * Displays the task most recently added to the task list.
     *
     * @param tasks task list containing the new task.
     * @param ui user interface used to display the result.
     */
    private void showAddedTask(TaskList tasks, Ui ui) {
        assert tasks.size() > 0 : "There are currently no tasks in the list. There must at least be one task.";

        ui.showResponse("Got it. I've added this task:",
                "  " + tasks.get(tasks.size() - 1),
                "Now you have " + tasks.size() + " tasks in the list.");
    }
}
