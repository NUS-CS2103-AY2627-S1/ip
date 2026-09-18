package com.fireinyu.themyth.requests;

import com.fireinyu.themyth.exceptions.ArgumentFormatException;
import com.fireinyu.themyth.tasks.TaskOrder;
import com.fireinyu.themyth.util.MythDateTime;

/**
 * Converts request fields into typed values.
 */
public abstract class InputFieldParser<T> {
    /** Parser for string fields. */
    public static final InputFieldParser<String> STRING = new InputFieldParser<>("String", "e.g. pancake") {
        /**
         * Returns the input field unchanged.
         */
        @Override
        public String tryParse(String field) {
            return field;
        }
    };
    /** Parser for date-time fields. */
    public static final InputFieldParser<MythDateTime> DATETIME = new InputFieldParser<>("Datetime", "YYYY-MM-DD") {
        /**
         * Parses the input field as a date-time.
         */
        @Override
        public MythDateTime tryParse(String field) {
            return MythDateTime.parse(field);
        }
    };
    /** Parser for integer fields. */
    public static final InputFieldParser<Integer> INT = new InputFieldParser<>("Integer", "e.g. 67") {
        /**
         * Parses the input field as an integer.
         */
        @Override
        public Integer tryParse(String field) throws ArgumentFormatException {
            return Integer.parseInt(field);
        }
    };
    /** Parser for task-order fields. */
    public static final InputFieldParser<TaskOrder> ORDER = new InputFieldParser<>("Order", "e.g. created, modified") {
        /**
         * Parses the input field as a task ordering.
         */
        @Override
        public TaskOrder tryParse(String field) throws ArgumentFormatException {
            return TaskOrder.valueOf(field.toUpperCase());
        }
    };

    private final String type;
    private final String format;

    private InputFieldParser(String type, String format) {
        this.type = type;
        this.format = format;
    }

    abstract T tryParse(String field);
    /**
     * Parses a field and converts parsing failures into an argument-format exception.
     *
     * @param field input field to parse.
     * @return the parsed value.
     * @throws ArgumentFormatException if the field has an invalid format.
     */
    public T parse(String field) throws ArgumentFormatException {
        try {
            return tryParse(field);
        } catch (RuntimeException e) {
            throw new ArgumentFormatException(type, field, format);
        }
    }
}
