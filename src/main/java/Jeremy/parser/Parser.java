package jeremy.parser;

import jeremy.exception.JeremyException;
import jeremy.task.Deadline;
import jeremy.task.Event;

/** Deals with making sense of the user's raw input command. */
public class Parser {

    /** Returns the command word (first token, lower-cased) of the input. */
    public String getCommandWord(String fullCommand) {
        assert fullCommand != null : "Command must not be null";
        String trimmed = fullCommand.trim();
        String[] parts = trimmed.split("\\s+", 2);
        return parts[0].toLowerCase();
    }

    /** Returns everything after the first token, trimmed. */
    public String getArguments(String fullCommand) {
        assert fullCommand != null : "Command must not be null";
        String trimmed = fullCommand.trim();
        String[] parts = trimmed.split("\\s+", 2);
        return parts.length == 2 ? parts[1].trim() : "";
    }

    /** Parses a task-number argument for mark/unmark/delete style commands. */
    public int parseIndex(String args, String actionWord) throws JeremyException {
        assert args != null : "Command arguments must not be null";
        assert actionWord != null && !actionWord.isBlank() : "Action word must be provided";
        if (args.isEmpty()) {
            throw new JeremyException("Which task? Use: " + actionWord + " <task number>.");
        }
        try {
            return Integer.parseInt(args);
        } catch (NumberFormatException e) {
            throw new JeremyException("'" + args + "' isn't a valid task number.");
        }
    }

    public String parseTodoDescription(String args) throws JeremyException {
        assert args != null : "Todo arguments must not be null";
        if (args.isEmpty()) {
            throw new JeremyException("The description of a todo cannot be empty.");
        }
        return args;
    }

    public Deadline parseDeadline(String args) throws JeremyException {
        assert args != null : "Deadline arguments must not be null";
        if (countOccurrences(args, "/by") > 1) {
            throw new JeremyException("A deadline may contain only one '/by' parameter.");
        }
        int byIndex = args.indexOf("/by");
        String description = byIndex >= 0 ? args.substring(0, byIndex).trim() : args.trim();
        String by = byIndex >= 0 ? args.substring(byIndex + 3).trim() : "";

        if (description.isEmpty() && by.isEmpty()) {
            throw new JeremyException(
                    "A deadline needs a description and a '/by' date/time, e.g. "
                            + "deadline submit report /by 11/10/2019 5pm");
        }
        if (description.isEmpty()) {
            throw new JeremyException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new JeremyException(
                    "A deadline needs a '/by' date/time, e.g. deadline " + description + " /by 11/10/2019 5pm");
        }
        ScheduleValidator.validateDateOrTime(by, "deadline");
        return new Deadline(description, by);
    }

    public Event parseEvent(String args) throws JeremyException {
        assert args != null : "Event arguments must not be null";
        if (countOccurrences(args, "/from") > 1 || countOccurrences(args, "/to") > 1) {
            throw new JeremyException("An event may contain only one '/from' and one '/to' parameter.");
        }
        int fromIndex = args.indexOf("/from");
        int toIndex = args.indexOf("/to");
        String description = fromIndex >= 0 ? args.substring(0, fromIndex).trim() : args.trim();

        String from = "";
        String to = "";
        if (fromIndex >= 0 && toIndex > fromIndex) {
            from = args.substring(fromIndex + 5, toIndex).trim();
            to = args.substring(toIndex + 3).trim();
        } else if (fromIndex >= 0) {
            from = args.substring(fromIndex + 5).trim();
        }

        if (description.isEmpty()) {
            throw new JeremyException("The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new JeremyException(
                    "An event needs both '/from' and '/to' date/time, e.g. "
                            + "event " + description + " /from 2/10/2019 2pm /to 4pm");
        }
        ScheduleValidator.validateDateOrTime(from, "event start time");
        ScheduleValidator.validateDateOrTime(to, "event end time");
        ScheduleValidator.validateEventOrder(from, to);
        return new Event(description, from, to);
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) >= 0) {
            count++;
            index += token.length();
        }
        return count;
    }
}
