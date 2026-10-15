package kevin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser Class that handles reading the user command.
 * Handles 1 String input at a time.
 */
public class Parser {
    private String input;

    /**
     * Creates new Parser.
     * Trims leading and trailing whitespaces.
     *
     * @param input Command line input.
     */
    public Parser(String input) {
        this.input = input.trim();
    }

    /**
     * Checks if input is bye.
     */
    public boolean isBye() {
        return input.equals("bye");
    }

    /**
     * Checks if input is list.
     */
    public boolean isList() {
        return input.equals("list");
    }

    /**
     * Checks if input starts with String command.
     *
     * @param command Command keyword.
     */
    public boolean startsWith(String command) {
        return input.startsWith(command);
    }

    /**
     * Parses regex pattern in input.
     *
     * @param regex Pattern to parse in input.
     * @return Matcher containing parsed groups.
     */
    public Matcher parseInput(String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input);
    }

    /**
     * Parses index from input of the form <command> <index>.
     *
     * @param command Command keyword.
     * @throws KevinException If input does not have a task number.
     */
    public int parseIndex(String command) throws KevinException {
        //Input must start with command
        assert input.startsWith(command);

        Matcher matcher = parseInput(command + "\\s+(\\d+)");
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) - 1;
        } else {
            throw new KevinException("Must include a task number.");
        }
    }

    /**
     * Parses by DateTime from input.
     *
     * @return By date as a String.
     * @throws KevinException If no by date.
     */
    public String parseByDate() throws KevinException {
        Matcher matcher = parseInput("/by\\s+(?<by>.+)$");
        if (matcher.find()) {
            return matcher.group("by");
        } else {
            throw new KevinException("Must include a by date.");
        }
    }

    /**
     * Parses from and to DateTime from input.
     *
     * @throws KevinException If no from and/or to date.
     */
    public Matcher parseFromAndToDate() throws KevinException {
        Matcher matcher = parseInput("/from\\s+(?<from>.+?)\\s+"
                + "/to\\s+(?<to>.+)$");
        if (matcher.find()) {
            return matcher;
        } else {
            throw new KevinException("Must include a from and to date.");
        }
    }

    /**
     * Parses keyword in find command.
     *
     * @throws KevinException If no keyword provided
     */
    public String parseKeyword() throws KevinException {
        //Input must start with find
        assert input.startsWith("find");

        Matcher matcher = parseInput("^find\\s+(.+)$");
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new KevinException("Must include a keyword to search for.");
        }
    }

    /**
     * Parses ToDo from input.
     *
     * @return Description of ToDo.
     * @throws KevinException If input does not have a description.
     */
    public String parseToDo() throws KevinException {
        //Input must start with todo
        assert input.startsWith("todo");

        Matcher matcher = parseInput("^todo\\s+(?<description>\\S+.*?)$");
        if (matcher.matches()) {
            return matcher.group("description");
        } else {
            throw new KevinException("ToDo does not have a description.");
        }
    }

    /**
     * Parses Deadline from input.
     *
     * @return Matcher containing description and by date.
     * @throws KevinException If input does not have a description or by date.
     */
    public Matcher parseDeadline() throws KevinException {
        //Input must start with deadline
        assert input.startsWith("deadline");

        String regex = "^deadline\\s+(?<description>\\S+.*?)\\s+/by\\s+(?<by>.+)$";
        Matcher matcher = parseInput(regex);

        if (matcher.matches()) {
            return matcher;
        } else {
            throw new KevinException("Deadline does not have a description or a by date.");
        }
    }

    /**
     * Parses Event from input.
     *
     * @return Matcher containing description, from date and to date.
     * @throws KevinException If input does not have a description, from date or to date.
     */
    public Matcher parseEvent() throws KevinException {
        //Input must start with event
        assert input.startsWith("event");

        String regex = "^event\\s+(?<description>\\S+.*?)\\s+"
                + "/from\\s+(?<from>.+?)\\s+"
                + "/to\\s+(?<to>.+)$";
        Matcher matcher = parseInput(regex);

        if (matcher.matches()) {
            return matcher;
        } else {
            throw new KevinException("Event does not have a description, from or to date.");
        }
    }
}
