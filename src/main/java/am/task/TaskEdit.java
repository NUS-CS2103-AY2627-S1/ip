package am.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Map;
import java.util.Set;

import am.command.InvalidCommandException;

/** Holds supplied edit fields and builds a validated replacement without mutation. */
public class TaskEdit {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private final Map<String, String> fields;

    /** Copies the parsed field values so a command cannot change after parsing. */
    public TaskEdit(Map<String, String> fields) {
        this.fields = Map.copyOf(fields);
    }

    /** Creates an edited task, retaining its type and completion state. */
    public Task applyTo(Task original) {
        Set<String> allowed = original instanceof EventTask ? Set.of("/name", "/from", "/to")
                : original instanceof DeadlineTask ? Set.of("/name", "/by") : Set.of("/name");
        if (fields.isEmpty() || !allowed.containsAll(fields.keySet())) {
            throw InvalidCommandException.forSyntax("edit");
        }
        String name = fields.getOrDefault("/name", original.getName());
        if (name.isBlank() || name.contains("|") || name.contains("\r") || name.contains("\n")) {
            throw InvalidCommandException.forSyntax("edit");
        }

        Task replacement;
        try {
            if (original instanceof EventTask event) {
                LocalDateTime from = resolve("/from", event.getFrom(), LocalTime.MIDNIGHT);
                LocalDateTime to = resolve("/to", event.getTo(), LocalTime.of(23, 59));
                if (to.isBefore(from)) {
                    throw InvalidCommandException.forSyntax("edit event");
                }
                replacement = new EventTask(name, from, to);
            } else if (original instanceof DeadlineTask deadline) {
                replacement = new DeadlineTask(name, resolve("/by", deadline.getBy(), LocalTime.of(23, 59)));
            } else {
                replacement = new TodoTask(name);
            }
        } catch (DateTimeException exception) {
            throw InvalidCommandException.forSyntax(original instanceof EventTask ? "edit event" : "edit deadline");
        }
        if (original.isDone()) {
            replacement.mark();
        }
        return replacement;
    }

    /** Resolves a supplied value against its own endpoint's date. */
    private LocalDateTime resolve(String field, LocalDateTime original, LocalTime defaultTime) {
        if (!fields.containsKey(field)) {
            return original;
        }
        String value = fields.get(field).replaceAll("\\s+", " ");
        if (value.matches("[0-9]{4}")) {
            return original.toLocalDate().atTime(LocalTime.parse(value, TIME));
        }
        if (value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return LocalDate.parse(value, DATE).atTime(defaultTime);
        }
        return LocalDateTime.parse(value, DATE_TIME);
    }
}
