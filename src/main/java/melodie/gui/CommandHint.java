package melodie.gui;

import java.util.Locale;
import java.util.StringJoiner;

/**
 * Determines which command fields have not yet been entered.
 */
final class CommandHint {
    private static final String DATE_FORMATS = "d/M/yyyy|today|tomorrow|weekday";
    private static final String DESCRIPTION_FIELD = "<description>";
    private static final String TASK_NUMBER_FIELD = "<task number>";
    private static final String KEYWORD_FIELD = "<keyword>";
    private static final String UPDATE_FIELD = "</description|/by|/from|/to>";
    private static final String UPDATE_VALUE_FIELD = "<new value>";

    private CommandHint() {
    }

    /**
     * Returns a concise hint containing only fields still missing from the input.
     *
     * @param input Current command input.
     * @return Missing fields, or an empty string when no fields are obviously missing.
     */
    static String getMissingFields(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return "";
        }

        String[] commandParts = trimmedInput.split("\\s+", 2);
        String commandWord = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length == 2 ? commandParts[1] : "";

        return switch (commandWord) {
            case "todo" -> arguments.isBlank() ? DESCRIPTION_FIELD : "";
            case "deadline" -> getDeadlineHint(arguments);
            case "event" -> getEventHint(arguments);
            case "mark", "unmark", "delete" -> arguments.isBlank() ? TASK_NUMBER_FIELD : "";
            case "find" -> arguments.isBlank() ? KEYWORD_FIELD : "";
            case "update" -> getUpdateHint(arguments);
            default -> "";
        };
    }

    private static String getDeadlineHint(String arguments) {
        int byIndex = arguments.indexOf("/by");
        if (byIndex < 0) {
            String descriptionHint = arguments.isBlank() ? DESCRIPTION_FIELD : "";
            return joinNonEmptyFields(descriptionHint, "/by " + getDateTimeField("due"));
        }

        String description = arguments.substring(0, byIndex).trim();
        String dueDateTime = arguments.substring(byIndex + "/by".length()).trim();
        return joinNonEmptyFields(
                description.isEmpty() ? DESCRIPTION_FIELD : "",
                getDateTimeHint(dueDateTime, "due"));
    }

    private static String getEventHint(String arguments) {
        int fromIndex = arguments.indexOf("/from");
        if (fromIndex < 0) {
            String descriptionHint = arguments.isBlank() ? DESCRIPTION_FIELD : "";
            return joinNonEmptyFields(
                    descriptionHint,
                    "/from " + getDateTimeField("start"),
                    "/to " + getDateTimeField("end"));
        }

        String description = arguments.substring(0, fromIndex).trim();
        String dateTimeArguments = arguments.substring(fromIndex + "/from".length()).trim();
        int toIndex = dateTimeArguments.indexOf("/to");
        if (toIndex < 0) {
            return joinNonEmptyFields(
                    description.isEmpty() ? DESCRIPTION_FIELD : "",
                    getDateTimeHint(dateTimeArguments, "start"),
                    "/to " + getDateTimeField("end"));
        }

        String startDateTime = dateTimeArguments.substring(0, toIndex).trim();
        String endDateTime = dateTimeArguments.substring(toIndex + "/to".length()).trim();
        return joinNonEmptyFields(
                description.isEmpty() ? DESCRIPTION_FIELD : "",
                getDateTimeHint(startDateTime, "start"),
                getDateTimeHint(endDateTime, "end"));
    }

    private static String getDateTimeHint(String dateTime, String fieldName) {
        if (dateTime.isEmpty()) {
            return getDateTimeField(fieldName);
        }
        if (dateTime.split("\\s+").length == 1) {
            return "<" + fieldName + " time HHmm>";
        }
        return "";
    }

    private static String getDateTimeField(String fieldName) {
        return "<" + fieldName + " date " + DATE_FORMATS + " HHmm>";
    }

    private static String getUpdateHint(String arguments) {
        if (arguments.isBlank()) {
            return joinNonEmptyFields(TASK_NUMBER_FIELD, UPDATE_FIELD, UPDATE_VALUE_FIELD);
        }

        String[] indexAndDetails = arguments.trim().split("\\s+", 2);
        if (indexAndDetails.length == 1) {
            return joinNonEmptyFields(UPDATE_FIELD, UPDATE_VALUE_FIELD);
        }

        String[] fieldAndValue = indexAndDetails[1].trim().split("\\s+", 2);
        return fieldAndValue.length == 1 ? UPDATE_VALUE_FIELD : "";
    }

    /**
     * Joins a variable number of non-empty field hints, separated by spaces.
     *
     * @param fields Field hints to join.
     * @return Joined non-empty field hints.
     */
    private static String joinNonEmptyFields(String... fields) {
        StringJoiner hint = new StringJoiner(" ");
        for (String field : fields) {
            if (!field.isEmpty()) {
                hint.add(field);
            }
        }
        return hint.toString();
    }
}
