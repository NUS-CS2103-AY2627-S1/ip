package eve.command;

import eve.EveException;

/**
 * The chatbot's recognized command words, each with its usage syntax and a
 * short description shown by the {@code help} command. This is purely a
 * lookup table of known words and their display text; {@link Parser} uses
 * it to validate a typed word and decide which {@link Command} to build.
 */
public enum CommandWord {
    HELP("help", "Show this list of commands again!"),
    TODO("todo <description>", "Add a to-do task!"),
    DEADLINE("deadline <description> /by <yyyy-mm-dd>", "Add a task with a deadline!"),
    EVENT("event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>", "Add an event!"),
    ON("on <yyyy-mm-dd>", "Show tasks happening on a date!"),
    FIND("find <keyword>", "Find tasks matching a keyword!"),
    SCHEDULE("schedule [yyyy-mm-dd]", "Show your whole schedule, or just one date's!"),
    LIST("list", "Show off your whole task list!"),
    MARK("mark <task number>", "Mark a task as done!"),
    UNMARK("unmark <task number>", "Mark a task as not done yet!"),
    DELETE("delete <task number>", "Clear a task off your list!"),
    BYE("bye", "Wrap up for now!");

    private final String usage;
    private final String description;

    CommandWord(String usage, String description) {
        this.usage = usage;
        this.description = description;
    }

    /** Returns this command's usage syntax, e.g. {@code "mark <task number>"}. */
    public String getUsage() {
        return usage;
    }

    /** Returns a short, human-readable description of what this command does. */
    public String getDescription() {
        return description;
    }

    /**
     * Looks up the command word matching the given text (case-insensitive).
     *
     * @param word the first word of a line of user input.
     * @return the matching command word.
     * @throws EveException if the word doesn't match any known command.
     */
    public static CommandWord fromWord(String word) throws EveException {
        try {
            return CommandWord.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EveException("Oops, I don't recognize that command! No worries -- give it another shot?");
        }
    }
}
