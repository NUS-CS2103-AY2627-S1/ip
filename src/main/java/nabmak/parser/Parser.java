package nabmak.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Parses and validates user commands.
 */
public class Parser {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final String DEADLINE_FORMAT_ERROR =
            "Deadline must have a '<description> /by <date>'.";
    private static final String EVENT_FORMAT_ERROR =
            "Event must have a '<description> /from <date> /to <date>'.";

    /** Error messages for commands that were entered without any arguments. */
    private static final Map<String, String> MISSING_ARGUMENT_ERRORS = Map.of(
            "find", "Find what?",
            "todo", "You need to say what task you wanna do.",
            "deadline", DEADLINE_FORMAT_ERROR,
            "event", EVENT_FORMAT_ERROR,
            "mark", "mark which task?",
            "unmark", "unmark which task?",
            "delete", "delete which task?",
            "place", "what place?");

    /**
     * Parses and validates user commands.
     *
     * @param input user command
     * @param taskNum number of tasks currently in task list
     * @throws NabmakException if user command is invalid
     */
    public static void parse(String input, int taskNum, int placeNum) throws NabmakException {
        checkMissingArgument(input);

        if (input.startsWith("todo ")) {
            validateTodo(input.substring(5));
        } else if (input.startsWith("deadline ")) {
            validateDeadline(input);
        } else if (input.startsWith("event ")) {
            validateEvent(input);
        } else if (input.equals("bye") || input.equals("list")) {
            return;
        } else if (input.startsWith("mark ")) {
            checkTaskNum(input.substring(5), taskNum, "You arent that busy");
        } else if (input.startsWith("unmark ")) {
            checkTaskNum(input.substring(7), taskNum,
                "You are making yourself busier than needed");
        } else if (input.startsWith("delete ")) {
            checkTaskNum(input.substring(7), taskNum,
                "You cant delete something that doesnt exist");
        } else if (input.startsWith("find ")) {
            validateFind(input.substring(5));
        } else if (input.startsWith("place ")) {
            validatePlace(input.substring(6), placeNum);
        } else {
            throw new NabmakException(
                "Idk whatchu mean. Input either a todo, deadline or event. Or mark, unmark, delete.");
        }
    }

    /**
     * Checks that a command which needs arguments was not entered on its own.
     *
     * @param input user command
     * @throws NabmakException if the command is missing its arguments
     */
    private static void checkMissingArgument(String input) throws NabmakException {
        String error = MISSING_ARGUMENT_ERRORS.get(input);

        if (error != null) {
            throw new NabmakException(error);
        }
    }

    /**
     * Validates the description of a todo command.
     *
     * @param desc text after "todo "
     * @throws NabmakException if the description is empty
     */
    private static void validateTodo(String desc) throws NabmakException {
        if (desc.isEmpty()) {
            throw new NabmakException("You need to say what task you wanna do.");
        }
    }

    /**
     * Validates a full deadline command.
     *
     * @param input full user command, starting with "deadline "
     * @throws NabmakException if the description, "/by" or date is invalid
     */
    private static void validateDeadline(String input) throws NabmakException {
        String info = input.substring(9);
        int mid = info.indexOf(" /by ");

        if (input.startsWith("deadline /by")) {
            throw new NabmakException("Deadline cant have empty description");
        }
        if (mid == -1 && info.indexOf(" /by") >= 0) {
            throw new NabmakException("Deadline cant have empty end date");
        }
        if (mid == -1) {
            throw new NabmakException(DEADLINE_FORMAT_ERROR);
        }

        String desc = info.substring(0, mid);
        if (desc.isEmpty()) {
            throw new NabmakException("Deadline cant have empty description");
        }

        String deadline = info.substring(mid + 5);
        if (deadline.isEmpty()) {
            throw new NabmakException("Deadline cant have empty end date");
        }

        checkDate(deadline, "Deadline date must be 'dd-MM-yyyy HH:mm'.");
    }

    /**
     * Validates a full event command.
     *
     * @param input full user command, starting with "event "
     * @throws NabmakException if the description, "/from", "/to" or either date is invalid
     */
    private static void validateEvent(String input) throws NabmakException {
        String info = input.substring(6);
        int left = info.indexOf(" /from ");

        if (input.startsWith("event /from")) {
            throw new NabmakException("Event cant have empty description");
        }
        if (left == -1 && info.indexOf(" /from") >= 0) {
            throw new NabmakException("Event cant have empty start date");
        }
        if (left == -1) {
            throw new NabmakException(EVENT_FORMAT_ERROR);
        }

        int right = info.indexOf(" /to ");
        if (right == -1) {
            throw new NabmakException(EVENT_FORMAT_ERROR);
        }

        String desc = info.substring(0, left);
        if (desc.isEmpty()) {
            throw new NabmakException("Event cant have empty description");
        }

        validateEventDates(info, left, right);
    }

    /**
     * Validates the start and end dates of an event command.
     *
     * @param info event command without the "event " prefix
     * @param left index of " /from " in info
     * @param right index of " /to " in info
     * @throws NabmakException if either date is empty or badly formatted
     */
    private static void validateEventDates(String info, int left, int right)
            throws NabmakException {
        if (left + 7 > right) {
            throw new NabmakException("Event cant have empty start date");
        }

        String start = info.substring(left + 7, right);
        if (start.isEmpty()) {
            throw new NabmakException("Event cant have empty start date");
        }
        checkDate(start, "Event start date must be 'dd-MM-yyyy HH:mm'.");

        String end = info.substring(right + 5);
        checkDate(end, "Event end date must be 'dd-MM-yyyy HH:mm'.");
    }

    /**
     * Validates the keyword of a find command.
     *
     * @param keyword text after "find "
     * @throws NabmakException if the keyword is empty
     */
    private static void validateFind(String keyword) throws NabmakException {
        if (keyword.isEmpty()) {
            throw new NabmakException("Find what?");
        }
    }

    /**
     * Validates a place command.
     *
     * @param info text after "place "
     * @param placeNum number of places
     * @throws NabmakException if the place command is invalid
     */
    private static void validatePlace(String info, int placeNum) throws NabmakException {
        if (info.equals("list")) {
            return;
        }

        if (info.startsWith("delete ")) {
            checkPlaceNum(info.substring(7), placeNum, "You dont have that many places");
        } else if (info.startsWith("add ")) {
            validatePlaceAdd(info.substring(4));
        } else {
            throw new NabmakException("Use place add, place list or place delete.");
        }
    }

    /**
     * Checks that a date string follows the expected format.
     *
     * @param text date string being checked
     * @param error error message if the date is invalid
     * @throws NabmakException if the date is not in 'dd-MM-yyyy HH:mm' format
     */
    private static void checkDate(String text, String error) throws NabmakException {
        try {
            LocalDateTime.parse(text, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new NabmakException(error);
        }
    }

    /**
     * Checks if given task number is valid.
     *
     * @param input task number being checked
     * @param taskNum number of tasks currently
     * @param error error message if input is invalid
     * @throws NabmakException if input is invalid
     */
    private static void checkTaskNum(String input, int taskNum, String error)
            throws NabmakException {
        checkIndex(input, taskNum, error, "Give valid task number");
    }

    /**
     * Checks if given place number is valid.
     *
     * @param input place number being checked
     * @param placeNum number of places
     * @param error error message if input is invalid
     * @throws NabmakException if input is invalid
     */
    private static void checkPlaceNum(String input, int placeNum, String error)
            throws NabmakException {
        checkIndex(input, placeNum, error, "Give valid place number");
    }

    /**
     * Checks that a string is an integer between 1 and the given size, inclusive.
     *
     * @param input number being checked
     * @param size number of items in the list
     * @param rangeError error message if the number is out of range
     * @param formatError error message if the input is not a number
     * @throws NabmakException if input is not a number or is out of range
     */
    private static void checkIndex(String input, int size, String rangeError,
            String formatError) throws NabmakException {
        int num;

        try {
            num = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new NabmakException(formatError);
        }

        if (num < 1 || num > size) {
            throw new NabmakException(rangeError);
        }
    }

    /**
     * Validates the input for adding a place.
     *
     * @param input input for adding a place
     * @throws NabmakException if input is invalid
     */
    private static void validatePlaceAdd(String input) throws NabmakException {
        int mid = input.indexOf(" /details ");

        if (mid == -1) {
            throw new NabmakException("Place must have a '<name> /details <details>'.");
        }

        String name = input.substring(0, mid);
        String details = input.substring(mid + 10);

        if (name.isEmpty()) {
            throw new NabmakException("Place cant have empty name");
        }
        if (details.isEmpty()) {
            throw new NabmakException("Place cant have empty details");
        }
    }

}
