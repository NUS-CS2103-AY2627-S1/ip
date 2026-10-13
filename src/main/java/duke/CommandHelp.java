package duke;

import java.util.List;

/**
 * Provides the command reference shown by {@code help} and used in AI answers.
 *
 * <p>Keeping the reference here lets both interfaces offer help without creating
 * an AI client. Update this text whenever a command's behavior changes.</p>
 */
public final class CommandHelp {
    /**
     * One command's syntax, short explanation, and ready-to-type example.
     *
     * @param format The accepted command format.
     * @param description A short explanation of the command.
     * @param example A valid example command.
     */
    public record Entry(String format, String description, String example) {
    }

    /**
     * A named, immutable group of related commands.
     *
     * @param title The group heading.
     * @param entries The commands in display order.
     */
    public record Section(String title, List<Entry> entries) {
        /** Copies the entries so callers cannot change the shared reference. */
        public Section {
            entries = List.copyOf(entries);
        }
    }

    private static final List<Section> SECTIONS = List.of(
            new Section("Add tasks", List.of(
                    new Entry("todo DESCRIPTION", "Add a task", "todo read a book"),
                    new Entry("deadline DESCRIPTION /by YYYY-MM-DD", "Add a due date",
                            "deadline submit report /by 2026-09-20"),
                    new Entry("event DESCRIPTION /from START /to END", "Add an event",
                            "event lunch /from 12pm /to 1pm"))),
            new Section("View and find", List.of(
                    new Entry("list", "Show your tasks and numbers", "list"),
                    new Entry("find KEYWORD", "Find matching descriptions", "find book"),
                    new Entry("sort", "Put the earliest deadlines first", "sort"))),
            new Section("Update tasks", List.of(
                    new Entry("mark NUMBER", "Mark a task done", "mark 1"),
                    new Entry("unmark NUMBER", "Mark a task not done", "unmark 1"),
                    new Entry("delete NUMBER", "Delete a task (no undo)", "delete 1"))),
            new Section("Help and exit", List.of(
                    new Entry("help", "Show this offline guide", "help"),
                    new Entry("@ai QUESTION", "Ask optional AI for command help", "@ai How do I add a task?"),
                    new Entry("bye", "Close Wangsa", "bye"))));

    private static final String NOTES = """
            • Use lowercase commands. Dates use YYYY-MM-DD.
            • Use task numbers from list. Check again after sorting or deleting.
            • Tasks save automatically. You can keep up to 100 tasks.
            • AI is optional. All task commands work offline.
            """.strip();

    private CommandHelp() {
    }

    /**
     * Returns the built-in command reference without needing a key or internet connection.
     *
     * @return The command reference as plain text.
     */
    public static String getText() {
        StringBuilder reference = new StringBuilder("Wangsa commands:\n");
        for (Section section : SECTIONS) {
            reference.append('\n').append(section.title()).append('\n');
            for (Entry entry : section.entries()) {
                reference.append("• ").append(entry.format()).append(" — ")
                        .append(entry.description()).append('\n');
            }
        }
        return reference.append('\n').append(NOTES).append("\n\n")
                .append("Find ignores case and keeps full-list numbers. Sort puts undated tasks last.\n")
                .append("Event start/end values are free-form text, without date-order checking.\n")
                .append("AI replies do not execute commands.\n")
                .append("Priorities, reminders, recurring tasks, and editing descriptions are not supported.")
                .toString();
    }

    /**
     * Returns the same command groups used by the desktop, terminal, and AI reference.
     *
     * @return Immutable command groups in display order.
     */
    public static List<Section> getSections() {
        return SECTIONS;
    }

    /**
     * Returns the short usage reminders displayed below the desktop help.
     *
     * @return Bulleted usage reminders.
     */
    public static String getNotes() {
        return NOTES;
    }
}
