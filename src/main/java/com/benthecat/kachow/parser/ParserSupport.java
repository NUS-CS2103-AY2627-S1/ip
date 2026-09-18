package com.benthecat.kachow.parser;

import java.time.format.DateTimeParseException;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.task.Task;

/** Provides shared token validation and error guidance for command parsers. */
final class ParserSupport {
    static final String USAGE_DEADLINE = "deadline DESCRIPTION /by DATE_OR_TIME";
    static final String USAGE_EVENT = "event DESCRIPTION /from START /to END";
    static final String USAGE_EDIT = "edit TASK_NUMBER FIELD VALUE [FIELD VALUE]...";
    static final String USAGE_FIND = "find KEYWORD";
    static final String USAGE_ON = "on DATE";
    private static final String DATE_FORMATS_TEXT =
            "yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US)";
    static final String DATE_FORMAT_GUIDANCE = "Use " + DATE_FORMATS_TEXT + ".";
    static final String DATE_TIME_FORMAT_GUIDANCE =
            "Use " + DATE_FORMATS_TEXT + ", optionally followed by HHmm, HH:mm, or an AM/PM time.";

    private ParserSupport() {
        // Prevent instantiation of this stateless utility class.
    }

    /** Validates descriptions before constructing a task and explains unsupported field markers. */
    static String validateDescription(String description) throws KachowException {
        try {
            String normalizedDescription = Task.normalizeDescription(description);
            if (findNextEditFieldIndex(normalizedDescription, 0) != -1) {
                throw new KachowException(
                        "Descriptions cannot contain slash-prefixed fields. Check the command's parameters.");
            }
            return normalizedDescription;
        } catch (IllegalArgumentException exception) {
            throw new KachowException(exception.getMessage(), exception);
        }
    }

    /** Creates guidance for an edit command that has no field. */
    static KachowException createMissingEditFieldException() {
        return new KachowException(
                "Tell me what to change. Use /description, /by, /from, or /to: " + USAGE_EDIT);
    }

    /** Creates consistent guidance for malformed task numbers. */
    static KachowException createInvalidTaskNumberException(Command action, NumberFormatException cause) {
        String message = "That racer number isn't a whole positive number. Use: "
                + action.getKeyword() + " TASK_NUMBER";
        return cause == null ? new KachowException(message) : new KachowException(message, cause);
    }

    /** Creates consistent, parameter-specific guidance for an invalid event date/time. */
    static KachowException createInvalidEventDateTimeException(String parameter, DateTimeParseException cause) {
        return new KachowException(
                "That event " + parameter + " date or time is invalid. " + DATE_TIME_FORMAT_GUIDANCE,
                cause);
    }

    /** Finds the first whitespace character so repeated spaces and tabs are accepted. */
    static int findFirstWhitespaceIndex(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Skips whitespace at and after an index. */
    static int skipWhitespace(String text, int startIndex) {
        int index = startIndex;
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
        return index;
    }

    /** Finds the next slash-prefixed edit field that begins after whitespace. */
    static int findNextEditFieldIndex(String text, int startIndex) {
        for (int i = startIndex; i < text.length(); i++) {
            if (text.charAt(i) == '/' && (i == 0 || Character.isWhitespace(text.charAt(i - 1)))) {
                return i;
            }
        }
        return -1;
    }

    /** Locates a slash-prefixed syntax token when it appears as a complete word. */
    static int findToken(String text, String token, int startIndex) {
        int tokenIndex = text.indexOf(token, startIndex);
        while (tokenIndex != -1) {
            int tokenEnd = tokenIndex + token.length();
            boolean isWordStart = tokenIndex == 0 || Character.isWhitespace(text.charAt(tokenIndex - 1));
            boolean isWordEnd = tokenEnd == text.length() || Character.isWhitespace(text.charAt(tokenEnd));
            if (isWordStart && isWordEnd) {
                return tokenIndex;
            }
            tokenIndex = text.indexOf(token, tokenIndex + 1);
        }
        return -1;
    }
}
