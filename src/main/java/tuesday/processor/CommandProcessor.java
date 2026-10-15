package tuesday.processor;

import java.io.IOException;
import java.util.Locale;

import tuesday.processor.UserInputParser.IndexValidation;
import tuesday.processor.UserInputParser.TaskValidation;
import tuesday.processor.UserInputParser.ValidationError;
import tuesday.task.Task;
import tuesday.task.TaskList;
import tuesday.ui.Strings;

/**
 * Executes user commands, manages duplicate choices, and saves task changes.
 */
public final class CommandProcessor {
    private static final int MAX_TASKS = 100;

    /** Uses the same save file from which the UI loaded the task list. */
    private final String storageFilePath;
    /** Remembers a proposed duplicate until the user chooses which task to keep. */
    private PendingDuplicate pendingDuplicate;

    /**
     * Creates a command processor that uses Tuesday's default save file.
     */
    public CommandProcessor() {
        this(Storage.FILE_PATH);
    }

    /**
     * Creates a command processor that saves changes to the specified file.
     *
     * @param storageFilePath the save file used when commands change tasks.
     */
    public CommandProcessor(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    /**
     * Processes a command as part of this processor's conversation session.
     *
     * <p>A processor remembers a proposed duplicate task until the user chooses {@code new} or {@code old}.</p>
     *
     * @param input the command entered by the user.
     * @param tasks the task list.
     * @return the response to display to the user.
     */
    public String processCommand(String input, TaskList tasks) {
        return processResponse(input, tasks).toPlainText();
    }

    /**
     * Executes a command once and provides link metadata separately from its text.
     *
     * @param input the command entered by the user.
     * @param tasks the task list.
     * @return the response and whether to offer the user guide.
     */
    public CommandResponse processResponse(String input, TaskList tasks) {
        if (pendingDuplicate != null) {
            return new CommandResponse(processDuplicateResolution(input, tasks), false);
        }
        if (input == null || input.trim().isEmpty()) {
            return new CommandResponse(Strings.EMPTY_COMMAND, false);
        }

        Command command = UserInputParser.getCommand(input);

        String response = switch (command) {
            case BYE -> Strings.FAREWELL;
            case LIST -> processListCommand(tasks);
            case FIND -> processFindCommand(input, tasks);
            case DELETE -> processDeleteCommand(input, tasks);
            case MARK, UNMARK -> processTaskStatusCommand(input, tasks, command);
            case TODO, DEADLINE, EVENT -> processTaskCreationCommand(input, tasks, command);
            case NEW, OLD -> Strings.DUPLICATE_CHOICE_UNAVAILABLE;
            case UNKNOWN -> formatUnknownCommand(input);
        };
        return new CommandResponse(response, command == Command.UNKNOWN);
    }

    /**
     * Converts an expected validation failure into guidance without throwing an exception.
     */
    private static String formatValidationError(ValidationError error, Command command) {
        return switch (error.code()) {
            case MISSING_DESCRIPTION -> "please add description, sir!";
            case MISSING_DEADLINE, MISSING_EVENT_TIME, INVALID_DATE_TIME -> formatDateTimeError(error, command);
            case INVALID_COMMAND_SYNTAX -> formatCommandSyntax(command);
            case MARK_OUT_OF_RANGE -> "Sir, that mark number is out of range.";
            case DELETE_OUT_OF_RANGE -> "Sir, that delete number is out of range.";
            case UNKNOWN_COMMAND -> formatUnknownCommand(error.detail());
        };
    }

    /**
     * Identifies only the unrecognized command word, not its arguments.
     */
    private static String formatUnknownCommand(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        return "Sir, I don't recognize the command \"" + commandWord + "\".";
    }

    /**
     * Explains the affected time field and supplies a complete command example.
     */
    private static String formatDateTimeError(ValidationError error, Command command) {
        String explanation = error.code() == UserInputParser.ValidationCode.INVALID_DATE_TIME
                ? "Sir, the value after " + error.field() + " is not a valid date or time."
                : "Sir, please provide " + error.field() + " with a date or time value for each field.";
        String syntax = command == Command.DEADLINE ? Strings.DEADLINE_SYNTAX : Strings.EVENT_SYNTAX;
        String example = command == Command.DEADLINE ? Strings.DEADLINE_EXAMPLE : Strings.EVENT_EXAMPLE;
        return explanation + "\n" + syntax + "\n" + Strings.DATE_TIME_FORMAT_HELP + "\n" + example;
    }

    /**
     * Shows marker requirements or task-number syntax for a recognized command.
     */
    private static String formatCommandSyntax(Command command) {
        return switch (command) {
            case DEADLINE -> "Sir, include /by exactly once.\n"
                    + Strings.DEADLINE_SYNTAX + "\n" + Strings.DEADLINE_EXAMPLE;
            case EVENT -> "Sir, include /from followed by /to, each exactly once.\n"
                    + Strings.EVENT_SYNTAX + "\n" + Strings.EVENT_EXAMPLE;
            default -> "Sir, provide one whole task number with no extra arguments.\nFormat: "
                    + command.name().toLowerCase(Locale.ROOT) + " NUMBER\nExample: "
                    + command.name().toLowerCase(Locale.ROOT) + " 1";
        };
    }

    /**
     * Processes the user's answer to a pending duplicate prompt.
     */
    private String processDuplicateResolution(String input, TaskList tasks) {
        Command choice = UserInputParser.parseDuplicateChoice(input);
        if (choice == Command.BYE) {
            pendingDuplicate = null;
            return Strings.FAREWELL;
        }
        if (choice == Command.OLD) {
            return keepExistingTask(tasks);
        }
        if (choice == Command.NEW) {
            return keepProposedTask(tasks);
        }
        return Strings.DUPLICATE_CHOICE_INSTRUCTION;
    }

    /**
     * Discards the proposed task and reports the unchanged list size.
     */
    private String keepExistingTask(TaskList tasks) {
        Task existingTask = pendingDuplicate.existingTask();
        pendingDuplicate = null;
        return "Okay. I've kept this task:\n"
                + "  " + existingTask + "\n"
                + "You still have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Replaces the first matching task with the proposed task and saves the list.
     */
    private String keepProposedTask(TaskList tasks) {
        PendingDuplicate duplicate = pendingDuplicate;
        pendingDuplicate = null;

        tasks.replace(duplicate.existingTaskIndex(), duplicate.proposedTask());
        String response = formatTaskReplaced(duplicate.existingTask(), duplicate.proposedTask(), tasks.size());
        try {
            Storage.modifyData(storageFilePath, tasks);
        } catch (IOException e) {
            tasks.replace(duplicate.existingTaskIndex(), duplicate.existingTask());
            return "Failed to save data! Unable to create the file, Sir!\n"
                    + "The existing task was kept.";
        }
        return response;
    }

    /**
     * Formats a replacement confirmation with the unchanged number of tasks.
     */
    private static String formatTaskReplaced(Task existingTask, Task proposedTask, int taskCount) {
        return "Got it. I've replaced this task:\n"
                + "  " + existingTask + "\n"
                + "with:\n"
                + "  " + proposedTask + "\n"
                + "You still have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats all tasks for the list command.
     */
    private static String processListCommand(TaskList tasks) {
        StringBuilder taskOutput = new StringBuilder(Strings.SHOW_LIST);
        for (int i = 1; i <= tasks.size(); i++) {
            taskOutput.append("\n").append(i).append(".").append(tasks.get(i - 1));
        }
        return taskOutput.toString();
    }

    /**
     * Finds and formats tasks whose descriptions contain the requested keyword.
     */
    private static String processFindCommand(String input, TaskList tasks) {
        String keyword = UserInputParser.parseFindKeyword(input);
        if (keyword.isEmpty()) {
            return "Please provide a keyword to find, Sir!";
        }

        StringBuilder taskOutput = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).matchesDescription(keyword)) {
                taskOutput.append("\n").append(i + 1).append(".").append(tasks.get(i));
            }
        }
        return taskOutput.toString();
    }

    /**
     * Deletes the task selected by a valid delete command and saves the updated list.
     */
    private String processDeleteCommand(String input, TaskList tasks) {
        IndexValidation validation = UserInputParser.validateTaskIndex(input, tasks.size(), Command.DELETE);
        if (validation.error() != null) {
            return formatValidationError(validation.error(), Command.DELETE);
        }

        Task removedTask = tasks.remove(validation.index());
        String response = formatTaskDeleted(removedTask, tasks.size());
        try {
            Storage.modifyData(storageFilePath, tasks);
        } catch (IOException e) {
            tasks.add(validation.index(), removedTask);
            return Strings.SAVE_FAILURE;
        }
        return response;
    }

    /**
     * Formats a deletion confirmation with the number of remaining tasks.
     */
    private static String formatTaskDeleted(Task removedTask, int taskCount) {
        return "Noted. I've removed this task:\n"
                + "  " + removedTask + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Marks or unmarks the selected task and saves its new status.
     */
    private String processTaskStatusCommand(String input, TaskList tasks, Command command) {
        assert command == Command.MARK || command == Command.UNMARK
                : "A task status command should always be mark or unmark";

        IndexValidation validation = UserInputParser.validateTaskIndex(input, tasks.size(), command);
        if (validation.error() != null) {
            return formatValidationError(validation.error(), command);
        }

        Task task = tasks.get(validation.index());
        boolean wasDone = task.isDone();
        boolean isDone = command == Command.MARK;
        setTaskCompletion(task, isDone);
        String response = formatStatusChanged(task, isDone);

        if (!saveTaskStatus(task, wasDone, tasks)) {
            return Strings.SAVE_FAILURE;
        }
        return response;
    }

    /**
     * Applies a completion state for both requested changes and failed-save recovery.
     */
    private static void setTaskCompletion(Task task, boolean isDone) {
        if (isDone) {
            task.mark();
        } else {
            task.unmark();
        }
    }

    /**
     * Saves the changed status, restoring the previous state if saving fails.
     *
     * @return True if saving succeeds, or false after restoring the previous status.
     */
    private boolean saveTaskStatus(Task task, boolean wasDone, TaskList tasks) {
        try {
            Storage.modifyData(storageFilePath, tasks);
        } catch (IOException e) {
            setTaskCompletion(task, wasDone);
            return false;
        }
        return true;
    }

    /**
     * Formats the confirmation for marking or unmarking a task.
     */
    private static String formatStatusChanged(Task task, boolean isDone) {
        return (isDone ? Strings.MARK : Strings.UNMARK) + "\n  " + task;
    }

    /**
     * Creates a task from a valid task-creation command and saves it.
     */
    private String processTaskCreationCommand(String input, TaskList tasks, Command command) {
        TaskValidation validation = UserInputParser.parseTaskCreation(input, command);
        if (validation.error() != null) {
            return formatValidationError(validation.error(), command);
        }

        if (tasks.size() >= MAX_TASKS) {
            return "Sir, this will cost too much time";
        }

        Task task = validation.task();
        int duplicateIndex = tasks.indexOfDuplicate(task);
        if (duplicateIndex >= 0) {
            return promptForDuplicate(task, tasks.get(duplicateIndex), duplicateIndex);
        }

        return addTaskAndSave(task, tasks);
    }

    /**
     * Remembers a proposed duplicate and prompts using the existing task's one-based list number.
     */
    private String promptForDuplicate(Task proposedTask, Task existingTask, int existingTaskIndex) {
        pendingDuplicate = new PendingDuplicate(proposedTask, existingTask, existingTaskIndex);
        return "I found an existing task with the same description:\n"
                + "  " + (existingTaskIndex + 1) + "." + existingTask + "\n"
                + "Would you like to keep the new task or the old task?\n"
                + "Please reply with \"new\" or \"old\".";
    }

    /**
     * Saves a new task before adding it to memory, leaving the list unchanged if saving fails.
     */
    private String addTaskAndSave(Task task, TaskList tasks) {
        String response = formatTaskAdded(task, tasks.size() + 1);
        try {
            Storage.saveNewData(storageFilePath, task.toString());
        } catch (IOException e) {
            return Strings.SAVE_FAILURE;
        }
        tasks.add(task);
        return response;
    }

    /**
     * Formats an addition confirmation with the number of tasks after insertion.
     */
    private static String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Stores the proposed task and the first existing task with the same description.
     */
    private record PendingDuplicate(Task proposedTask, Task existingTask, int existingTaskIndex) {
    }

}
