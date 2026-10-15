package bucket;

import java.time.LocalDate;

/**
 * Splits the line the user typed into the command word and its argument,
 * and turns arguments into the objects the rest of the program works with.
 *
 * This class sits right at the boundary where user input arrives, so the
 * assertions below only cover what callers promise. Whether the line itself is
 * well formed is a separate question, answered by exceptions that Bucket turns
 * into messages.
 */
public class Parser {
    private String command;
    private String argument;

    /**
     * Splits one line of user input into its command word and the rest.
     *
     * @param input Raw line the user typed.
     */
    public Parser(String input) {
        assert input != null : "Parser needs a line, not null";

        // Split once only, so a description keeps any spaces inside it
        String[] parts = input.trim().split(" ", 2);

        // String.split never returns an empty array: even "" yields one element, so
        // parts[0] below is always safe. Recording that here explains why there is
        // no length check guarding the next line.
        assert parts.length >= 1 : "split() must always yield at least one part";

        this.command = parts[0];
        this.argument = parts.length > 1 ? parts[1] : "";
    }

    /**
     * Returns the first word of the input.
     *
     * @return Command word, e.g. "deadline".
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns everything after the first word.
     *
     * @return Argument, or an empty string if there was none.
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Builds a deadline from an argument like "return book /by 2019-10-15".
     *
     * @param argument Part of the line after the command word.
     * @return Deadline described by the argument.
     * @throws java.time.format.DateTimeParseException If the date is not yyyy-mm-dd.
     */
    public static Deadline toDeadline(String argument) {
        assert argument != null : "toDeadline() needs an argument, not null";

        String[] detail = argument.split(" /by ", 2); // Split into description and date

        // Deliberately no assertion on detail.length. A deadline typed without "/by"
        // is the user being human, not a broken assumption, and the resulting
        // ArrayIndexOutOfBoundsException is already answered with a helpful message.
        return new Deadline(detail[0], LocalDate.parse(detail[1]));
    }

    /**
     * Builds an event from an argument like
     * "project meeting /from 2019-10-15 /to 2019-10-16".
     *
     * @param argument Part of the line after the command word.
     * @return Event described by the argument.
     * @throws java.time.format.DateTimeParseException If either date is not yyyy-mm-dd.
     */
    public static Event toEvent(String argument) {
        assert argument != null : "toEvent() needs an argument, not null";

        String[] detail = argument.split(" /from ", 2); // Splits into description and the rest
        String[] fromTo = detail[1].split(" /to ", 2); // Splits the rest into start and end
        return new Event(detail[0], LocalDate.parse(fromTo[0]), LocalDate.parse(fromTo[1]));
    }

    /**
     * Turns the task number the user typed into a list index.
     *
     * @param argument Number as text, e.g. "2".
     * @return Zero-based index, one lower than what the user typed.
     * @throws NumberFormatException If the argument is not a whole number.
     */
    public static int toIndex(String argument) {
        assert argument != null : "toIndex() needs an argument, not null";

        // No assertion that the result is within the list, or even positive. "mark 0"
        // legitimately produces -1 here, and the list is what decides whether an index
        // exists, so checking it at this point would reject input the caller handles.
        return Integer.parseInt(argument) - 1;
    }
}
