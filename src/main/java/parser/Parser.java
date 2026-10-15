package parser;

import cxception.EmptyEventException;
import cxception.InvalidDateException;
import cxception.MarkOutException;
import tasklist.Deadline;
import tasklist.Event;
import tasklist.Task;
import tasklist.Tasklist;

/**
 * Parses raw user input strings into commands and executes them against a {@link Tasklist}.
 */
public class Parser {
    private Tasklist tL;

    /**
     * Constructs a Parser bound to the given task list.
     *
     * @param tL the task list to operate on.
     */
    public Parser(Tasklist tL) {
        this.tL = tL;
    }

    /**
     * Parses the given input string, identifies the command, and executes it.
     * Supported commands: mark, unmark, delete, find, deadline, event, todo.
     * Any unrecognized command is treated as a plain task to add.
     *
     * @param inp the raw user input.
     * @return the response message to display to the user.
     */
    public String firstParse(String inp) {
        try {
            inp = inp.trim();
            int idxCmdSubstr = inp.contains(" ") ? inp.indexOf(" ") : inp.length();
            if (idxCmdSubstr == inp.length()) {
                return addToStruct(inp);
            }
            String potentialCmd = inp.substring(0, idxCmdSubstr);
            String restOfCmd = inp.substring(idxCmdSubstr).trim();
            boolean isNum = restOfCmd.chars().allMatch(Character::isDigit);
            int idxInConsideration = isNum ? Integer.parseInt(restOfCmd) : inp.length();

            switch (potentialCmd) {
                case "mark":
                    if (!isNum) {
                        return addToStruct(inp);
                    }
                    if (idxInConsideration > tL.getSize() || idxInConsideration <= 0) {
                        throw new MarkOutException();
                    }
                    return "Marked, just for you~ " + tL.markItem(idxInConsideration - 1);
                case "unmark":
                    if (!isNum) {
                        return addToStruct(inp);
                    }
                    if (idxInConsideration > tL.getSize() || idxInConsideration <= 0) {
                        throw new MarkOutException();
                    }
                    return "Unmarked. I'll remember it anyway. " + tL.unmarkItem(idxInConsideration - 1);
                case "delete":
                    if (!isNum) {
                        return addToStruct(inp);
                    }
                    if (idxInConsideration > tL.getSize() || idxInConsideration <= 0) {
                        throw new MarkOutException();
                    }
                    return "...gone. " + tL.deleteItem(idxInConsideration - 1) + " But don't worry, I'm still here.";
                case "find":
                    return tL.findItems(restOfCmd);
                case "deadline":
                    return parseDeadline(restOfCmd);
                case "event":
                    return parseEvent(restOfCmd);
                case "todo":
                    return addToStruct(restOfCmd);
                default:
                    return addToStruct(inp);
            }
        } catch (MarkOutException e) {
            return e.getMessage();
        }
    }

    /**
     * Adds a plain (non-deadline, non-event) task to the task list.
     * Optionally parses a trailing "/at" marker to attach a place.
     *
     * @param inp the task description, optionally containing "/at" followed by a place.
     * @return the confirmation message, or an error message if the input is empty.
     */
    private String addToStruct(String inp) {
        try {
            inp = inp.trim();
            if (inp.isEmpty()) {
                throw new EmptyEventException();
            }
            String place = null;
            if (inp.contains("/at ")) {
                String[] parts = inp.split("/at ", 2);
                inp = parts[0].trim();
                place = parts[1].trim();
                if (inp.isEmpty()) {
                    throw new EmptyEventException();
                }
            }

            tL.addItem(new Task(inp, place));
            return "Of course, my love~ \"" + inp + "\" has been added, just as you wished.";
        } catch (EmptyEventException e) {
            return e.getMessage();
        }
    }

    /**
     * Parses a deadline command of the form task plus a "/by" date marker and adds it to the task list.
     *
     * @param inp the input following the "deadline" keyword.
     * @return the confirmation message, or an error message if parsing fails.
     */
    private String parseDeadline(String inp) {
        try {
            if (!inp.contains("/by ")) {
                return "You forgot the /by <date>... I'll wait as long as you need. Usage: deadline <task> /by <date>";
            }
            String[] parts = inp.split("/by ", 2);
            String objective = parts[0].trim();
            if (objective.isEmpty()) {
                throw new EmptyEventException();
            }
            String by = validate(parts[1].trim());
            tL.addItem(new Deadline(objective, by));
            return "I'll never let you forget: \"" + objective + "\" by " + by + ". I promise.";
        } catch (EmptyEventException | InvalidDateException e) {
            return e.getMessage();
        }
    }

    /**
     * Parses an event command of the form task plus "/from" and "/to" date markers and adds it to the task list.
     * The /from and /to markers may appear in either order.
     *
     * @param inp the input following the "event" keyword.
     * @return the confirmation message, or an error message if parsing fails.
     */
    private String parseEvent(String inp) {
        try {
            boolean f = inp.contains("/from ");
            boolean t = inp.contains("/to ");
            if (!f || !t) {
                return "Missing /from or /to... don't leave me guessing. Usage: event <task> /from <date> /to <date>";
            }
            int fIdx = inp.indexOf("/from ");
            int tIdx = inp.indexOf("/to ");
            String objective = inp.substring(0, Math.min(fIdx, tIdx)).trim();
            String from;
            String to;
            if (fIdx < tIdx) {
                from = validate(inp.substring(fIdx + 6, tIdx).trim());
                to = validate(inp.substring(tIdx + 4).trim());
            } else {
                from = validate(inp.substring(fIdx + 6).trim());
                to = validate(inp.substring(tIdx + 4, fIdx).trim());
            }
            if (objective.isEmpty()) {
                throw new EmptyEventException();
            }
            tL.addItem(new Event(objective, from, to));
            return "Marked in my heart and the list: \"" + objective + "\" (" + from + " to " + to + ").";
        } catch (EmptyEventException | InvalidDateException e) {
            return e.getMessage();
        }
    }

    /**
     * Validates and returns a non-empty, parseable date candidate string.
     *
     * @param candidate the raw date string to validate.
     * @return the validated date string.
     * @throws EmptyEventException if the candidate is empty.
     * @throws InvalidDateException if the candidate cannot be parsed as a date.
     */
    private String validate(String candidate) throws EmptyEventException, InvalidDateException {
        if (candidate.isEmpty()) {
            throw new EmptyEventException();
        }
        DateTimeParser.parse(candidate);
        return candidate;
    }
}
