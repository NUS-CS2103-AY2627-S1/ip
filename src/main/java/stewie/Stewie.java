package stewie;

import java.util.Locale;
import java.util.Scanner;

import stewie.model.TaskList;
import stewie.parser.Command;
import stewie.parser.Parser;
import stewie.storage.StorageException;
import stewie.ui.cli.Ui;

/**
 * Represents the Stewie console task assistant.
 * Parses user commands and maintains a task list backed by local storage.
 */
public class Stewie {
    private final TaskList taskList;

    /**
     * Creates a Stewie chatbot with tasks loaded from local storage.
     */
    public Stewie() {
        this.taskList = new TaskList();
    }

    /**
     * Runs the console chatbot until the user enters bye or input ends.
     * Prints the banner and greeting, then processes commands and reports recoverable errors.
     */
    public void run() {
        showStartupMessages();
        readCommands(new Scanner(System.in));
    }

    /**
     * Displays the greeting and any warning from loading saved tasks.
     */
    private void showStartupMessages() {
        Ui.greetUser();
        Ui.printLoadWarning(taskList.getLoadWarning());
    }

    /**
     * Processes input until the user says goodbye or the input stream ends.
     */
    private void readCommands(Scanner scanner) {
        while (scanner.hasNextLine()) {
            if (!processInput(scanner.nextLine())) {
                return;
            }
        }
    }

    /**
     * Handles one input and reports recoverable errors; returns false after goodbye.
     */
    private boolean processInput(String rawInput) {
        String input = Parser.normalize(rawInput);
        Command command = Parser.getCommand(input);
        assert command != null : "Parser must classify every input command";
        try {
            executeCommand(command, input);
        } catch (StorageException exception) {
            Ui.printStorageError(exception.getMessage());
        } catch (IndexOutOfBoundsException exception) {
            Ui.printNumberedCommandFormat(command.name().toLowerCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            Ui.printInputError(exception.getMessage());
        }
        return command != Command.BYE;
    }

    /**
     * Dispatches a classified command to its corresponding operation.
     */
    private void executeCommand(Command command, String input) {
        switch (command) {
            case BYE -> Ui.printBye();
            case LIST -> Ui.printTaskList(taskList.produceTaskList());
            case MARK -> markAsDone(input);
            case UNMARK -> markAsUndone(input);
            case DEADLINE -> addDeadline(input);
            case EVENT -> addEvent(input);
            case TODO -> addToDo(input);
            case DELETE -> deleteTask(input);
            case FIND -> findTasks(input);
            case UPDATE -> updateTask(input);
            default -> Ui.printUnknownCommand();
        }
    }

    /**
     * Adds a task of type deadline to the task list.
     * Deadline task includes description and deadline date.
     *
     * @param input Input from user.
     */
    private void addDeadline(String input) {
        String[] parsedInput = Parser.parseDeadline(input);
        this.taskList.addDeadline(parsedInput[0], parsedInput[1]);
        printTaskAddConfirmation();
    }

    /**
     * Adds a task of type event to the task list.
     * An event has a description, start date, and end date.
     *
     * @param input Input from user.
     */
    private void addEvent(String input) {
        String[] parsedInput = Parser.parseEvent(input);
        this.taskList.addEvent(parsedInput[0], parsedInput[1], parsedInput[2]);
        printTaskAddConfirmation();
    }

    /**
     * Adds a task of type ToDo to the task list.
     * Records description of the task.
     *
     * @param input Input from user.
     */
    private void addToDo(String input) {
        this.taskList.addToDo(Parser.parseTodo(input));
        printTaskAddConfirmation();
    }

    /**
     * Confirms the last added task only after the model has successfully saved it.
     */
    private void printTaskAddConfirmation() {
        String[] tasks = taskList.produceTaskList();
        Ui.printTaskAddConfirmation(tasks[tasks.length - 1], tasks.length);
    }

    /**
     * Marks the task as done.
     *
     * @param input Input from user.
     */
    private void markAsDone(String input) {
        int index = Parser.getTaskIndex(input);
        if (index == -1) {
            Ui.printMissingTaskNumber("mark");
            return;
        } else {
            this.taskList.markAsDone(index);
        }
    }

    /**
     * Marks the task as not done.
     *
     * @param input Input from user.
     */
    private void markAsUndone(String input) {
        int idx = Parser.getTaskIndex(input);
        if (idx == -1) {
            Ui.printMissingTaskNumber("unmark");
        } else {
            this.taskList.markAsUndone(idx);
        }
    }

    /**
     * Deletes the task from the task list.
     *
     * @param input Input from user.
     */
    private void deleteTask(String input) {
        int idx = Parser.getTaskIndex(input);
        if (idx == -1) {
            Ui.printMissingTaskNumber("delete");
        } else {
            this.taskList.deleteTask(idx);
        }
    }

    /**
     * Updates the supplied task description and date fields.
     *
     * @param input Input from user.
     */
    private void updateTask(String input) {
        int index = Parser.getUpdateTaskIndex(input);
        String[] updates = Parser.parseUpdate(input);
        if (index == -1 || areAllUpdateFieldsMissing(updates)) {
            Ui.printUpdateFormat();
            return;
        }
        this.taskList.updateTask(index, updates[0], updates[1], updates[2], updates[3]);
        Ui.printTaskUpdateConfirmation(taskList.produceTaskList()[index]);
    }

    /**
     * Checks whether all optional update fields are missing.
     *
     * @param updates Parsed update fields.
     * @return True when no update field was supplied.
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
     * Prints tasks that contain at least one search keyword from the input.
     *
     * @param input Input from user.
     */
    private void findTasks(String input) {
        String[] keywords = Parser.parseFindKeywords(input);
        Ui.printTaskList(this.taskList.findTasks(keywords));
    }

    /**
     * Starts the console task assistant.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Stewie().run();
    }
}
