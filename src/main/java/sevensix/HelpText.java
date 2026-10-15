package sevensix;

/** Provides the shared command reference for the console and graphical interfaces. */
public final class HelpText {
    /** Prevents instantiation of the help reference. */
    private HelpText() {
    }

    /** Returns command syntax, examples, and input rules as a single response.
     *
     * @return the command reference using platform line separators.
     */
    public static String getMessage() {
        return String.join(System.lineSeparator(),
                "Here is your command playbook:",
                "  help - Show this guide.",
                "  todo <description> - Add a task without a date.",
                "  deadline <description> /by <date/time> - Add a task with a due date.",
                "  event <description> /from <date/time> /to <date/time> - Add an event.",
                "  list - Show all tasks and their numbers.",
                "  mark <number> - Mark a task as done.",
                "  unmark <number> - Mark a task as not done.",
                "  delete <number> - Remove a task.",
                "  find <keyword> - Search descriptions, ignoring letter case.",
                "  undo - Undo the latest task change; repeat to undo earlier changes.",
                "  bye - Exit SevenSix.",
                "Use task numbers from list, starting at 1.",
                "Dates: yyyy-MM-dd or d/M/yyyy; optionally add HHmm or HH:mm for a time.",
                "Events must end after they start; omitted times mean midnight.",
                "Examples:",
                "  todo read book",
                "  deadline submit report /by 2026-09-30 1800",
                "  event team meeting /from 2026-09-18 1400 /to 2026-09-18 1500",
                "  mark 1",
                "Help does not change tasks or undo history.");
    }
}
