package anby;

/**
 * Parses user input into commands and command arguments.
 */
public class Parser {
    private static final String COMMAND_SEPARATOR = " ";
    private static final int COMMAND_PART_LIMIT = 2;
    private static final int ARGUMENT_INDEX = 1;
    private static final int FIRST_PART_INDEX = 0;
    private static final int EXPECTED_PART_COUNT = 2;
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_START_SEPARATOR = "/from";
    private static final String EVENT_END_SEPARATOR = "/to";

    /**
     * Creates a parser.
     */
    public Parser() {
    }

    /**
     * Splits user input into a command word and the remaining arguments.
     *
     * @param input full user input
     * @return array containing the command word and, if present, the remaining arguments
     */
    public String[] splitInput(String input) {
        return input.split(COMMAND_SEPARATOR, COMMAND_PART_LIMIT);
    }

    /**
     * Converts a command word into the matching command.
     *
     * @param commandWord command word entered by the user
     * @return matching command
     * @throws IllegalArgumentException if the command word is not recognised
     */
    public Command parseCommand(String commandWord) {
        String normalizedCommandWord = commandWord.toLowerCase();
        Command aliasCommand = parseAlias(normalizedCommandWord);

        if (aliasCommand != null) {
            return aliasCommand;
        }

        return Command.valueOf(normalizedCommandWord.toUpperCase());
    }

    private Command parseAlias(String commandWord) {
        switch (commandWord) {
            case "ls":
                return Command.LIST;
            case "m":
                return Command.MARK;
            case "um":
                return Command.UNMARK;
            case "del":
                return Command.DELETE;
            case "q":
                return Command.BYE;
            case "t":
                return Command.TODO;
            case "d":
                return Command.DEADLINE;
            case "e":
                return Command.EVENT;
            case "f":
                return Command.FIND;
            default:
                return null;
        }
    }

    private boolean hasNoArguments(String[] parts) {
        return parts.length <= ARGUMENT_INDEX;
    }

    private String getArguments(String[] parts) {
        return parts[ARGUMENT_INDEX];
    }

    /**
     * Returns the task number argument from a command.
     *
     * @param parts command input split into command word and arguments
     * @param errorMessage message to show if the task number is missing
     * @return task number argument
     * @throws AnbyException if the task number is missing
     */
    public String parseTaskNumber(String[] parts, String errorMessage) throws AnbyException {
        if (hasNoArguments(parts)) {
            throw new AnbyException(errorMessage);
        }

        return getArguments(parts);
    }

    /**
     * Returns the keyword for a find command.
     *
     * @param parts find input split into command word and arguments
     * @return keyword
     * @throws AnbyException if the keyword is missing
     */
    public String parseFind(String[] parts) throws AnbyException {
        if (hasNoArguments(parts) || getArguments(parts).trim().isEmpty()) {
            throw new AnbyException("what am i supposed to find but burgers?\ntry: find burger");
        }

        return getArguments(parts).trim();
    }

    /**
     * Returns the description for a todo command.
     *
     * @param parts todo input split into command word and arguments
     * @return todo description
     * @throws AnbyException if the description is missing
     */
    public String parseTodo(String[] parts) throws AnbyException {
        if (hasNoArguments(parts) || getArguments(parts).trim().isEmpty()) {
            throw new AnbyException("hey put a todo down dont be lazy\ntry: todo buy burgers");
        }

        return getArguments(parts).trim();
    }

    /**
     * Parses a deadline command into task description and due date.
     *
     * @param parts deadline input split into command word and arguments
     * @return array containing the deadline description and due date
     * @throws AnbyException if the description or due date is missing
     */
    public String[] parseDeadline(String[] parts) throws AnbyException {
        if (hasNoArguments(parts)) {
            throw new AnbyException("whats the deadline? is it before dinner?\n"
                    + "try: deadline eat burgers /by 2026-09-18");
        }

        String[] deadlineParts = getArguments(parts).split(DEADLINE_SEPARATOR, COMMAND_PART_LIMIT);

        if (deadlineParts.length != EXPECTED_PART_COUNT) {
            throw new AnbyException("whats the deadline? is it before dinner?\n"
                    + "try: deadline cook burgers /by 2026-09-18");
        }

        return new String[] {
                deadlineParts[0].trim(),
                deadlineParts[1].trim()
        };
    }

    /**
     * Parses an event command into task description, start date, and end date.
     *
     * @param parts event input split into command word and arguments
     * @return array containing the event description, start date, and end date
     * @throws AnbyException if the description, start date, or end date is missing
     */
    public String[] parseEvent(String[] parts) throws AnbyException {
        if (hasNoArguments(parts)) {
            throw new AnbyException("what event is it? count me in if its lunch\n"
                    + "try: event burger run /from 2026-09-14 /to 2026-09-18");
        }

        String[] eventParts = getArguments(parts).split(EVENT_START_SEPARATOR, COMMAND_PART_LIMIT);

        if (eventParts.length != EXPECTED_PART_COUNT) {
            throw new AnbyException("when does the event start?\n"
                    + "try: event fast food haul /from 2026-09-14 /to 2026-09-18");
        }

        String[] timeParts = eventParts[ARGUMENT_INDEX].split(EVENT_END_SEPARATOR, COMMAND_PART_LIMIT);

        if (timeParts.length != EXPECTED_PART_COUNT) {
            throw new AnbyException("hmm an event has to end even if it concerns burgers...\n"
                    + "try: event post food nap /from 2026-09-14 /to 2026-09-18");
        }

        return new String[] {
                eventParts[FIRST_PART_INDEX].trim(),
                timeParts[FIRST_PART_INDEX].trim(),
                timeParts[ARGUMENT_INDEX].trim()
        };
    }
}
