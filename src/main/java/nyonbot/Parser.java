package nyonbot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nyonbot.command.Command;
import nyonbot.command.CommandType;
import nyonbot.command.DeadlineCommand;
import nyonbot.command.DeleteCommand;
import nyonbot.command.EchoCommand;
import nyonbot.command.EventCommand;
import nyonbot.command.ExitCommand;
import nyonbot.command.FindCommand;
import nyonbot.command.HelpCommand;
import nyonbot.command.ListCommand;
import nyonbot.command.ManCommand;
import nyonbot.command.MarkCommand;
import nyonbot.command.NoCommand;
import nyonbot.command.NyonCommand;
import nyonbot.command.OnCommand;
import nyonbot.command.TodoCommand;
import nyonbot.command.UnmarkCommand;

/**
 * Converts command-line input into executable commands.
 *
 * @author nje14
 */
public class Parser {
    private static final Pattern FLAG_PATTERN =
            Pattern.compile("(?<!\\S)(--[A-Za-z][A-Za-z0-9-]*)(?=\\s|$)");
    private static final DateTimeFormatter DAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static Parser instance = null;

    private Parser() {
    }

    public static synchronized Parser getInstance() {
        if (instance == null) {
            instance = new Parser();
        }
        return instance;
    }

    /**
     * Parses raw input into a command.
     *
     * @param input raw input.
     * @return command associated with this input.
     * @throws IllegalArgumentException if the command contains invalid flags.
     */
    public Command parse(String input) {
        HashMap<String, String> arguments = parseArguments(input);
        String command = arguments.get(Command.COMMAND_KEY);
        if (command == null || command.isBlank()) {
            return new NoCommand();
        }
        // easter egg check
        if (command.equals("man")) {
            int mills = Calendar.getInstance().get(Calendar.MILLISECOND) / 10;
            if (mills == 6 || mills == 66) {
                return new ManCommand();
            }
        }

        CommandType type = CommandType.toCommandType(command);
        validateFlags(type, arguments);
        return switch (type) {
            case EXIT -> new ExitCommand(arguments);
            case ECHO -> new EchoCommand(arguments);
            case NYON -> new NyonCommand();
            case LIST -> new ListCommand(arguments, Logic.getInstance().getList());
            case TODO -> new TodoCommand(arguments, Logic.getInstance().getList());
            case DEADLINE -> new DeadlineCommand(arguments, Logic.getInstance().getList());
            case EVENT -> new EventCommand(arguments, Logic.getInstance().getList());
            case MARK -> new MarkCommand(arguments, Logic.getInstance().getList());
            case UNMARK -> new UnmarkCommand(arguments, Logic.getInstance().getList());
            case DELETE -> new DeleteCommand(arguments, Logic.getInstance().getList());
            case FIND -> new FindCommand(arguments, Logic.getInstance().getList());
            case HELP -> new HelpCommand(arguments);
            case ON -> new OnCommand(arguments, Logic.getInstance().getList());
            default -> throw new IllegalArgumentException("unrecognized command");
        };
    }

    private void validateFlags(CommandType type, HashMap<String, String> arguments) {
        Set<String> allowedFlags = getAllowedFlags(type);
        for (String key : arguments.keySet()) {
            if (key.startsWith("--") && !allowedFlags.contains(key)) {
                throw new IllegalArgumentException(
                        String.format("%s does not support the %s flag", type.keyword(), key));
            }
        }
    }

    private Set<String> getAllowedFlags(CommandType type) {
        return switch (type) {
            case DEADLINE -> Set.of("--by");
            case EVENT -> Set.of("--from", "--to");
            default -> Set.of();
        };
    }

    /**
     * Parses command-line input into a command, positional description, and
     * flag-value pairs.
     *
     * @param input raw command-line input.
     * @return parsed arguments keyed by {@code command}, {@code description},
     *         or their literal flag such as {@code --by}.
     * @throws IllegalArgumentException if a flag is specified more than once.
     */
    public HashMap<String, String> parseArguments(String input) {
        HashMap<String, String> arguments = new HashMap<>();
        String normalizedInput = input == null ? "" : input.strip();
        if (normalizedInput.isBlank()) {
            return arguments;
        }

        String[] commandParts = normalizedInput.split("\\s+", 2);
        arguments.put(Command.COMMAND_KEY, commandParts[0]);
        if (commandParts.length == 1) {
            return arguments;
        }

        String rawArguments = commandParts[1];
        Matcher matcher = FLAG_PATTERN.matcher(rawArguments);
        int firstFlagIndex = rawArguments.length();
        int previousFlagValueIndex = -1;
        String previousFlag = null;

        while (matcher.find()) {
            String currentFlag = matcher.group(1);
            ensureFlagIsUnique(arguments, currentFlag, previousFlag);
            if (previousFlag == null) {
                firstFlagIndex = matcher.start();
            } else {
                arguments.put(previousFlag,
                        rawArguments.substring(previousFlagValueIndex, matcher.start()).strip());
            }
            previousFlag = currentFlag;
            previousFlagValueIndex = matcher.end();
        }

        String description = rawArguments.substring(0, firstFlagIndex).strip();
        if (!description.isBlank()) {
            arguments.put(Command.DESCRIPTION_KEY, description);
        }
        if (previousFlag != null) {
            arguments.put(previousFlag,
                    rawArguments.substring(previousFlagValueIndex).strip());
        }
        return arguments;
    }

    private void ensureFlagIsUnique(HashMap<String, String> arguments,
            String currentFlag, String previousFlag) {
        if (currentFlag.equals(previousFlag) || arguments.containsKey(currentFlag)) {
            throw new IllegalArgumentException(
                    String.format("the %s flag was specified more than once", currentFlag));
        }
    }

    /**
     * Parses a string date using the format {@code dd/MM/yyyy HHmm}.
     *
     * @param date date to be parsed.
     * @return parsed date, or {@code null} when the input does not match the format.
     */
    public static LocalDateTime parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
        try {
            return LocalDateTime.parse(date, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a day using the format {@code dd/MM/yyyy}.
     *
     * @param day day to be parsed.
     * @return parsed day, or {@code null} when the input does not match the format.
     */
    public static LocalDate parseDay(String day) {
        try {
            return LocalDate.parse(day, DAY_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
