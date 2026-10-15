package dude.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dude.command.core.CommandType;
import dude.command.duplicate.DuplicateTaskConflict;
import dude.exception.UsageException;
import dude.task.Task;
import dude.task.TaskMatch;

/**
 * Formats and prints DUDE's console responses.
 */
final class ConsoleRenderer {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private final PrintStream output;
    private final String border;
    private boolean isAtDivider;

    /**
     * Creates a renderer for the supplied output stream.
     *
     * @param output Destination for user-facing output.
     */
    ConsoleRenderer(PrintStream output) {
        this.output = Objects.requireNonNull(output, "output");
        border = "─".repeat(TerminalWidth.detect());
    }

    void showWelcome() {
        String banner = "██████╗  ██╗   ██╗ ██████╗  ███████╗\n"
                + "██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝\n"
                + "██║  ██║ ██║   ██║ ██║  ██║ █████╗\n"
                + "██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝\n"
                + "██████╔╝ ╚██████╔╝╚██████╔╝ "
                + "███████╗\n"
                + "╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝";
        printBox(banner, "Hey! I'm DUDE, your dependable task buddy.",
                "Dates can be represented in this format: yyyy-MM-dd.",
                "To include a time, use this format: yyyy-MM-dd HHmm.",
                "What can I help you with, dude?");
    }

    void showLine() {
        if (!isAtDivider) {
            output.println(border);
            isAtDivider = true;
        }
    }

    void showGoodbye() {
        printBox("Catch you later, dude!");
    }

    void showTaskList(List<Task> tasks) {
        String[] taskLines = Stream.concat(
                        Stream.of("Here's your task list, dude:"),
                        IntStream.range(0, tasks.size())
                                .mapToObj(i -> String.format("%d.%s", i + 1, tasks.get(i))))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    void showMatchingTasks(List<TaskMatch> matchingTasks) {
        String[] taskLines = Stream.concat(
                        Stream.of("Here are the tasks I found, dude:"),
                        matchingTasks.stream()
                                .map(match -> String.format("%d.%s", match.index() + 1,
                                        match.task())))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    void showTasksOnDate(List<Task> tasks, LocalDate date) {
        String[] taskLines = Stream.concat(
                        Stream.of(String.format("Here's what you have on %s:", date)),
                        IntStream.range(0, tasks.size())
                                .filter(index -> tasks.get(index).occursOn(date))
                                .mapToObj(index -> String.format("%d.%s", index + 1, tasks.get(index))))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    void showAddedTask(Task task, int taskCount) {
        printBox("Nice, dude — I've added this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    void showUpdatedTask(Task task, CommandType commandType) {
        String message = commandType == CommandType.MARK
                ? "Solid work, dude — this task is done:"
                : "No worries, dude — this task is back in progress:";
        printBox(message, "  " + task);
    }

    void showEditedTask(Task task) {
        printBox("Updated, dude — I've replaced the matching task:", "  " + task);
    }

    void showDeletedTask(Task task, int taskCount) {
        printBox("All right, dude — I've removed this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    void showUndo(String description) {
        printBox("Done, dude — I rolled back:", "  " + description + ".");
    }

    void showUndoUnavailable() {
        printBox("Nothing to undo yet, dude.");
    }

    void showLoadingError() {
        printBox("I couldn't load your tasks, dude.",
                "Starting with an empty task list.");
    }

    void showSavingError() {
        printBox("I couldn't save your tasks, dude.",
                "The change remains in memory for this session.");
    }

    void showDuplicateConflict(DuplicateTaskConflict conflict) {
        String[] lines = Stream.concat(
                        Stream.of("I found a task with the same description, dude:"),
                        conflict.matches().stream().map(match -> String.format("%d.%s",
                                match.index() + 1, match.task())))
                .toArray(String[]::new);
        printBox(lines);
    }

    void showDuplicatePrompt() {
        printBox("Edit [E], add [A], or cancel [C]?");
    }

    void showDuplicateSelection() {
        printBox("Which matching task number should I edit?");
    }

    void showInvalidDuplicateChoice() {
        printBox("Please choose Edit, Add, or Cancel, dude.");
    }

    void showInvalidDuplicateSelection() {
        printBox("Please choose one of the matching task numbers, dude.");
    }

    void showError(UsageException exception) {
        String usage = exception.getUsageMessage().replace(
                exception.getUsageToken(), ANSI_RED + exception.getUsageToken() + ANSI_RESET);
        String actualValue = formatActualValue(exception.getActualValue());
        if ("command".equals(exception.getFieldName())) {
            printBox(String.format("Error: invalid command %s.", actualValue),
                    String.format("Expected: %s.", exception.getExpectedType()), usage);
            return;
        }
        printBox(String.format("Error: invalid %s %s for %s.", exception.getFieldName(),
                        actualValue, exception.getAction()),
                String.format("Expected: %s.", exception.getExpectedType()), usage);
    }

    private static String formatActualValue(String actualValue) {
        if (actualValue == null || actualValue.equals("<missing>")) {
            return "<missing>";
        }
        return String.format("\"%s\"", actualValue);
    }

    private static String padRight(String text, int length) {
        return text.length() >= length ? text : text + " ".repeat(length - text.length());
    }

    private void printBox(String... lines) {
        output.println(border);
        isAtDivider = true;
        for (String line : lines) {
            for (String part : line.split("\n")) {
                output.println(padRight(part, border.length()));
            }
        }
        isAtDivider = false;
        showLine();
    }
}
