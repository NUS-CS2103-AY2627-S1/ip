package dude.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import dude.command.core.Command;
import dude.command.core.CommandCreator;
import dude.command.core.CommandQueue;
import dude.command.core.CommandResult;
import dude.command.duplicate.DuplicateResolutionHandler;
import dude.exception.CommandExecutionException;
import dude.exception.UsageException;
import dude.parser.Parser;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskService;
import dude.ui.Ui;

/**
 * Connects JavaFX controls to DUDE's command, task, and storage layers.
 */
public class GuiController {
    private final TaskService receiver;
    private final CommandQueue commandQueue;
    private final CommandCreator commandCreator;
    private String loadMessage = "";

    /**
     * Creates a controller using the default storage location.
     */
    public GuiController() {
        this(new Storage(), new DialogDuplicateResolutionHandler());
    }

    /**
     * Creates a controller with an injected duplicate-conflict interaction handler.
     *
     * @param resolutionHandler Handler used when adding a duplicate task.
     */
    public GuiController(DuplicateResolutionHandler resolutionHandler) {
        this(new Storage(), resolutionHandler);
    }

    /**
     * Creates a controller with explicit storage and duplicate interaction dependencies.
     *
     * @param storage           Storage used by commands.
     * @param resolutionHandler Handler used when adding a duplicate task.
     */
    public GuiController(Storage storage, DuplicateResolutionHandler resolutionHandler) {
        receiver = new TaskService(Objects.requireNonNull(storage, "storage"));
        commandQueue = new CommandQueue();
        commandCreator = new CommandCreator(receiver,
                Objects.requireNonNull(resolutionHandler, "resolutionHandler"), commandQueue);
    }

    /**
     * Loads tasks from persistent storage.
     */
    public void loadTasks() {
        try {
            receiver.loadTasks();
            loadMessage = "";
        } catch (IOException exception) {
            loadMessage = "I couldn't load your tasks, dude. Starting with an empty task list.";
        }
    }

    /**
     * Returns a startup storage message for the GUI response area.
     *
     * @return Empty text when loading succeeded, otherwise an error message.
     */
    public String getLoadMessage() {
        return loadMessage;
    }

    /**
     * Executes one existing DUDE command and captures its normal response.
     *
     * @param input Command entered in the GUI.
     * @return Text response, validation state, and whether the GUI should close.
     */
    public GuiResponse execute(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        String errorMessage = null;
        boolean shouldExit = false;
        try (PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            Ui outputUi = new Ui(new Scanner(""), output);
            Command command = commandCreator.create(Parser.parse(input), outputUi);
            CommandResult result = commandQueue.execute(command);
            shouldExit = result.shouldExit();
        } catch (UsageException exception) {
            errorMessage = formatGuiError(exception);
        } catch (CommandExecutionException exception) {
            errorMessage = "I couldn't save your tasks, dude. The change remains in memory.";
        }
        if (errorMessage != null) {
            return new GuiResponse(errorMessage, true, false);
        }
        return new GuiResponse(cleanForGui(buffer.toString(StandardCharsets.UTF_8)), false,
                shouldExit);
    }

    /**
     * Formats a usage error as a conversational GUI response.
     *
     * @param exception Invalid command details.
     * @return Friendly error message without CLI borders or usage formatting.
     */
    private static String formatGuiError(UsageException exception) {
        if ("command".equals(exception.getFieldName())) {
            return String.format("Hmm, I don't recognize \"%s\" as a command. "
                    + "Try one of: %s.", exception.getActualValue(), exception.getExpectedType());
        }
        String actual = "<missing>".equals(exception.getActualValue())
                ? "that part is missing" : String.format("\"%s\" isn't valid",
                exception.getActualValue());
        return String.format("Hmm, the %s for %s %s. I need %s.%nTry: %s",
                exception.getFieldName(), exception.getAction(), actual,
                exception.getExpectedType(), removeUsagePrefix(exception.getUsageMessage()));
    }

    /**
     * Removes the CLI-only usage label before a usage string is shown in the GUI.
     *
     * @param usageMessage Usage text from the command validation layer.
     * @return Usage text without a leading {@code Usage: } label.
     */
    private static String removeUsagePrefix(String usageMessage) {
        String prefix = "Usage: ";
        return usageMessage.startsWith(prefix)
                ? usageMessage.substring(prefix.length()) : usageMessage;
    }

    /**
     * Removes terminal-only colour codes, borders, and padding before output enters the GUI.
     *
     * @param output Captured console output.
     * @return Text suitable for JavaFX conversation bubbles.
     */
    private static String cleanForGui(String output) {
        return output.replaceAll("\\u001B\\[[;\\d]*m", "").lines()
                .map(String::stripTrailing)
                .filter(line -> !line.isBlank() && !isDivider(line))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Returns whether a line contains only the console divider character.
     *
     * @param line Candidate divider line.
     * @return True when the line is a divider.
     */
    private static boolean isDivider(String line) {
        return line.codePoints().allMatch(character -> character == '─');
    }

    /**
     * Returns the current tasks in a format suitable for the GUI.
     *
     * @return Numbered task list, or a message when there are no tasks.
     */
    public String renderTasks() {
        if (receiver.tasks().size() == 0) {
            return "No tasks yet.";
        }
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < receiver.tasks().size(); index++) {
            Task task = receiver.tasks().get(index);
            result.append(index + 1).append('.').append(task).append(System.lineSeparator());
        }
        return result.toString();
    }

    /**
     * Returns a snapshot of tasks for structured JavaFX list cells.
     *
     * @return Current tasks in display order.
     */
    public List<Task> getTasks() {
        return receiver.tasks();
    }
}
