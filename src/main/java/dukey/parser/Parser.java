package dukey.parser;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import dukey.command.Command;
import dukey.command.CommandWord;
import dukey.command.DeadlineCommand;
import dukey.command.DeleteCommand;
import dukey.command.EventCommand;
import dukey.command.ExitCommand;
import dukey.command.FindCommand;
import dukey.command.ListCommand;
import dukey.command.MarkCommand;
import dukey.command.OnCommand;
import dukey.command.StatsCommand;
import dukey.command.TodoCommand;
import dukey.command.UnmarkCommand;
import dukey.exception.DukeyException;

/**
 * Makes sense of user input by identifying commands and extracting command arguments.
 */
public class Parser {
    private static final int DESCRIPTION_INDEX = 0;
    private static final int DEADLINE_BY_INDEX = 1;
    private static final int EVENT_FROM_INDEX = 1;
    private static final int EVENT_TO_INDEX = 2;

    private final Clock clock;

    /**
     * Creates a parser that uses the system clock for time-sensitive commands.
     */
    public Parser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser that uses the given clock for time-sensitive commands.
     *
     * @param clock Clock used by parsed commands that depend on the current date/time.
     */
    public Parser(Clock clock) {
        this.clock = clock;
    }

    /**
     * Parses raw user input into an executable command.
     *
     * @param userInput Full command entered by the user.
     * @return Command object representing the user input.
     * @throws DukeyException If the command is unknown or its arguments are invalid.
     */
    public Command parse(String userInput) throws DukeyException {
        String normalizedInput = normalizeCommandInput(userInput);
        CommandWord commandWord = parseCommand(normalizedInput);
        if (commandWord == CommandWord.UNKNOWN) {
            throw new DukeyException("I'm sorry, but I don't know what that means :-(");
        }

        try {
            switch (commandWord) {
                case BYE:
                    return new ExitCommand();
                case LIST:
                    return new ListCommand();
                case ON:
                    return new OnCommand(parseOnDate(normalizedInput));
                case STATS:
                    parseStats(normalizedInput);
                    return new StatsCommand(this.clock);
                case TODO:
                    return new TodoCommand(parseTodoDescription(normalizedInput));
                case DEADLINE: {
                    String[] parts = parseDeadline(normalizedInput);
                    assert parts.length == 2 : "Deadline parsing should return description and by date/time.";
                    return new DeadlineCommand(parts[DESCRIPTION_INDEX], parts[DEADLINE_BY_INDEX]);
                }
                case EVENT: {
                    String[] parts = parseEvent(normalizedInput);
                    assert parts.length == 3
                            : "Event parsing should return description, from date/time, and to date/time.";
                    return new EventCommand(parts[DESCRIPTION_INDEX], parts[EVENT_FROM_INDEX], parts[EVENT_TO_INDEX]);
                }
                case MARK:
                    return new MarkCommand(parseTaskNumber(normalizedInput, CommandWord.MARK,
                            "Please provide a task number to mark."), this.clock);
                case UNMARK:
                    return new UnmarkCommand(parseTaskNumber(normalizedInput, CommandWord.UNMARK,
                            "Please provide a task number to unmark."));
                case DELETE:
                    return new DeleteCommand(parseTaskNumber(normalizedInput, CommandWord.DELETE,
                            "Please provide a task number to delete."));
                case FIND:
                    return new FindCommand(parseFindKeyword(normalizedInput));
                default:
                    throw new DukeyException("I'm sorry, but I don't know what that means :-(");
            }
        } catch (NumberFormatException e) {
            throw new DukeyException("Please provide a valid task number.");
        } catch (DateTimeParseException e) {
            throw new DukeyException(getDateFormatMessage(commandWord));
        }
    }

    /**
     * Parses the command word from raw user input.
     *
     * @param userInput Full command entered by the user.
     * @return Matching command word, or UNKNOWN if the command is unknown.
     */
    public CommandWord parseCommand(String userInput) {
        String normalizedInput = normalizeCommandInput(userInput);
        for (CommandWord command : CommandWord.values()) {
            if (command != CommandWord.UNKNOWN && isCommand(normalizedInput, command)) {
                return command;
            }
        }
        return CommandWord.UNKNOWN;
    }

    /**
     * Parses the date argument of an on command.
     *
     * @param userInput Full on command entered by the user.
     * @return Date to search for.
     * @throws DukeyException If the date argument is missing.
     */
    public LocalDate parseOnDate(String userInput) throws DukeyException {
        String dateText = getCommandArguments(userInput, CommandWord.ON);
        if (dateText.isEmpty()) {
            throw new DukeyException("Please provide a date using format: yyyy-MM-dd");
        }

        return LocalDate.parse(dateText);
    }

    /**
     * Parses a one-based task number from a command.
     *
     * @param userInput Full command entered by the user.
     * @param command Command word whose arguments should be parsed.
     * @param emptyMessage Error message to use if the task number is missing.
     * @return One-based task number.
     * @throws DukeyException If the task number is missing.
     */
    public int parseTaskNumber(String userInput, CommandWord command, String emptyMessage) throws DukeyException {
        String taskNumberText = getCommandArguments(userInput, command);
        if (taskNumberText.isEmpty()) {
            throw new DukeyException(emptyMessage);
        }

        return Integer.parseInt(taskNumberText);
    }

    /**
     * Parses the description from a todo command.
     *
     * @param userInput Full todo command entered by the user.
     * @return Todo description.
     * @throws DukeyException If the description is empty.
     */
    public String parseTodoDescription(String userInput) throws DukeyException {
        String description = getCommandArguments(userInput, CommandWord.TODO);
        if (description.isEmpty()) {
            throw new DukeyException("The description of a todo cannot be empty.");
        }

        return description;
    }

    /**
     * Parses the search keyword from a find command.
     *
     * @param userInput Full find command entered by the user.
     * @return Keyword to search for.
     * @throws DukeyException If the keyword is empty.
     */
    public String parseFindKeyword(String userInput) throws DukeyException {
        String keyword = getCommandArguments(userInput, CommandWord.FIND);
        if (keyword.isEmpty()) {
            throw new DukeyException("Please provide a keyword to find.");
        }

        return keyword;
    }

    /**
     * Checks that a stats command has no arguments.
     *
     * @param userInput Full stats command entered by the user.
     * @throws DukeyException If the stats command has arguments.
     */
    public void parseStats(String userInput) throws DukeyException {
        String arguments = getCommandArguments(userInput, CommandWord.STATS);
        if (!arguments.isEmpty()) {
            throw new DukeyException("The stats command does not take any arguments.");
        }
    }

    /**
     * Parses the description and date/time from a deadline command.
     *
     * @param userInput Full deadline command entered by the user.
     * @return Array containing the description followed by the by date/time.
     * @throws DukeyException If the description, keyword, or date/time is missing.
     */
    public String[] parseDeadline(String userInput) throws DukeyException {
        String input = getCommandArguments(userInput, CommandWord.DEADLINE);
        if (input.isEmpty()) {
            throw new DukeyException("Please provide a deadline task description.");
        }

        String[] parts = parseByKeywords(input, "Please use: deadline {DESCRIPTION} /by {WHEN}", "/by");
        if (parts[DESCRIPTION_INDEX].isEmpty()) {
            throw new DukeyException("Please provide a deadline task description.");
        }

        if (parts[DEADLINE_BY_INDEX].isEmpty()) {
            throw new DukeyException("Please provide a deadline task date/time after /by.");
        }

        return parts;
    }

    /**
     * Parses the description, start date/time, and end date/time from an event command.
     *
     * @param userInput Full event command entered by the user.
     * @return Array containing the description, from date/time, and to date/time.
     * @throws DukeyException If the description, keywords, or date/time values are missing.
     */
    public String[] parseEvent(String userInput) throws DukeyException {
        String input = getCommandArguments(userInput, CommandWord.EVENT);
        if (input.isEmpty()) {
            throw new DukeyException("Please provide a event task description.");
        }

        String[] parts = parseByKeywords(input, "Please use: event {DESCRIPTION} /from {WHEN} /to {WHEN}",
                "/from", "/to");
        if (parts[DESCRIPTION_INDEX].isEmpty()) {
            throw new DukeyException("Please provide a event task description.");
        }

        if (parts[EVENT_FROM_INDEX].isEmpty()) {
            throw new DukeyException("Please provide a event task date/time after /from.");
        }

        if (parts[EVENT_TO_INDEX].isEmpty()) {
            throw new DukeyException("Please provide a event task date/time after /to.");
        }

        return parts;
    }

    private boolean isCommand(String userInput, CommandWord command) {
        String commandWord = command.getWord();
        return userInput.equals(commandWord) || userInput.startsWith(commandWord + " ");
    }

    private String getCommandArguments(String userInput, CommandWord command) {
        String normalizedInput = normalizeCommandInput(userInput);
        return normalizedInput.substring(command.getWord().length()).trim();
    }

    private String getDateFormatMessage(CommandWord commandWord) {
        if (commandWord == CommandWord.ON) {
            return "Please use date format: yyyy-MM-dd";
        } else if (commandWord == CommandWord.DEADLINE) {
            return "Please use deadline date/time format: yyyy-MM-dd HHmm";
        } else if (commandWord == CommandWord.EVENT) {
            return "Please use event date/time format: yyyy-MM-dd HHmm";
        }

        return "Please use a valid date/time format.";
    }

    /**
     * Parses an input string into parts separated by one or more command keywords.
     * The varargs parameter lets commands pass the exact number of keywords they need.
     *
     * @param input Arguments to parse, without the command word.
     * @param errorMessage Message to show if a required keyword is missing.
     * @param keywords Keywords that separate the expected parts.
     * @return Parts split around the given keywords.
     * @throws DukeyException If any keyword is missing.
     */
    private String[] parseByKeywords(String input, String errorMessage, String... keywords) throws DukeyException {
        assert keywords.length > 0 : "Parser keyword helper should receive at least one keyword.";
        String[] result = new String[keywords.length + 1];

        int currentStart = 0;

        for (int i = 0; i < keywords.length; i++) {
            String keyword = keywords[i];

            int keywordIndex = findKeywordIndex(input, keyword, currentStart);
            if (keywordIndex == -1) {
                throw new DukeyException(errorMessage);
            }

            result[i] = input.substring(currentStart, keywordIndex).trim();
            currentStart = keywordIndex + keyword.length();

            while (currentStart < input.length() && input.charAt(currentStart) == ' ') {
                currentStart++;
            }
        }

        result[keywords.length] = input.substring(currentStart).trim();
        ensureNoExtraKeywords(result, errorMessage, keywords);
        return result;
    }

    private void ensureNoExtraKeywords(String[] parts, String errorMessage, String... keywords) throws DukeyException {
        for (String part : parts) {
            for (String keyword : keywords) {
                if (findKeywordIndex(part, keyword, 0) != -1) {
                    throw new DukeyException(errorMessage);
                }
            }
        }
    }

    private String normalizeCommandInput(String userInput) {
        return userInput.trim();
    }

    private int findKeywordIndex(String input, String keyword, int startIndex) {
        int keywordIndex = input.indexOf(keyword, startIndex);
        while (keywordIndex != -1 && !isKeywordDelimited(input, keyword, keywordIndex)) {
            keywordIndex = input.indexOf(keyword, keywordIndex + 1);
        }
        return keywordIndex;
    }

    private boolean isKeywordDelimited(String input, String keyword, int keywordIndex) {
        return isStartOfWord(input, keywordIndex) && isEndOfWord(input, keyword, keywordIndex);
    }

    private boolean isStartOfWord(String input, int keywordIndex) {
        return keywordIndex == 0 || input.charAt(keywordIndex - 1) == ' ';
    }

    private boolean isEndOfWord(String input, String keyword, int keywordIndex) {
        int nextIndex = keywordIndex + keyword.length();
        return nextIndex == input.length() || input.charAt(nextIndex) == ' ';
    }
}
