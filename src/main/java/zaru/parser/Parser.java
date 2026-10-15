package zaru.parser;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import zaru.command.ByeCommand;
import zaru.command.Command;
import zaru.command.DeadlineCommand;
import zaru.command.DeleteCommand;
import zaru.command.EventCommand;
import zaru.command.FindCommand;
import zaru.command.ListCommand;
import zaru.command.MarkCommand;
import zaru.command.SortCommand;
import zaru.command.TodoCommand;
import zaru.command.UnmarkCommand;
import zaru.exception.ZaruException;

/**
 * Parses raw user input into an executable command.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Parses a raw command line into an executable command.
     *
     * @param message Raw user input.
     * @return Command represented by the input.
     * @throws ZaruException If the user input or command parameters are malformed.
     */
    public static Command parseMessage(String message) throws ZaruException {
        if (message == null || message.isBlank()) {
            throw new ZaruException("Please enter a command!");
        }

        String trimmedMessage = message.trim();
        String[] inputBlocks = trimmedMessage.split("\\s*/\\s*", -1);

        String[] commandParts = inputBlocks[0].trim().split("\\s+", 2);
        String command = commandParts[0].toLowerCase(Locale.ROOT);
        String argument = commandParts.length == 1 ? "" : normalizeWhitespace(commandParts[1]);

        Map<String, String> keyedArguments = parseKeyedArguments(inputBlocks);
        Command parsedCommand = createCommand(command, argument, keyedArguments);
        validateCommandFormat(parsedCommand, argument, keyedArguments);
        return parsedCommand;
    }

    /**
     * Parses slash-prefixed arguments from the input blocks after the command block.
     *
     * @param inputBlocks Command block followed by any keyed argument blocks.
     * @return Keyed arguments mapped to their values.
     * @throws ZaruException If a keyed argument has no value.
     */
    private static Map<String, String> parseKeyedArguments(String[] inputBlocks) throws ZaruException {
        Map<String, String> keyedArguments = new LinkedHashMap<>();

        for (int blockIndex = 1; blockIndex < inputBlocks.length; blockIndex++) {
            String keyedArgument = inputBlocks[blockIndex].trim();
            if (keyedArgument.isEmpty()) {
                throw new ZaruException("Please provide a parameter after /.");
            }

            String[] keyValue = keyedArgument.split("\\s+", 2);
            String key = keyValue[0].toLowerCase(Locale.ROOT);
            if (!key.matches("[a-z]+")) {
                throw new ZaruException("Parameter names after / may only contain letters.");
            }
            if (keyValue.length == 1 || keyValue[1].isBlank()) {
                throw new ZaruException("Please provide a value after /%s.".formatted(key));
            }
            if (keyedArguments.containsKey(key)) {
                throw new ZaruException("The /%s parameter can only be specified once.".formatted(key));
            }

            String value = normalizeWhitespace(keyValue[1]);
            keyedArguments.put(key, value);
        }

        return keyedArguments;
    }

    /**
     * Validates whether a command accepts its supplied positional and keyed arguments.
     *
     * @param command Parsed command.
     * @param argument Parsed positional argument.
     * @param keyedArguments Parsed slash-prefixed arguments.
     * @throws ZaruException If the command receives an unsupported argument.
     */
    private static void validateCommandFormat(Command command, String argument, Map<String, String> keyedArguments)
            throws ZaruException {
        for (String parameter : keyedArguments.keySet()) {
            if (!command.getAllowedKeyedArguments().contains(parameter)) {
                throw new ZaruException(
                        "The /%s parameter is not valid for the %s command."
                                .formatted(parameter, command.getCommand()));
            }
        }

        if (!command.acceptsPositionalArgument() && !argument.isEmpty()) {
            throw new ZaruException("The %s command does not accept additional text."
                    .formatted(command.getCommand()));
        }
    }

    /**
     * Collapses repeated whitespace and removes whitespace at both ends of user-entered text.
     *
     * @param text Text to normalize.
     * @return Normalized text.
     */
    private static String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Creates the command represented by a parsed command word and its arguments.
     *
     * @param command Parsed command word.
     * @param argument Unkeyed command argument.
     * @param keyedArguments Slash-prefixed arguments mapped to their values.
     * @return Command represented by the parsed input.
     * @throws ZaruException If the command word is not supported.
     */
    private static Command createCommand(String command, String argument, Map<String, String> keyedArguments)
            throws ZaruException {
        return switch (command) {
            case "bye" -> new ByeCommand();
            case "list" -> new ListCommand();
            case "sort" -> new SortCommand();
            case "mark" -> new MarkCommand(argument);
            case "unmark" -> new UnmarkCommand(argument);
            case "delete" -> new DeleteCommand(argument);
            case "todo" -> new TodoCommand(argument);
            case "deadline" -> new DeadlineCommand(argument, keyedArguments.get("by"));
            case "event" -> new EventCommand(
                    argument, keyedArguments.get("from"), keyedArguments.get("to"));
            case "find" -> new FindCommand(argument);
            default -> throw new ZaruException("Sorry, I don't know what that means ;w;");
        };
    }
}
