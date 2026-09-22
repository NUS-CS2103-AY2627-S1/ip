package stewie.ui.gui;

import java.util.ArrayList;
import java.util.List;

import stewie.model.TaskList;
import stewie.parser.Command;
import stewie.parser.Parser;
import stewie.storage.StorageException;
import stewie.ui.Dialogue;

/**
 * Applies chat commands and returns replies independently of JavaFX rendering.
 */
final class GuiCommandHandler {
    static final String COMMAND_HELP = """
            The instructions. A brief reading should spare us both a great deal of theatre.

            todo <description>
            Add a task without a date.

            deadline <description> /by <date>
            Add a task with a deadline.

            event <description> /from <date> /to <date>
            Add an event with start and end dates.

            list
            Show all tasks and their numbers.

            find <keyword> [more keywords]
            Find tasks matching keywords.

            mark <number>
            Mark a task as done.

            unmark <number>
            Mark a task as undone.

            delete <number>
            Remove a task.

            update <number> [description] [d/<date>] [from/<date>] [to/<date>]
            Change one or more fields; omitted fields stay unchanged.
            Use d/ or by/ for deadlines, and from/ or to/ for events.

            help
            Show this command reference in Chat.

            bye
            Show a farewell message in Chat.

            Replace <...> with your values; [...] means optional.
            Task numbers start at 1. Use list to check the current numbers.
            Dates include 2026-08-12, 12/08/2026, 12-08-2026, 12.08.2026,
            12 Aug 2026, 12 August 2026, Aug 12, 2026, or August 12, 2026.
            Example: deadline submit report /by 12/08/2026
            """;

    private final TaskList taskList;

    /**
     * Creates a handler for the task list shared by the workspace.
     */
    GuiCommandHandler(TaskList taskList) {
        this.taskList = taskList;
    }

    /**
     * Handles normalized input, converting recoverable failures into assistant replies.
     */
    GuiCommandResult handle(String input) {
        if ("help".equals(input)) {
            return GuiCommandResult.message(COMMAND_HELP);
        }
        try {
            return executeCommand(Parser.getCommand(input), input);
        } catch (StorageException exception) {
            return GuiCommandResult.message(exception.getMessage());
        } catch (IllegalArgumentException exception) {
            return GuiCommandResult.message("A slight flaw in your plan: " + exception.getMessage());
        }
    }

    /**
     * Dispatches a command without constructing controls or changing the conversation.
     */
    private GuiCommandResult executeCommand(Command command, String input) {
        return switch (command) {
            case BYE -> GuiCommandResult.message(Dialogue.GOODBYE);
            case LIST -> taskListResult(Dialogue.LIST);
            case MARK -> updateTaskStatus(input, true);
            case UNMARK -> updateTaskStatus(input, false);
            case DEADLINE -> addDeadline(input);
            case EVENT -> addEvent(input);
            case TODO -> addTodo(input);
            case DELETE -> deleteTask(input);
            case FIND -> findTasks(input);
            case UPDATE -> updateTask(input);
            default -> GuiCommandResult.message(
                    "What precisely is the plan? Try `todo`, `event`, `deadline`, `list`, `find`, "
                            + "`mark`, `unmark`, `delete`, or `update`.");
        };
    }

    /**
     * Adds a todo and confirms only after persistence succeeds.
     */
    private GuiCommandResult addTodo(String input) {
        taskList.addToDo(Parser.parseTodo(input));
        return GuiCommandResult.message(Dialogue.ADDED);
    }

    /**
     * Adds a deadline using the existing parser and date validation.
     */
    private GuiCommandResult addDeadline(String input) {
        String[] parsedInput = Parser.parseDeadline(input);
        taskList.addDeadline(parsedInput[0], parsedInput[1]);
        return GuiCommandResult.message("Deadline recorded. Time is now officially judging you.");
    }

    /**
     * Adds an event using the existing parser and date validation.
     */
    private GuiCommandResult addEvent(String input) {
        String[] parsedInput = Parser.parseEvent(input);
        taskList.addEvent(parsedInput[0], parsedInput[1], parsedInput[2]);
        return GuiCommandResult.message("Event scheduled. I trust the occasion warrants all this organisation.");
    }

    /**
     * Updates a task status and includes a fresh interactive list in the reply.
     */
    private GuiCommandResult updateTaskStatus(String input, boolean isDone) {
        int index = Parser.getTaskIndex(input);
        if (!isValidTaskIndex(index)) {
            return GuiCommandResult.message(
                    "That task exists only in your imagination. Use a listed number with `mark` or `unmark`.");
        }
        String message;
        if (isDone) {
            taskList.markAsDone(index);
            message = "Completed. Rather well done, actually. Let us not make a scene.";
        } else {
            taskList.markAsUndone(index);
            message = "Reopened. A strategic reconsideration, shall we call it?";
        }
        return taskListResult(message, "The revised agenda, for your inspection:");
    }

    /**
     * Deletes a numbered task and returns the surviving commitments.
     */
    private GuiCommandResult deleteTask(String input) {
        int index = Parser.getTaskIndex(input);
        if (!isValidTaskIndex(index)) {
            return GuiCommandResult.message(
                    "I cannot delete an imaginary task. Use a listed number, such as `delete 1`.");
        }
        taskList.deleteTask(index);
        return taskListResult("Deleted. I have dismissed it from our affairs.", "The surviving commitments:");
    }

    /**
     * Updates supplied fields while retaining the existing validation guidance.
     */
    private GuiCommandResult updateTask(String input) {
        int index = Parser.getUpdateTaskIndex(input);
        String[] updates = Parser.parseUpdate(input);
        if (!isValidTaskIndex(index) || areAllUpdateFieldsMissing(updates)) {
            return GuiCommandResult.message(
                    "Details, please: `update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]`.");
        }
        taskList.updateTask(index, updates[0], updates[1], updates[2], updates[3]);
        return taskListResult(Dialogue.UPDATED, "The revised agenda, for your inspection:");
    }

    /**
     * Returns search cards without controls because their numbers are local to the results.
     */
    private GuiCommandResult findTasks(String input) {
        String[] matches = taskList.findTasks(Parser.parseFindKeywords(input));
        if (matches.length == 0) {
            return GuiCommandResult.message(
                    "Nothing matches. Even my brilliance needs a clue. Try another keyword or `list`.");
        }
        return new GuiCommandResult(List.of("Aha. The evidence you requested:"), List.of(matches), false);
    }

    /**
     * Returns a complete list, appending the empty-state message when needed.
     */
    private GuiCommandResult taskListResult(String... messages) {
        List<String> tasks = List.of(taskList.produceTaskList());
        if (tasks.isEmpty()) {
            ArrayList<String> replies = new ArrayList<>(List.of(messages));
            replies.add(Dialogue.EMPTY);
            return new GuiCommandResult(replies, tasks, true);
        }
        return new GuiCommandResult(List.of(messages), tasks, true);
    }

    /**
     * Checks whether all optional update fields are missing.
     *
     * @param updates Parsed update fields.
     * @return Whether no update field was supplied.
     */
    private boolean areAllUpdateFieldsMissing(String[] updates) {
        for (String update : updates) {
            if (update != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether a zero-based task index points to an existing task.
     *
     * @param index The zero-based task index.
     * @return True when the index is valid.
     */
    private boolean isValidTaskIndex(int index) {
        return index >= 0 && index < taskList.produceTaskList().length;
    }
}
