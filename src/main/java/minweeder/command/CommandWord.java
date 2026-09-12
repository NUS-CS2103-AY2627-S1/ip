package minweeder.command;

import minweeder.exception.MinweederException;

/**
 * The set of commands the application recognizes from user input.
 */
public enum CommandWord {
    LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, LOAN, DELETE, ON, FIND, BYE;

    /**
     * Resolves the first word of user input into a known {@link CommandWord}.
     *
     * @param word the first word of the user's input, e.g. "todo".
     * @return the matching {@link CommandWord}.
     * @throws MinweederException if the word does not match any known command.
     */
    public static CommandWord getCommandWord(String word) throws MinweederException {
        try {
            return CommandWord.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new MinweederException("That's not even a command? Theres todo, deadline, event, loan, "
                    + "list, mark, unmark, delete, on, find, bye.");
        }
    }
}
