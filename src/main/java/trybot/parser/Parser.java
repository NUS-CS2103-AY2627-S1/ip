package trybot.parser;

import java.util.Locale;

import trybot.command.AddDeadlineCommand;
import trybot.command.AddEventCommand;
import trybot.command.AddTodoCommand;
import trybot.command.Command;
import trybot.command.DeleteCommand;
import trybot.command.EmptyCommand;
import trybot.command.ExitCommand;
import trybot.command.FindCommand;
import trybot.command.HelpCommand;
import trybot.command.ListCommand;
import trybot.command.MarkCommand;
import trybot.command.TagCommand;
import trybot.command.TagDeleteCommand;
import trybot.command.UnknownCommand;
import trybot.command.UnmarkCommand;
import trybot.exception.TryBotException;

/**
 * Interprets raw user input as executable TryBot commands.
 */
public class Parser {
    /**
     * Parses one line of user input into a command object.
     *
     * @param input raw user input.
     * @return executable command
     * @throws TryBotException if a command has malformed structured arguments
     */
    public Command parse(String input) throws TryBotException {
        if (input == null) {
            return new EmptyCommand();
        }

        String command = input.trim();
        if (command.isEmpty()) {
            return new EmptyCommand();
        }
        if (containsControlCharacter(command)) {
            throw new TryBotException("A command cannot contain control characters.");
        }
        return parseCommand(command);
    }

    /**
     * Routes a non-blank command to the parser for its command category.
     *
     * @param command trimmed user input.
     * @return executable command
     * @throws TryBotException if a command has malformed structured arguments
     */
    private Command parseCommand(String command) throws TryBotException {
        if (command.equalsIgnoreCase("bye") || command.equalsIgnoreCase("bye!")) {
            return new ExitCommand();
        }
        if (command.equalsIgnoreCase("list")) {
            return new ListCommand();
        }
        if (command.equalsIgnoreCase("help")) {
            return new HelpCommand();
        }
        return parseTaskCommand(command);
    }

    /**
     * Parses commands that operate on or create tasks.
     *
     * @param command trimmed user input.
     * @return executable task command
     * @throws TryBotException if a command has malformed structured arguments
     */
    private Command parseTaskCommand(String command) throws TryBotException {
        if (startsWithKeyword(command, "find")) {
            return new FindCommand(parseFindKeyword(getCommandBody(command, "find")));
        }
        if (startsWithKeyword(command, "mark")) {
            return new MarkCommand(parseTaskNumber(getCommandBody(command, "mark"), "mark"));
        }
        if (startsWithKeyword(command, "tag")) {
            return parseTag(getCommandBody(command, "tag"));
        }
        if (startsWithKeyword(command, "tagdel")) {
            return new TagDeleteCommand(parseTaskNumber(getCommandBody(command, "tagdel"), "tagdel"));
        }
        if (startsWithKeyword(command, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(getCommandBody(command, "unmark"), "unmark"));
        }
        if (startsWithKeyword(command, "delete")) {
            return new DeleteCommand(parseTaskNumber(getCommandBody(command, "delete"), "delete"));
        }
        if (startsWithKeyword(command, "todo", true)) {
            return new AddTodoCommand(getCommandBody(command, "todo"));
        }
        if (startsWithKeyword(command, "deadline", true)) {
            return new AddDeadlineCommand(parseDeadline(getCommandBody(command, "deadline")));
        }
        if (startsWithKeyword(command, "event", true)) {
            return new AddEventCommand(parseEvent(getCommandBody(command, "event")));
        }
        return new UnknownCommand();
    }

    /** Parses a tag command containing one task number and one tag name. */
    private Command parseTag(String body) throws TryBotException {
        String[] commandParts = body.split("\\s+", 2);
        if (body.isBlank() || commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new TryBotException("Tag needs a task number and tag name. Example: tag 1 important.");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[0]);
        } catch (NumberFormatException exception) {
            throw new TryBotException("The task number must be a whole number. Example: tag 1 important.");
        }
        return new TagCommand(taskNumber, commandParts[1].trim());
    }

    /**
     * Parses the structured fields of a deadline command body.
     *
     * @param body normalized text after the deadline keyword.
     * @return parsed deadline fields
     * @throws TryBotException if the body does not contain valid deadline fields
     */
    private ParsedDeadline parseDeadline(String body) throws TryBotException {
        String lowerCaseBody = body.toLowerCase(Locale.ROOT);
        int byIndex = lowerCaseBody.indexOf("/by");
        if (byIndex < 0 || lowerCaseBody.indexOf("/by", byIndex + 3) >= 0) {
            throw new TryBotException("A deadline needs /by followed by a date or time. "
                    + "Example: deadline report /by Friday.");
        }

        String description = body.substring(0, byIndex).trim();
        String by = body.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new TryBotException("A deadline needs both a description and a date or time. "
                    + "Example: deadline report /by Friday.");
        }
        return new ParsedDeadline(description, by);
    }

    /**
     * Parses the structured fields of an event command body.
     *
     * @param body normalized text after the event keyword.
     * @return parsed event fields
     * @throws TryBotException if the body does not contain valid event fields
     */
    private ParsedEvent parseEvent(String body) throws TryBotException {
        String lowerCaseBody = body.toLowerCase(Locale.ROOT);
        int fromIndex = lowerCaseBody.indexOf("/from");
        int toIndex = lowerCaseBody.indexOf("/to", fromIndex + "/from".length());
        boolean hasDuplicateFrom = fromIndex >= 0 && lowerCaseBody.indexOf("/from", fromIndex + 5) >= 0;
        boolean hasDuplicateTo = toIndex >= 0 && lowerCaseBody.indexOf("/to", toIndex + 3) >= 0;
        if (fromIndex < 0 || toIndex < 0 || hasDuplicateFrom || hasDuplicateTo || toIndex < fromIndex) {
            throw new TryBotException("An event needs /from and /to time details. "
                    + "Example: event meeting /from Monday /to Tuesday.");
        }

        String description = body.substring(0, fromIndex).trim();
        String from = body.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = body.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new TryBotException("An event needs a description, start time, and end time. "
                    + "Example: event meeting /from Monday /to Tuesday.");
        }
        return new ParsedEvent(description, from, to);
    }

    /**
     * Parses the single task number used by mark, unmark, and delete commands.
     *
     * @param body normalized text after the command keyword.
     * @param commandName lowercase command name used in error messages.
     * @return parsed task number
     * @throws TryBotException if the body is missing, has extra values, or is not numeric
     */
    private int parseTaskNumber(String body, String commandName) throws TryBotException {
        String[] commandParts = body.split("\\s+");
        if (body.isEmpty() || commandParts.length != 1) {
            String displayName = Character.toUpperCase(commandName.charAt(0)) + commandName.substring(1);
            throw new TryBotException(displayName + " needs one task number. Example: " + commandName + " 1.");
        }

        if (!commandParts[0].matches("\\d+")) {
            throw new TryBotException("The task number must be a whole number. Example: " + commandName + " 1.");
        }
        try {
            return Integer.parseInt(commandParts[0]);
        } catch (NumberFormatException exception) {
            throw new TryBotException("The task number must be a whole number. Example: " + commandName + " 1.");
        }
    }

    /**
     * Parses the keyword used by a find command.
     *
     * @param body normalized text after the find keyword.
     * @return non-blank search keyword
     * @throws TryBotException if the keyword is missing or blank
     */
    private String parseFindKeyword(String body) throws TryBotException {
        if (body.isBlank()) {
            throw new TryBotException("Find needs a keyword. Example: find book.");
        }
        return body;
    }

    /**
     * Checks whether a command starts with a keyword.
     *
     * @param command trimmed command text.
     * @param keyword keyword to find.
     * @return true when the first command word matches the keyword
     */
    private boolean startsWithKeyword(String command, String keyword) {
        return startsWithKeyword(command, keyword, false);
    }

    /**
     * Checks whether a command starts with a keyword, optionally allowing a colon.
     *
     * @param command trimmed command text.
     * @param keyword keyword to find.
     * @param allowColon whether the keyword may be followed by a colon.
     * @return true when the first command word matches the keyword
     */
    private boolean startsWithKeyword(String command, String keyword, boolean allowColon) {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length == 0) {
            return false;
        }
        return commandParts[0].equalsIgnoreCase(keyword)
                || (allowColon && commandParts[0].equalsIgnoreCase(keyword + ":"));
    }

    /**
     * Extracts the text after a command keyword and removes an optional colon.
     *
     * @param command command text beginning with the keyword.
     * @param keyword command keyword to remove.
     * @return text after the keyword
     */
    private String getCommandBody(String command, String keyword) {
        String body = command.substring(keyword.length()).trim();
        if (body.startsWith(":")) {
            body = body.substring(1).trim();
        }
        return body;
    }

    /** Returns whether input contains characters that cannot safely be treated as command text. */
    private boolean containsControlCharacter(String command) {
        return command.chars().anyMatch(character -> Character.isISOControl(character)
                && !Character.isWhitespace(character));
    }

    /**
     * Holds the fields needed to create a deadline task.
     *
     * @param description deadline description.
     * @param by date or time by which the task should be completed.
     */
    public record ParsedDeadline(String description, String by) {
    }

    /**
     * Holds the fields needed to create an event task.
     *
     * @param description event description.
     * @param from event start date or time.
     * @param to event end date or time.
     */
    public record ParsedEvent(String description, String from, String to) {
    }
}
