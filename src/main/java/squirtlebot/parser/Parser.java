package squirtlebot.parser;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import squirtlebot.command.AddDeadlineCommand;
import squirtlebot.command.AddEventCommand;
import squirtlebot.command.AddToDoCommand;
import squirtlebot.command.Command;
import squirtlebot.command.ConfirmEventDateCommand;
import squirtlebot.command.DeleteCommand;
import squirtlebot.command.ExitCommand;
import squirtlebot.command.FindCommand;
import squirtlebot.command.ListCommand;
import squirtlebot.command.MarkCommand;
import squirtlebot.command.UnmarkCommand;
import squirtlebot.exception.CommandException;

/**
 * Parses user input strings to identify the command the user would like to execute.
 */
public class Parser {
    private static final int NUMBER_OF_COMMANDS = 10;

    private final HashMap<String, Function<String[], Command>> commandsByName = new HashMap<>();

    /**
     * Constructs a Parser object.
     * Initializes {@code commandsByName} to contain the various mappings of user input to command.
     */
    public Parser() {
        commandsByName.put("todo", x -> new AddToDoCommand(x));
        commandsByName.put("deadline", x -> new AddDeadlineCommand(x));
        commandsByName.put("event", x -> new AddEventCommand(x));
        commandsByName.put("mark", x -> new MarkCommand(x));
        commandsByName.put("unmark", x -> new UnmarkCommand(x));
        commandsByName.put("bye", x -> new ExitCommand());
        commandsByName.put("list", x -> new ListCommand());
        commandsByName.put("find", x -> new FindCommand(x));
        commandsByName.put("delete", x -> new DeleteCommand(x));
        commandsByName.put("confirm", x -> new ConfirmEventDateCommand(x));

        assert commandsByName.size() == NUMBER_OF_COMMANDS;
        assert commandsByName.values().stream().allMatch(function -> function != null);
    }

    /**
     * Returns {@link Command} representing the user input.
     *
     * @param userInput string representing command to execute and its parameters.
     * @return a {@link Command} object representing the user-entered command.
     * @throws CommandException if user specifies an unsupported command, or input is null/blank.
     */
    public Command parseCommand(String userInput) {
        validateUserInput(userInput);

        String[] inputTokens = userInput.trim().split("\\s+");

        String commandKeyword = inputTokens[0];
        Function<String[], Command> commandFunction = Optional.ofNullable(commandsByName.get(commandKeyword))
                .orElseThrow(() -> new CommandException("Invalid command"));

        assert commandFunction != null;
        return commandFunction.apply(inputTokens);
    }

    /**
     * Scans user input for values belonging to a specified token.
     * Stops when it detects the start of other tokens, identified by a preceding "/" character.
     *
     * @param inputTokens array containing user input to scan for tokens.
     * @param expectedToken token to identify values for.
     * @return values belonging to {@code expectedToken}.
     */
    public String parseTokens(String[] inputTokens, String expectedToken) {
        boolean isExpectedTokenIdentified = false;
        String assembledToken = "";

        for (String currentToken : inputTokens) {
            boolean isDoneReadingExpectedToken = currentToken.startsWith("/") && isExpectedTokenIdentified;

            if (currentToken.equals(expectedToken)) {
                isExpectedTokenIdentified = true;
                continue;
            } else if (isDoneReadingExpectedToken) {
                break;
            } else if (currentToken.startsWith("/")) {
                isExpectedTokenIdentified = false;
            }

            if (!isExpectedTokenIdentified) {
                continue;
            }
            assembledToken += (currentToken + " ");
        }

        return assembledToken.trim();
    }

    /**
     * Scans user input for text belonging to a task's description.
     * Stops upon reading tokens/parameters of a command, identified by a preceding "/" character.
     *
     * @param inputTokens array of user inputs containing a task description.
     * @return text describing a task.
     */
    public String parseDescription(String[] inputTokens) {
        String assembledDescription = "";

        for (int i = 1; i < inputTokens.length; i++) {
            String currentText = inputTokens[i];
            if (currentText.startsWith("/")) {
                break;
            }
            assembledDescription += (currentText + " ");
        }

        return assembledDescription.trim();
    }

    private void validateUserInput(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            throw new CommandException("Please enter a command :(");
        }
    }
}
