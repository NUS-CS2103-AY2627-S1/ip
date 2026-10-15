package bern.logic;

import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

import bern.datetime.DateTime;
import bern.datetime.DateTimeFactory;
import bern.storage.SaveDataController;
import bern.task.TaskFactory;
import bern.task.TaskManager;
import bern.ui.Dialog;
import javafx.application.Platform;

/**
 * Processes user commands and coordinates task-management operations.
 */
public class Controller {
    private static final TaskManager TASK_MANAGER = TaskManager.getInstance();
    private static final Dialog DIALOG = Dialog.getInstance();

    /**
     * Stores the commands supported by the application and their handlers.
     */
    private enum Keyword {
        BYE(Controller::attemptExit),
        LIST(Controller::attemptListTasks),
        MARK(Controller::attemptMarkTask),
        UNMARK(Controller::attemptUnmarkTask),
        TODO(Controller::attemptMakeTodo),
        DEADLINE(Controller::attemptMakeDeadline),
        EVENT(Controller::attemptMakeEvent),
        DELETE(Controller::attemptDeleteTask),
        FIND(Controller::attemptFindTasks),
        SCHEDULE(Controller::attemptShowSchedule);

        /**
         * Handles the full tokenized command, including the command keyword at index zero.
         */
        private final Function<String[], Response> action;

        /**
         * Associates a command keyword with its handler.
         *
         * @param action The function that handles this command's input tokens.
         */
        Keyword(Function<String[], Response> action) {
            this.action = action;
        }
    }

    /**
     * Creates a command controller that uses the application's shared task manager and dialog.
     */
    public Controller() {
    }

    /**
     * Converts a task factory parsing failure into an error message.
     * An offset of -1 identifies missing arguments; other offsets identify a missing keyword.
     *
     * @param e The task factory exception whose message names the relevant keyword.
     * @return An error message reflecting the exception details.
     */
    private static String getParseExceptionResponse(ParseException e) {
        if (e.getErrorOffset() == -1) {
            // Missing argument
            return String.format("Argument(s) for %s keyword not specified\n", e.getMessage());
        }
        return String.format("Missing keyword: %s\n", e.getMessage());
    }

    /**
     * Saves the tasks and requests JavaFX shutdown when the exit command is valid.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The goodbye response, or a usage or save error without requesting shutdown.
     */
    private static Response attemptExit(String[] inputTokens) {
        if (inputTokens.length != 1) {
            return DIALOG.printIncorrectKeywordUsageError(Keyword.BYE, Keyword.BYE);
        }
        if (!SaveDataController.saveTaskData(TASK_MANAGER.getTaskList())) {
            return DIALOG.printSaveError();
        }
        Platform.exit();
        return DIALOG.sayGoodbye();
    }

    /**
     * Returns the task list when the command has no extra arguments.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The task list, or a message for invalid usage or an empty task list.
     */
    private static Response attemptListTasks(String[] inputTokens) {
        if (inputTokens.length != 1) {
            return DIALOG.printIncorrectKeywordUsageError(Keyword.LIST, Keyword.LIST);
        }

        return TASK_MANAGER.listTasks();
    }

    /**
     * Returns matching tasks, or a usage error if the command does not contain exactly one search token.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The matching tasks, or a message for invalid usage or no matching tasks.
     */
    private static Response attemptFindTasks(String[] inputTokens) {
        if (inputTokens.length != 2) {
            return DIALOG.printIncorrectKeywordUsageError(
                    Keyword.FIND, Keyword.FIND + " [search token]");
        }

        return TASK_MANAGER.findTasks(inputTokens[1]);
    }

    /**
     * Returns the requested day's schedule, defaulting to today when the command has no arguments.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The schedule, or a message for an invalid date or a day without tasks.
     */
    private static Response attemptShowSchedule(String[] inputTokens) {
        try {
            String dateArgument = String.join(" ", Arrays.copyOfRange(inputTokens, 1, inputTokens.length));
            DateTime scheduleDate = inputTokens.length == 1
                    ? DateTime.now()
                    : DateTimeFactory.parseDateTime(dateArgument);
            return TASK_MANAGER.showSchedule(scheduleDate);
        } catch (DateTimeParseException e) {
            return DIALOG.printInvalidDateTimeError(e.getParsedString());
        }
    }

    /**
     * Extracts and validates the one-based task number from a command containing exactly two tokens.
     *
     * @param inputTokens The nonempty command tokens, including the keyword at index zero.
     * @return The valid one-based task number.
     * @throws IllegalArgumentException If no tasks exist, the token count is not two, or the number is invalid.
     */
    private static int tryGetTaskNumber(String[] inputTokens) throws IllegalArgumentException {
        if (!TASK_MANAGER.hasTasks()) {
            throw new IllegalArgumentException(DIALOG.printNoTasksError().toString());
        }

        if (inputTokens.length != 2) {
            throw new IllegalArgumentException(
                    DIALOG.printIncorrectKeywordUsageError(inputTokens[0], inputTokens[0]).toString());
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(inputTokens[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(DIALOG.printInvalidTaskNumberError(TASK_MANAGER.size()).toString());
        }

        if (taskNumber < 1 || taskNumber > TASK_MANAGER.size()) {
            throw new IllegalArgumentException(DIALOG.printInvalidTaskNumberError(TASK_MANAGER.size()).toString());
        }

        return taskNumber;
    }

    /**
     * Marks the requested task as done, returning an error response for an invalid task number.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The task's completion status, or a response describing the invalid input.
     */
    private static Response attemptMarkTask(String[] inputTokens) {
        try {
            int taskNumber = tryGetTaskNumber(inputTokens);
            return TASK_MANAGER.markTask(taskNumber);
        } catch (IllegalArgumentException e) {
            return DIALOG.printMessage(e.getMessage());
        }
    }

    /**
     * Marks the requested task as not done, returning an error response for an invalid task number.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The task's completion status, or a response describing the invalid input.
     */
    private static Response attemptUnmarkTask(String[] inputTokens) {
        try {
            int taskNumber = tryGetTaskNumber(inputTokens);
            return TASK_MANAGER.unmarkTask(taskNumber);
        } catch (IllegalArgumentException e) {
            return DIALOG.printMessage(e.getMessage());
        }
    }

    /**
     * Creates and adds a todo, returning an error response when its description is missing.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The added task and its confirmation, or a response describing the invalid input.
     */
    private static Response attemptMakeTodo(String[] inputTokens) {
        try {
            return TASK_MANAGER.addTask(TaskFactory.makeTodo(inputTokens));
        } catch (ParseException e) {
            return DIALOG.printMessage(getParseExceptionResponse(e) + "Command syntax: todo <task name>");
        }
    }

    /**
     * Creates and adds a deadline, returning an error response when its arguments are invalid.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The added task and its confirmation, or a response describing the invalid input.
     */
    private static Response attemptMakeDeadline(String[] inputTokens) {
        try {
            return TASK_MANAGER.addTask(TaskFactory.makeDeadline(inputTokens));
        } catch (ParseException e) {
            return DIALOG.printMessage(getParseExceptionResponse(e)
                    + "Command syntax: deadline <task name> /by <due date>");
        } catch (DateTimeParseException e) {
            return DIALOG.printInvalidDateTimeError(e.getParsedString());
        }
    }

    /**
     * Creates and adds an event, returning an error response when its arguments or time range are invalid.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The added task and its confirmation, or a response describing the invalid input.
     */
    private static Response attemptMakeEvent(String[] inputTokens) {
        try {
            return TASK_MANAGER.addTask(TaskFactory.makeEvent(inputTokens));
        } catch (ParseException e) {
            return DIALOG.printMessage(getParseExceptionResponse(e)
                    + "Command syntax: event <task name> /from <start date time> /to <end date time>");
        } catch (DateTimeParseException e) {
            return DIALOG.printInvalidDateTimeError(e.getParsedString());
        } catch (IllegalArgumentException e) {
            return DIALOG.printMessage(e.getMessage());
        }
    }

    /**
     * Deletes the requested task, returning an error response for an invalid task number.
     *
     * @param inputTokens The command tokens, including the keyword at index zero.
     * @return The deleted task and its confirmation, or a response describing the invalid input.
     */
    private static Response attemptDeleteTask(String[] inputTokens) {
        try {
            int taskNumber = tryGetTaskNumber(inputTokens);
            return TASK_MANAGER.deleteTask(taskNumber);
        } catch (IllegalArgumentException e) {
            return DIALOG.printMessage(e.getMessage());
        }
    }

    /**
     * Handles a user command and returns the corresponding response.
     *
     * @param input The non-null command entered by the user.
     * @return The command result, or a message explaining invalid input.
     */
    public Response getResponse(String input) {
        if (input.isBlank()) {
            return DIALOG.printKeywordInvalidError();
        }

        String[] inputTokens = input.strip().split(" ");
        try {
            Keyword keyword = Keyword.valueOf(inputTokens[0].toUpperCase());
            return keyword.action.apply(inputTokens);
        } catch (IllegalArgumentException e) {
            return DIALOG.printKeywordInvalidError();
        }
    }

    /**
     * Loads saved tasks if the shared task manager is empty.
     *
     * @return Whether at least one task was loaded; false if the manager was already populated or no tasks were read.
     */
    public boolean loadTasks() {
        return TASK_MANAGER.loadTaskList(SaveDataController.readTaskData());
    }

    /**
     * Loads saved tasks and handles command-line input until a standalone bye command is received.
     */
    static void main() {
        DIALOG.greetUser();

        Scanner sc = new Scanner(System.in);
        boolean isReadingInput = true;
        Keyword keyword;

        TASK_MANAGER.loadTaskList(SaveDataController.readTaskData());

        if (TASK_MANAGER.hasTasks()) {
            DIALOG.printTasksLoaded();
        }

        while (isReadingInput) {
            String input = DIALOG.promptForInput(sc);
            String[] inputTokens = input.split(" ");

            try {
                keyword = Keyword.valueOf(inputTokens[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                DIALOG.printKeywordInvalidError();
                continue;
            }

            keyword.action.apply(inputTokens);

            // Manual check for Keyword.BYE
            if (keyword.equals(Keyword.BYE) && inputTokens.length == 1) {
                isReadingInput = false;
            }
        }

        sc.close();
        DIALOG.sayGoodbye();
    }
}
