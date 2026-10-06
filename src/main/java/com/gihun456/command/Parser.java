package com.gihun456.command;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;
import com.gihun456.model.Deadline;
import com.gihun456.model.Event;

/**
 * Parses raw user input into an operation and its arguments.
 */
public class Parser {
    private static final Integer FROM_LENGTH = 5;
    private static final Integer BY_TO_LENGTH = 3;

    /**
     * Represents one parsed user command.
     */
    public static class ParsedInput {
        private final Operation operation;
        private final String arguments;

        /**
         * Creates a parsed input value.
         *
         * @param operation Parsed operation.
         * @param arguments Operation arguments.
         */
        public ParsedInput(Operation operation, String arguments) {
            assert operation != null : "Parsed input must have an operation";
            assert arguments != null : "Parsed input must have non-null arguments";
            this.operation = operation;
            this.arguments = arguments;
        }

        public Operation getOperation() {
            return operation;
        }

        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Converts a raw command line into a command and its argument text.
     *
     * @param input Raw input from the user.
     * @return Parsed input containing the operation and remaining text.
     * @throws GihunException If the command is empty or invalid.
     */
    public ParsedInput parse(String input) throws GihunException {
        if (input == null || input.trim().isEmpty()) {
            throw new GihunException(ErrorMessages.EMPTY_COMMAND);
        }

        String trimmed = input.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0];
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        Operation operation = Operation.fromInput(command);
        return new ParsedInput(operation, arguments);
    }

    /**
     * Parses a deadline command argument block.
     */
    public Deadline parseDeadline(String arguments) throws GihunException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new GihunException(ErrorMessages.DEADLINE_DESCRIPTION_EMPTY);
        }

        int byIndex = arguments.indexOf("/by");
        if (byIndex < 0) {
            throw new GihunException(ErrorMessages.DEADLINE_BY_MISSING);
        }

        String taskName = arguments.substring(0, byIndex).trim();
        if (taskName.isEmpty()) {
            throw new GihunException(ErrorMessages.DEADLINE_DESCRIPTION_EMPTY);
        }

        String dueDate = arguments.substring(byIndex + BY_TO_LENGTH).trim();
        if (dueDate.isEmpty()) {
            throw new GihunException(ErrorMessages.DEADLINE_DUE_DATE_EMPTY);
        }

        return new Deadline(taskName, dueDate);
    }

    /**
     * Parses an event command argument block.
     */
    public Event parseEvent(String arguments) throws GihunException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new GihunException(ErrorMessages.EVENT_DESCRIPTION_EMPTY);
        }

        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");

        if (fromIndex < 0 || toIndex < 0) {
            throw new GihunException(ErrorMessages.EVENT_FROM_TO_MISSING);
        }

        if (toIndex < fromIndex) {
            throw new GihunException(ErrorMessages.EVENT_FROM_BEFORE_TO);
        }

        String taskName = arguments.substring(0, fromIndex).trim();
        if (taskName.isEmpty()) {
            throw new GihunException(ErrorMessages.EVENT_DESCRIPTION_EMPTY);
        }

        String startDate = arguments.substring(fromIndex + FROM_LENGTH, toIndex).trim();
        String endDate = arguments.substring(toIndex + BY_TO_LENGTH).trim();

        if (startDate.isEmpty()) {
            throw new GihunException(ErrorMessages.EVENT_START_DATE_EMPTY);
        }
        if (endDate.isEmpty()) {
            throw new GihunException(ErrorMessages.EVENT_END_DATE_EMPTY);
        }

        return new Event(taskName, startDate, endDate);
    }
}
