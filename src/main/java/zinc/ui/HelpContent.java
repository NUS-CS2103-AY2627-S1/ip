package zinc.ui;

import java.util.List;
import java.util.stream.Collectors;

/** Provides the shared command-reference content used by Zinc's interfaces. */
public final class HelpContent {
    /** Complete command-reference topics in display order. */
    private static final List<HelpTopic> TOPICS = List.of(
            new HelpTopic(HelpCategory.TASKS, "Todo", "todo",
                    "Adds a task without a date or time.",
                    List.of("todo <description>"), List.of()),
            new HelpTopic(HelpCategory.TASKS, "Deadline", "deadline",
                    "Adds a task that must be completed by a date and optional time.",
                    List.of("deadline <description> /by <DD/MM/YY> [HHMM or HH:MM]"), List.of()),
            new HelpTopic(HelpCategory.TASKS, "Event", "event",
                    "Adds an event with a start and end date and optional times.",
                    List.of("event <description> /from <DD/MM/YY> [HHMM or HH:MM]"
                            + " /to <DD/MM/YY> [HHMM or HH:MM]"),
                    List.of()),
            new HelpTopic(HelpCategory.TASKS, "List tasks", "list",
                    "Lists every task, or tasks ending on a selected date.",
                    List.of("list", "list <DD/MM/YY>"), List.of("ls")),
            new HelpTopic(HelpCategory.TASKS, "Find tasks", "find",
                    "Finds tasks whose descriptions contain a keyword.",
                    List.of("find <keyword>"), List.of()),
            new HelpTopic(HelpCategory.TASKS, "Mark task", "mark",
                    "Marks a task as completed.",
                    List.of("mark <task number>"), List.of()),
            new HelpTopic(HelpCategory.TASKS, "Unmark task", "unmark",
                    "Marks a task as incomplete.",
                    List.of("unmark <task number>"), List.of()),
            new HelpTopic(HelpCategory.TASKS, "Delete task", "delete",
                    "Deletes a task from the task list.",
                    List.of("delete <task number>"), List.of()),
            new HelpTopic(HelpCategory.CONTACTS, "Add contact", "contact add",
                    "Adds a contact. The phone number and description are optional.",
                    List.of("contact add /n <name> [/p <8-digit contact number>] [/d <description>]"),
                    List.of("ct add")),
            new HelpTopic(HelpCategory.CONTACTS, "Delete contact", "contact delete",
                    "Deletes a contact using its name.",
                    List.of("contact delete /n <name>"),
                    List.of("contact del", "ct delete", "ct del")),
            new HelpTopic(HelpCategory.CONTACTS, "Update contact", "contact update",
                    "Updates one or more fields of an existing contact.",
                    List.of("contact update <current name> [/n <new name>]"
                            + " [/p <8-digit contact number>] [/d <description>]"),
                    List.of("ct update")),
            new HelpTopic(HelpCategory.CONTACTS, "List contacts", "contact list",
                    "Lists every contact, or contacts whose names contain a supplied keyword.",
                    List.of("contact list [keyword]"),
                    List.of("contact ls", "ct list", "ct ls")),
            new HelpTopic(HelpCategory.GENERAL, "Help", "help",
                    "Shows the commands available in Zinc.",
                    List.of("help"), List.of()),
            new HelpTopic(HelpCategory.GENERAL, "Set background", "ui background",
                    "Changes the window background or restores automatic time-based selection.",
                    List.of("ui background <morning|evening|night|auto>"), List.of("ui bg")),
            new HelpTopic(HelpCategory.GENERAL, "Exit", "bye",
                    "Exits Zinc.",
                    List.of("bye"), List.of()));

    /** Prevents construction of a utility class. */
    private HelpContent() {
    }

    /**
     * Returns all help topics in display order.
     *
     * @return An immutable list of help topics.
     */
    public static List<HelpTopic> getTopics() {
        return TOPICS;
    }

    /**
     * Creates the compact command overview used by the terminal interface.
     *
     * @return Commands grouped by category without detailed usages.
     */
    public static String getCompactOverview() {
        StringBuilder overview = new StringBuilder("Available commands:");
        for (HelpCategory category : HelpCategory.values()) {
            String commands = TOPICS.stream()
                    .filter(topic -> topic.getCategory() == category)
                    .map(HelpTopic::getCommand)
                    .collect(Collectors.joining(", "));
            overview.append("\n")
                    .append(category.getDisplayName())
                    .append(": ")
                    .append(commands);
        }
        return overview.toString();
    }
}
