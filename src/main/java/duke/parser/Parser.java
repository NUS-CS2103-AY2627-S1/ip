package duke.parser;

import duke.command.AddCommand;
import duke.command.AddPlaceCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.DeletePlaceCommand;
import duke.command.ExitCommand;
import duke.command.FindCommand;
import duke.command.FindPlaceCommand;
import duke.command.ListCommand;
import duke.command.ListPlacesCommand;
import duke.command.MarkCommand;
import duke.command.UndoCommand;
import duke.command.UnmarkCommand;
import duke.exception.DukeException;
import duke.place.Place;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.ToDo;

/**
 * Parses user input strings into actionable Command objects.
 */
public class Parser {
    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses the user's full input string and returns the corresponding Command.
     *
     * @param fullCommand The raw input string from the user.
     * @return The Command representing the parsed user instruction.
     * @throws DukeException If the command is invalid or missing required parameters.
     */
    public static Command parse(String fullCommand) throws DukeException {
        assert fullCommand != null : "Command string must not be null";
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new DukeException("OOPS!!! Command cannot be empty.");
        }

        String[] parts = trimmed.split("\\s+");
        String commandWord = parts[0];

        switch (commandWord) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(parseTaskIndex(parts));
            case "unmark":
                return new UnmarkCommand(parseTaskIndex(parts));
            case "delete":
                return new DeleteCommand(parseTaskIndex(parts));
            case "todo":
                return parseToDo(trimmed, commandWord);
            case "deadline":
                return parseDeadline(trimmed, commandWord);
            case "event":
                return parseEvent(trimmed, commandWord);
            case "find":
                return parseFind(trimmed, commandWord);
            case "undo":
                return new UndoCommand();
            case "place":
                return parsePlace(trimmed, commandWord);
            case "places":
                return new ListPlacesCommand();
            case "deleteplace":
                return new DeletePlaceCommand(parsePlaceIndex(parts));
            case "findplace":
                return parseFindPlace(trimmed, commandWord);
            default:
                throw new DukeException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    private static int parseTaskIndex(String[] parts) throws DukeException {
        if (parts.length < 2) {
            throw new DukeException("OOPS!!! The task number cannot be empty.");
        }
        if (parts.length > 2) {
            throw new DukeException("OOPS!!! Please provide only a single task number.");
        }
        try {
            int index = Integer.parseInt(parts[1]);
            if (index <= 0) {
                throw new DukeException("OOPS!!! The task number must be greater than 0.");
            }
            return index - 1;
        } catch (NumberFormatException e) {
            throw new DukeException("OOPS!!! The task number provided is invalid.");
        }
    }

    private static Command parseToDo(String trimmed, String commandWord) throws DukeException {
        String todoDesc = trimmed.substring(commandWord.length()).trim();
        if (todoDesc.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a todo cannot be empty.");
        }
        validateNoPipe(todoDesc, "description");
        return new AddCommand(new ToDo(todoDesc));
    }

    private static Command parseDeadline(String trimmed, String commandWord) throws DukeException {
        String deadlineInput = trimmed.substring(commandWord.length()).trim();
        if (deadlineInput.isEmpty()) {
            throw new DukeException("OOPS!!! The description of a deadline cannot be empty.");
        }
        validateNoPipe(deadlineInput, "deadline details");

        if (countOccurrences(deadlineInput, " /by ") > 1) {
            throw new DukeException("OOPS!!! Multiple /by parameters are not allowed.");
        }

        String[] deadlineParts = deadlineInput.split(" /by ");
        if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty()
                || deadlineParts[1].trim().isEmpty()) {
            throw new DukeException("OOPS!!! The description and /by time of a deadline cannot be empty.");
        }
        return new AddCommand(new Deadline(deadlineParts[0].trim(), deadlineParts[1].trim()));
    }

    private static Command parseEvent(String trimmed, String commandWord) throws DukeException {
        String eventInput = trimmed.substring(commandWord.length()).trim();
        if (eventInput.isEmpty()) {
            throw new DukeException("OOPS!!! The description of an event cannot be empty.");
        }
        validateNoPipe(eventInput, "event details");

        if (countOccurrences(eventInput, " /from ") > 1) {
            throw new DukeException("OOPS!!! Multiple /from parameters are not allowed.");
        }
        if (countOccurrences(eventInput, " /to ") > 1) {
            throw new DukeException("OOPS!!! Multiple /to parameters are not allowed.");
        }

        int fromIndex = eventInput.indexOf(" /from ");
        int toIndex = eventInput.indexOf(" /to ");
        if (fromIndex != -1 && toIndex != -1 && toIndex < fromIndex) {
            throw new DukeException("OOPS!!! The /from parameter must appear before the /to parameter.");
        }

        String[] eventParts = eventInput.split(" /from ");
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new DukeException("OOPS!!! The description and /from time of an event cannot be empty.");
        }
        String[] timeParts = eventParts[1].split(" /to ");
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
            throw new DukeException("OOPS!!! The /to time of an event cannot be empty.");
        }
        return new AddCommand(new Event(eventParts[0].trim(), timeParts[0].trim(), timeParts[1].trim()));
    }

    private static Command parseFind(String trimmed, String commandWord) throws DukeException {
        String keyword = trimmed.substring(commandWord.length()).trim();
        if (keyword.isEmpty()) {
            throw new DukeException("OOPS!!! The search keyword cannot be empty.");
        }
        validateNoPipe(keyword, "search keyword");
        return new FindCommand(keyword);
    }

    private static int parsePlaceIndex(String[] parts) throws DukeException {
        if (parts.length < 2) {
            throw new DukeException("OOPS!!! The place number cannot be empty.");
        }
        if (parts.length > 2) {
            throw new DukeException("OOPS!!! Please provide only a single place number.");
        }
        try {
            int index = Integer.parseInt(parts[1]);
            if (index <= 0) {
                throw new DukeException("OOPS!!! The place number must be greater than 0.");
            }
            return index - 1;
        } catch (NumberFormatException e) {
            throw new DukeException("OOPS!!! The place number provided is invalid.");
        }
    }

    private static Command parsePlace(String trimmed, String commandWord) throws DukeException {
        String placeInput = trimmed.substring(commandWord.length()).trim();
        if (placeInput.isEmpty()) {
            throw new DukeException("OOPS!!! The name of a place cannot be empty.");
        }
        validateNoPipe(placeInput, "place details");

        boolean hasDetails = placeInput.contains(" /details ");
        boolean hasDesc = placeInput.contains(" /desc ");
        if (hasDetails && hasDesc) {
            throw new DukeException("OOPS!!! Cannot specify both /details and /desc.");
        }
        if (countOccurrences(placeInput, " /details ") > 1) {
            throw new DukeException("OOPS!!! Multiple /details parameters are not allowed.");
        }
        if (countOccurrences(placeInput, " /desc ") > 1) {
            throw new DukeException("OOPS!!! Multiple /desc parameters are not allowed.");
        }

        String name;
        String details = "";
        if (hasDetails) {
            String[] split = placeInput.split(" /details ", 2);
            name = split[0].trim();
            details = split.length > 1 ? split[1].trim() : "";
        } else if (hasDesc) {
            String[] split = placeInput.split(" /desc ", 2);
            name = split[0].trim();
            details = split.length > 1 ? split[1].trim() : "";
        } else {
            name = placeInput;
        }

        if (name.isEmpty()) {
            throw new DukeException("OOPS!!! The name of a place cannot be empty.");
        }
        return new AddPlaceCommand(new Place(name, details));
    }

    private static Command parseFindPlace(String trimmed, String commandWord) throws DukeException {
        String keyword = trimmed.substring(commandWord.length()).trim();
        if (keyword.isEmpty()) {
            throw new DukeException("OOPS!!! The search keyword cannot be empty.");
        }
        validateNoPipe(keyword, "search keyword");
        return new FindPlaceCommand(keyword);
    }

    private static void validateNoPipe(String input, String fieldName) throws DukeException {
        if (input.contains("|")) {
            throw new DukeException("OOPS!!! The character '|' is reserved and cannot be used in " + fieldName + ".");
        }
    }

    private static int countOccurrences(String text, String target) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(target, idx)) != -1) {
            count++;
            idx += target.length();
        }
        return count;
    }
}
