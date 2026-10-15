package milo.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import milo.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.ToDo;

/** Validates replacement dates without mutating the original task. */
public final class RescheduleParser {
    private RescheduleParser() {
    }

    /** Creates a replacement dated task, retaining omitted event endpoints. */
    public static Task parse(Task original, String input) throws MiloException {
        if (original instanceof ToDo) {
            throw new MiloException("Hmmm... How does one reschedule a task without a date?");
        }
        String[] parts = splitDateArguments(input);
        LocalDateTime by = null;
        LocalDateTime from = null;
        LocalDateTime to = null;
        for (int i = 0; i < parts.length; i += 2) {
            String flag = parts[i];
            validateFlag(parts, i);
            LocalDateTime date = parseDate(parts[i + 1]);
            if (flag.equals("/by")) {
                by = date;
            } else if (flag.equals("/from")) {
                from = date;
            } else {
                to = date;
            }
        }
        return createRescheduledTask(original, by, from, to);
    }

    /** Rejects missing, unknown, or repeated flags before their date values are parsed. */
    private static void validateFlag(String[] parts, int index) throws MiloException {
        String flag = parts[index];
        if (!flag.startsWith("/")) {
            throw new MiloException("Let's untangle those dates! Use /by for deadlines, or /from and /to for events.");
        }
        if (!flag.equals("/by") && !flag.equals("/from") && !flag.equals("/to")) {
            throw new MiloException("Let's untangle those dates! Use /by for deadlines, or /from and /to for events.");
        }
        for (int i = 0; i < index; i += 2) {
            if (parts[i].equals(flag)) {
                throw new MiloException("That doesn't make sense... No duplicate flags!");
            }
        }
    }

    /** Converts a date value and translates parsing failures into a command error. */
    private static LocalDateTime parseDate(String input) throws MiloException {
        try {
            return DateTimeParser.parse(input);
        } catch (DateTimeParseException e) {
            throw Parser.invalidDateMessage();
        }
    }

    /** Checks task-specific flags and builds a replacement, retaining omitted event endpoints. */
    private static Task createRescheduledTask(Task original, LocalDateTime by,
            LocalDateTime from, LocalDateTime to) throws MiloException {
        if (original instanceof Deadline) {
            if (by == null || from != null || to != null) {
                throw new MiloException("Let's untangle those dates! Use /by for deadlines, "
                        + "or /from and /to for events.");
            }
            return new Deadline(original.getDescription(), by);
        }
        if (by != null) {
            throw new MiloException("Let's untangle those dates! Use /by for deadlines, or /from and /to for events.");
        }
        Event event = (Event) original;
        return new Event(event.getDescription(),
                from == null ? event.getStartDate() : from,
                to == null ? event.getEndDate() : to);
    }

    /** Splits flags and their values into alternating array entries, preserving spaces within dates. */
    private static String[] splitDateArguments(String input) {
        ArrayList<String> parts = new ArrayList<>();
        // Split before flag names, leaving slashes inside day/month/year dates intact.
        String[] arguments = input.trim().split("(?=/[a-zA-Z]+)");
        for (String argument : arguments) {
            String[] pair = argument.trim().split(" ", 2);
            parts.add(pair[0]);
            parts.add(pair.length == 2 ? pair[1] : "");
        }
        return parts.toArray(new String[0]);
    }
}
