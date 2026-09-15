package luke;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luke.commands.AddTaskCommand;
import luke.commands.Command;
import luke.commands.FixedCommand;
import luke.exceptions.InvalidArgumentException;
import luke.exceptions.InvalidCommandException;
import luke.exceptions.InvalidFlagException;
import luke.exceptions.StorageException;
import luke.exceptions.UserInputException;
import luke.storage.ItemListStorage;
import luke.tasks.Flag;
import luke.tasks.ItemList;
import luke.tasks.TaskTypes;

/**
 * A small command-line chatbot. It greets the user, then reads lines of input
 * in a loop. Each line is parsed into a {@link Command}, an argument string,
 * and any flags such as {@code /by}, {@code /from}, or {@code /to}. Invalid user
 * input is reported as a normal chatbot error. Type {@code bye} to exit.
 */
public class Luke {
    private static final String CHATBOT_NAME = "Luke";
    private static final Pattern FLAG_PATTERN = Pattern.compile("(^|\\s)/(\\w+)");

    // ANSI escape codes. These are special strings the terminal reads as
    // "start coloring text" / "stop coloring text" rather than printing them.
    // We use them to make the bot's replies visually distinct from what you type.
    private static final String DEFAULT_BOT_COLOR = "\u001B[36m"; // cyan
    private static final String ERROR_BOT_COLOR = "\u001B[31m"; // red
    private static final String RESET_BOT_COLOR = "\u001B[0m"; // back to normal

    private final ItemList items;
    private final StringBuilder responseBuffer = new StringBuilder();
    private boolean shouldExit;

    /**
     * Creates a chatbot with the task list loaded from ItemListStorage.
     */
    public Luke() {
        ItemList loadedItems;
        try {
            loadedItems = ItemListStorage.load();
        } catch (StorageException e) {
            loadedItems = new ItemList();
            this.error(e.getMessage());
        }
        items = loadedItems;
    }

    /**
     * Returns the shared item list used by commands.
     *
     * @return the task list owned by this chatbot
     */
    public ItemList getItems() {
        return items;
    }

    /**
     * Returns the greeting shown when the chatbot starts.
     *
     * @return the startup greeting text
     */
    public String getWelcomeMessage() {
        return "Hello! I'm %s.\nWhat can I do for you?".formatted(CHATBOT_NAME);
    }

    /**
     * Returns whether the most recent command asked the chatbot to exit.
     *
     * @return true if Luke should stop accepting new input
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Prints the divider used between chatbot interactions.
     */
    private static void printHorizontalLine() {
        System.out.println("-".repeat(60));
    }

    /**
     * Prints the Luke banner shown when the chatbot starts.
     */
    private static void printBanner() {
        String banner = " _         _        \n"
                + "| |  _   _| | _____ \n"
                + "| | | | | | |/ / _ \\\n"
                + "| |_| |_| |   <  __/\n"
                + "|_____\\__,_|_|\\_\\___|\n";
        System.out.println(banner);
    }

    /**
     * Prints a normal chatbot message, followed by a divider.
     *
     * @param message text to show to the user
     */
    public void say(String message) {
        message = message.strip(); // strip trailing \n
        responseBuffer.append(message).append(System.lineSeparator());
        System.out.println(DEFAULT_BOT_COLOR + message + RESET_BOT_COLOR);
        printHorizontalLine();
    }

    /**
     * Prints a user-facing error message, followed by a divider.
     *
     * @param message error text to show to the user
     */
    public void error(String message) {
        message = message.strip(); // strip trailing \n
        responseBuffer.append(message).append(System.lineSeparator());
        System.out.println(ERROR_BOT_COLOR + message + RESET_BOT_COLOR);
        printHorizontalLine();
    }

    /**
     * Prints the startup banner and greeting.
     */
    private void greet() {
        printBanner();
        say(getWelcomeMessage());
    }

    /**
     * A parsed user input line, ready to execute.
     */
    private static class Invocation {
        private final Command command;
        private final String argument;
        private final EnumMap<Flag, String> flags;

        /**
         * Creates a parsed invocation.
         *
         * @param command command to run
         * @param argument argument text after the command keyword
         * @param flags parsed command flags
         */
        Invocation(Command command, String argument, EnumMap<Flag, String> flags) {
            assert command != null : "Invocation requires a command.";
            assert argument != null : "Invocation requires an argument string.";
            assert flags != null : "Invocation requires a flag map.";
            this.command = command;
            this.argument = argument;
            this.flags = flags;
        }
    }

    /**
     * Resolves the first word of an input line into a command.
     *
     * @param keyword the first word typed by the user
     * @return a fixed command or a task-creation command
     * @throws InvalidCommandException if the keyword is not recognized
     */
    private Command parseCommand(String keyword) throws InvalidCommandException {
        Command command = FixedCommand.findByKeyword(keyword);
        if (command != null) {
            return command;
        }

        TaskTypes taskType = TaskTypes.findByKeyword(keyword);
        if (taskType != null) {
            return new AddTaskCommand(taskType);
        }

        throw new InvalidCommandException(keyword);
    }

    /**
     * Records where one flag appears in the raw argument text.
     */
    private static class FlagMatch {
        private final String keyword;
        private final int start;
        private final int valueStart;

        /**
         * Creates a record of one flag found in the raw argument text.
         *
         * @param keyword flag keyword without the leading slash
         * @param start position where the flag starts
         * @param valueStart position where the flag value starts
         */
        FlagMatch(String keyword, int start, int valueStart) {
            assert keyword != null && !keyword.isBlank() : "Flag keyword must be present.";
            assert start >= 0 : "Flag start index must be non-negative.";
            assert valueStart > start : "Flag value must start after the flag keyword.";
            this.keyword = keyword;
            this.start = start;
            this.valueStart = valueStart;
        }
    }

    /**
     * Splits a raw input line into a command, plain argument text, and parsed flags.
     *
     * @param line the full user input line, without surrounding whitespace
     * @return the parsed invocation to execute
     * @throws UserInputException if the command or flags are invalid
     */
    private Invocation parseUserInput(String line) throws UserInputException {
        assert line != null && !line.isBlank() : "Parser expects non-empty user input.";
        assert line.equals(line.strip()) : "Parser expects stripped user input.";
        String argument;
        EnumMap<Flag, String> flags = new EnumMap<>(Flag.class);

        // Split the line into [command] [rest]
        String[] parts = line.split(" ", 2);
        String keyword = parts[0];
        String rest = parts.length > 1 ? parts[1] : "";
        assert !keyword.isBlank() : "Command keyword must be present.";

        Command command = parseCommand(keyword);

        List<FlagMatch> flagMatches = findFlagMatches(rest);

        if (flagMatches.isEmpty()) {
            argument = rest.trim();
        } else {
            argument = rest.substring(0, flagMatches.get(0).start).trim();

            parseFlags(rest, flagMatches, flags);
        }

        return new Invocation(command, argument, flags);
    }

    /**
     * Finds flags that start the argument text or follow whitespace.
     *
     * @param text argument text to search
     * @return flag locations in their original order
     */
    private List<FlagMatch> findFlagMatches(String text) {
        Matcher matcher = FLAG_PATTERN.matcher(text);
        List<FlagMatch> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new FlagMatch(
                    matcher.group(2),
                    matcher.start() + matcher.group(1).length(),
                    matcher.end()));
        }
        return matches;
    }

    /**
     * Extracts and validates values for the flags found in argument text.
     *
     * @param text raw argument text
     * @param matches flag locations in {@code text}
     * @param flags destination for parsed flag values
     * @throws InvalidFlagException if a flag is unknown, empty, or repeated
     */
    private void parseFlags(String text, List<FlagMatch> matches,
                            EnumMap<Flag, String> flags) throws InvalidFlagException {
        for (int i = 0; i < matches.size(); i++) {
            FlagMatch match = matches.get(i);
            int valueEnd = i + 1 < matches.size() ? matches.get(i + 1).start : text.length();
            assert match.valueStart <= valueEnd : "Flag matches should be ordered.";
            String value = text.substring(match.valueStart, valueEnd).trim();
            Flag flag = Flag.findByKeyword(match.keyword);

            if (flag == null) {
                throw InvalidFlagException.unidentified(match.keyword);
            }
            if (value.isEmpty()) {
                throw InvalidFlagException.missingValue(match.keyword);
            }
            if (flags.put(flag, value) != null) {
                throw InvalidFlagException.duplicate(match.keyword);
            }
        }
    }

    /**
     * Runs one line of user input and returns the chatbot's reply.
     *
     * @param input the raw text typed by the user
     * @return Luke's reply to display in the GUI
     */
    public String getResponse(String input) {
        responseBuffer.setLength(0);

        String line = input.strip();
        if (line.isEmpty()) {
            return "";
        }

        try {
            executeLine(line);
        } catch (UserInputException e) {
            this.error(e.getMessage());
        }

        return responseBuffer.toString().strip();
    }

    /**
     * Parses and executes one non-empty line of user input.
     *
     * @param line the stripped user input line
     * @throws UserInputException if the line cannot be parsed or executed
     */
    private void executeLine(String line) throws UserInputException {
        assert line != null && !line.isBlank() : "Executor expects non-empty user input.";
        assert line.equals(line.strip()) : "Executor expects stripped user input.";
        if (line.chars().anyMatch(Character::isISOControl)) {
            throw InvalidArgumentException.unsupportedControlCharacter();
        }
        Invocation invocation = parseUserInput(line);
        invocation.command.execute(this, invocation.argument, invocation.flags);
        if (invocation.command.shouldSaveItemList()) {
            try {
                ItemListStorage.save(items);
            } catch (StorageException e) {
                this.error(e.getMessage());
            }
        }
        shouldExit = invocation.command.shouldExit();
    }

    /**
     * Reads and runs commands until the user exits or the input ends.
     */
    private void run() {
        greet();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().strip();
            if (line.isEmpty()) {
                continue; // ignore blanks
            }

            try {
                executeLine(line);
                if (shouldExit) {
                    return;
                }
            } catch (UserInputException e) {
                this.error(e.getMessage());
            }
        }
    }

    /**
     * Starts the chatbot.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        new Luke().run();
    }
}
