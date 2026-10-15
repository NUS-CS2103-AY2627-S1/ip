package milo.parser;

import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import milo.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.ToDo;

/** Converts user-entered task commands into task objects. */
public final class Parser {
    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^deadline\\s+(.+?)\\s*/by\\s+(.+)$");
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("^event\\s+(.+?)\\s*/from\\s+(.+?)\\s*/to\\s+(.+)$");

    /** Prevents construction of this utility class. */
    private Parser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a todo, deadline, or event command.
     *
     * @param s complete user command
     * @return the task represented by the command
     * @throws MiloException if the command is empty, malformed, or has invalid dates
     */
    public static Task parseTask(String s) throws MiloException {
        String example = "Try: todo read book";
        try {
            if (s.startsWith("todo")) {
                return parseTodo(s);
            }
            if (s.startsWith("deadline")) {
                example = "Try: deadline return book /by 2026-10-15 1800";
                return parseDeadline(s);
            }
            if (s.startsWith("event")) {
                example = "Try: event study /from 2026-10-15 1400 /to 2026-10-15 1600";
                return parseEvent(s);
            }
            throw new MiloException("Erm... I don't know what you mean...");
        } catch (MiloException e) {
            throw new MiloException(e.getMessage(), example);
        }
    }

    /** Parses a todo command into a task. */
    private static Task parseTodo(String s) throws MiloException {
        if (s.equals("todo")) {
            throw new MiloException("An empty todo? What is that for? Doomscrolling?!?");
        }

        String taskDescription = s.substring(4).trim();
        if (taskDescription.isEmpty()) {
            throw new MiloException("An empty todo? What is that for? Doomscrolling?!?");
        }
        return new ToDo(taskDescription);
    }

    /** Parses a deadline command into a task. */
    private static Task parseDeadline(String s) throws MiloException {
        if (s.equals("deadline")) {
            throw new MiloException("An empty deadline? What is that for? Doomscrolling?!?");
        }

        Matcher matcher = DEADLINE_PATTERN.matcher(s);
        if (!matcher.matches()) {
            String remainder = s.substring(8).trim();
            if (remainder.isEmpty()) {
                throw new MiloException("An empty deadline? What is that for? Doomscrolling?!?");
            }
            if (!remainder.contains("/")) {
                throw new MiloException("A deadline without a deadline isn't really a deadline is it...");
            }
            if (!remainder.contains("/by")) {
                throw new MiloException(
                        "Let's get that deadline sorted! Use deadline description /by yyyy-MM-dd HHmm.");
            }
            throw invalidDateMessage();
        }

        String taskDescription = matcher.group(1).trim();
        String date = matcher.group(2).trim();
        if (taskDescription.isEmpty()) {
            throw new MiloException("An empty deadline? What is that for? Doomscrolling?!?");
        }
        try {
            return new Deadline(taskDescription, date);
        } catch (DateTimeParseException e) {
            throw invalidDateMessage();
        }
    }

    /** Parses an event command into a task. */
    private static Task parseEvent(String s) throws MiloException {
        if (s.equals("event")) {
            throw new MiloException("An empty event? What is that for? Doomscrolling?!?");
        }

        Matcher matcher = EVENT_PATTERN.matcher(s);
        if (!matcher.matches()) {
            String remainder = s.substring(5).trim();
            if (remainder.isEmpty()) {
                throw new MiloException("An empty event? What is that for? Doomscrolling?!?");
            }
            if (!remainder.contains("/from") || !remainder.contains("/to")) {
                throw new MiloException("Erm... An event has to start and end...");
            }
            throw new MiloException(
                    "Let's get that event sorted! Use event description /from yyyy-MM-dd HHmm "
                            + "/to yyyy-MM-dd HHmm.");
        }

        String taskDescription = matcher.group(1).trim();
        String startDate = matcher.group(2).trim();
        String endDate = matcher.group(3).trim();
        if (taskDescription.isEmpty()) {
            throw new MiloException("An empty event? What is that for? Doomscrolling?!?");
        }
        try {
            return new Event(taskDescription, startDate, endDate);
        } catch (DateTimeParseException e) {
            throw invalidDateMessage();
        }
    }

    /** Creates the standard message shown when a task date cannot be parsed. */
    protected static MiloException invalidDateMessage() {
        return new MiloException(
                "I couldn't understand that date. Use yyyy-MM-dd or yyyy-MM-dd HHmm "
                        + "(for example, 2019-10-15 1800), or d/M/yyyy HHmm.");
    }
}
