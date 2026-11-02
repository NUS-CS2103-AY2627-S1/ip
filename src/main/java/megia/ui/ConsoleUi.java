package megia.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import megia.exception.MegiaException;
import megia.model.CommandResult;
import megia.model.TaskEntry;
import megia.service.LocalizationService;

/**
 * Handles all console input and output for the Megia application.
 */
public final class ConsoleUi implements AutoCloseable {
    private static final String BANNER = """
            /\\ "-./  \\   /\\  ___\\   /\\  ___\\   /\\ \\   /\\  __ \\
            \\ \\ \\-./\\ \\  \\ \\  __\\   \\ \\ \\__‾\\  \\ \\ \\  \\ \\  __ \\
            \\ \\_\\ \\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_\\  \\ \\_\\ \\_\\
            \\/_/  \\/_/   \\/_____/   \\/_____/   \\/_/   \\/_/\\/_/
            """.stripTrailing() + "\n\n";
    private static final String MESSAGE_SEPARATOR = "\n\033[37m"
            + "─".repeat(80)
            + "\033[0m";

    private final Scanner userInput;
    private final PrintStream standardOutput;
    private final PrintStream errorOutput;

    /**
     * Creates a console UI connected to the system input and output streams.
     */
    public ConsoleUi() {
        this(System.in, System.out, System.err);
    }

    /**
     * Creates a console UI connected to the supplied streams.
     *
     * @param inputStream Stream from which commands are read.
     * @param standardOutput Stream to which normal messages are written.
     * @param errorOutput Stream to which diagnostic messages are written.
     */
    public ConsoleUi(
            InputStream inputStream,
            PrintStream standardOutput,
            PrintStream errorOutput) {
        this.userInput = new Scanner(inputStream);
        this.standardOutput = new PrintStream(standardOutput, true, StandardCharsets.UTF_8);
        this.errorOutput = errorOutput;
    }

    /**
     * Reads the next command after displaying the command prompt.
     *
     * @return Entered command, or an empty optional when the input stream has ended.
     */
    public Optional<String> readCommand() {
        standardOutput.print("> ");
        if (!userInput.hasNextLine()) {
            return Optional.empty();
        }
        return Optional.of(userInput.nextLine());
    }

    /**
     * Displays the application banner and greeting.
     */
    public void showGreeting() {
        standardOutput.println(MESSAGE_SEPARATOR);
        showMessage(BANNER + LocalizationService.getMessage("greeting"));
    }

    /**
     * Displays a message followed by a console separator.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        standardOutput.println(message);
        standardOutput.println(MESSAGE_SEPARATOR);
    }

    /**
     * Displays a structured command result using the existing console wording.
     *
     * @param result Result produced by the shared command pipeline.
     */
    public void showResult(CommandResult result) {
        switch (result) {
            case CommandResult.TaskList taskList -> showTaskList(taskList);
            case CommandResult.TaskMutation mutation -> showTaskMutation(mutation);
            case CommandResult.Empty ignored -> showMessage(LocalizationService.getMessage("empty"));
            case CommandResult.Help ignored -> showMessage(LocalizationService.getMessage("help"));
            case CommandResult.Exit ignored -> {
                // The application loop handles exit results without output.
            }
        }
    }

    /**
     * Displays a localized message for a recoverable application error.
     *
     * @param exception Error to display.
     */
    public void showError(MegiaException exception) {
        showMessage(LocalizationService.getException(
                exception.getErrorCode(), exception.getMessageArguments()));
    }

    /**
     * Reports an unexpected error and displays a safe message to the user.
     *
     * @param exception Unexpected error to report.
     */
    public void showUnexpectedError(RuntimeException exception) {
        errorOutput.println("Unexpected application error: " + exception.getMessage());
        showMessage(LocalizationService.getMessage("unexpected_error"));
    }

    /**
     * Displays the application farewell.
     */
    public void showFarewell() {
        showMessage(LocalizationService.getMessage("farewell"));
    }

    private void showTaskList(CommandResult.TaskList taskList) {
        String message;
        if (taskList.entries().isEmpty()) {
            message = switch (taskList.query().type()) {
                case ALL -> LocalizationService.getMessage("task_storage_empty");
                case DATE -> String.format(
                        LocalizationService.getMessage("task_storage_date_empty"),
                        taskList.query().value());
                case FIND -> String.format(
                        LocalizationService.getMessage("task_storage_find_empty"),
                        taskList.query().value());
            };
        } else {
            String heading = switch (taskList.query().type()) {
                case ALL -> LocalizationService.getMessage("task_storage_list");
                case DATE -> String.format(
                        LocalizationService.getMessage("task_storage_date_list"),
                        taskList.query().value());
                case FIND -> LocalizationService.getMessage("task_storage_find_list");
            };
            message = heading + "\n" + formatTasks(taskList.entries());
        }
        showMessage(message);
    }

    private void showTaskMutation(CommandResult.TaskMutation mutation) {
        if (mutation.operation() == CommandResult.MutationType.ADD) {
            showMessage(
                    LocalizationService.getMessage("task_storage_add")
                            + "\n  " + mutation.task().task()
                            + "\n"
                            + String.format(
                                    LocalizationService.getMessage("task_storage_add_2"),
                                    mutation.taskCount()));
            return;
        }
        String messageKey = "task_storage_" + mutation.operation().name().toLowerCase();
        showMessage(LocalizationService.getMessage(messageKey) + "\n" + mutation.task().task());
    }

    private static String formatTasks(Iterable<TaskEntry> entries) {
        String taskText = java.util.stream.StreamSupport.stream(entries.spliterator(), false)
                .map(entry -> entry.id() + "." + entry.task() + "\n")
                .collect(Collectors.joining());
        return taskText.strip();
    }

    @Override
    public void close() {
        userInput.close();
    }
}
