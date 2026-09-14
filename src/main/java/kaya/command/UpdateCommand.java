package kaya.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Task;
import kaya.task.Todo;

/**
 * Holds a parsed single-field update and creates a validated replacement task.
 *
 * @param index the zero-based task index
 * @param field the field to change
 * @param value the new field value
 */
public record UpdateCommand(int index, String field, String value) {
    /**
     * Creates an updated task while preserving its type and completion status.
     * The original task is untouched even when validation fails.
     *
     * @param original the task to update
     * @return a validated replacement task
     * @throws KayaException if the field or value is incompatible with the task
     */
    public Task applyTo(Task original) throws KayaException {
        Task replacement;
        if (field.equals("/description")) {
            replacement = withDescription(original);
        } else if (original instanceof Deadline deadline && field.equals("/by")) {
            replacement = new Deadline(original.getDescription(), parseDate());
        } else if (original instanceof Event event && (field.equals("/from") || field.equals("/to"))) {
            LocalDate from = field.equals("/from") ? parseDate() : event.getFrom();
            LocalDate to = field.equals("/to") ? parseDate() : event.getTo();
            if (to.isBefore(from)) {
                throw new KayaException("An event's ending date cannot be before its starting date.");
            }
            replacement = new Event(original.getDescription(), from, to);
        } else {
            throw new KayaException("That field cannot be updated for this task type.");
        }
        if (original.isDone()) {
            replacement.markAsDone();
        }
        return replacement;
    }

    /**
     * Copies the task with a new description and its original dates.
     *
     * @param original the task to copy
     * @return the task with its new description
     * @throws KayaException if the description is empty or the task type is unsupported
     */
    private Task withDescription(Task original) throws KayaException {
        if (value.isBlank()) {
            throw new KayaException("An updated description must not be empty.");
        }
        if (original instanceof Deadline deadline) {
            return new Deadline(value, deadline.getBy());
        }
        if (original instanceof Event event) {
            return new Event(value, event.getFrom(), event.getTo());
        }
        if (original instanceof Todo) {
            return new Todo(value);
        }
        throw new KayaException("This task type does not support updates.");
    }

    /**
     * Parses the replacement date using the existing ISO date format.
     *
     * @return the replacement date
     * @throws KayaException if the value is not a valid ISO date
     */
    private LocalDate parseDate() throws KayaException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new KayaException("Use dates in yyyy-MM-dd format, for example 2019-10-15.");
        }
    }
}
