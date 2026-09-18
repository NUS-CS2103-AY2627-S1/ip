package com.benthecat.kachow.parser;

import static com.benthecat.kachow.parser.ParserSupport.DATE_TIME_FORMAT_GUIDANCE;
import static com.benthecat.kachow.parser.ParserSupport.createInvalidEventDateTimeException;
import static com.benthecat.kachow.parser.ParserSupport.validateDescription;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.Parser.EditCommand;
import com.benthecat.kachow.parser.Parser.EditField;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.Todo;

/** Applies validated field replacements while preserving other task details. */
final class TaskEditor {
    /**
     * Creates an updated copy of a task according to one parsed edit request.
     *
     * @param task Existing task to update.
     * @param editCommand Parsed edit request.
     * @return Updated task with its unedited details and completion status preserved.
     * @throws KachowException If the selected field does not apply or its new value is invalid.
     */
    public Task applyEdit(Task task, EditCommand editCommand) throws KachowException {
        assert task != null : "Edited task must not be null";
        assert editCommand != null : "Edit command must not be null";

        String description = validateDescription(editCommand.changes()
                .getOrDefault(EditField.DESCRIPTION, task.getDescription()));
        return switch (task) {
            case Todo todo -> editTodo(todo, description, editCommand);
            case Deadline deadline -> editDeadline(deadline, description, editCommand);
            case Event event -> editEvent(event, description, editCommand);
            default -> throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
        };
    }

    /** Creates an edited todo after ensuring that every requested field is supported. */
    private Task editTodo(Todo todo, String description, EditCommand editCommand) throws KachowException {
        requireSupportedFields(editCommand, EditField.DESCRIPTION);
        return new Todo(description, todo.isDone());
    }

    /** Creates an edited deadline after ensuring that every requested field is supported. */
    private Task editDeadline(Deadline deadline, String description, EditCommand editCommand)
            throws KachowException {
        requireSupportedFields(editCommand, EditField.DESCRIPTION, EditField.BY);

        DateTimeParser.ParsedDateTime by = deadline.getDueDateTime();
        if (editCommand.changes().containsKey(EditField.BY)) {
            by = parseEditedDeadlineDateTime(editCommand.changes().get(EditField.BY), by.date());
        }
        return new Deadline(description, by, deadline.isDone());
    }

    /** Parses an edited deadline value, allowing a time that keeps the existing date. */
    private DateTimeParser.ParsedDateTime parseEditedDeadlineDateTime(String value, LocalDate existingDate)
            throws KachowException {
        try {
            return DateTimeParser.parse(value, existingDate);
        } catch (DateTimeParseException exception) {
            throw new KachowException(
                    "That deadline date or time is invalid. " + DATE_TIME_FORMAT_GUIDANCE,
                    exception);
        }
    }

    /** Creates an edited event after parsing all requested values and validating the final range. */
    private Task editEvent(Event event, String description, EditCommand editCommand) throws KachowException {
        requireSupportedFields(editCommand, EditField.DESCRIPTION, EditField.FROM, EditField.TO);

        DateTimeParser.ParsedDateTime from = event.getFrom();
        if (editCommand.changes().containsKey(EditField.FROM)) {
            from = parseEditedEventDateTime(
                    editCommand.changes().get(EditField.FROM), event.getFrom().date(), "start");
        }
        DateTimeParser.ParsedDateTime to = event.getTo();
        if (editCommand.changes().containsKey(EditField.TO)) {
            to = parseEditedEventDateTime(
                    editCommand.changes().get(EditField.TO), event.getTo().date(), "end");
        }

        try {
            return new Event(description, from, to, event.isDone());
        } catch (IllegalArgumentException exception) {
            throw new KachowException(
                    "That event must end after it starts. Use a full date when moving it across midnight.",
                    exception);
        }
    }

    /** Parses an edited event value, allowing a time that keeps the existing field's date. */
    private DateTimeParser.ParsedDateTime parseEditedEventDateTime(String value, LocalDate existingDate,
            String parameter) throws KachowException {
        try {
            return DateTimeParser.parse(value, existingDate);
        } catch (DateTimeParseException exception) {
            throw createInvalidEventDateTimeException(parameter, exception);
        }
    }

    /** Rejects the first requested field that is unsupported by the selected task type. */
    private void requireSupportedFields(EditCommand editCommand, EditField... supportedFields)
            throws KachowException {
        for (EditField field : EditField.values()) {
            if (editCommand.changes().containsKey(field) && !isSupportedField(field, supportedFields)) {
                throw new KachowException("This racer does not have a " + field.getKeyword() + " detail.");
            }
        }
    }

    /** Reports whether a field is in the selected task type's supported field list. */
    private boolean isSupportedField(EditField field, EditField[] supportedFields) {
        for (EditField supportedField : supportedFields) {
            if (field == supportedField) {
                return true;
            }
        }
        return false;
    }

}
