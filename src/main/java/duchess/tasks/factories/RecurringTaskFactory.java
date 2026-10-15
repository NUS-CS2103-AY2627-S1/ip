package duchess.tasks.factories;

import java.time.LocalDate;
import java.time.Period;

import duchess.tasks.RecurringTask;
import duchess.tasks.exceptions.TaskCreationException;

/** Creates {@link RecurringTask} tasks from user commands. */
class RecurringTaskFactory {
    private static final String TASK_TYPE = "recurring";
    private static final String BY_MARKER = "/by";
    private static final String REPEAT_MARKER = "/repeat";
    private static final String CORRECT_FORMAT =
            "recurring DESCRIPTION /by yyyy-MM-dd /repeat N days";
    private static final String EXAMPLE_COMMAND =
            "recurring exercise /by 2026-12-10 /repeat 2 days";

    private RecurringTaskFactory() {
        // Utility class.
    }

    /**
     * Creates a recurring task from a command.
     *
     * @throws TaskCreationException if required information, a valid date, or a valid
     *                       recurrence is missing
     */
    static RecurringTask create(String command) throws TaskCreationException {
        RecurringTaskDetails details = parseDetails(command);
        validateDetails(details);

        LocalDate byDate = TaskFactory.parseDate(details.getByString());
        Period recurrence = parseRecurrence(details.getRepeatString());

        return new RecurringTask(details.getDescription(), byDate, recurrence);
    }

    private static RecurringTaskDetails parseDetails(String command)
            throws TaskCreationException {
        int byIndex = TaskFactory.findMarker(command, BY_MARKER);
        if (byIndex == -1) {
            RecurringTaskFactory.declareMissingField(BY_MARKER);
        }

        int repeatIndex = TaskFactory.findMarker(command, REPEAT_MARKER);
        if (repeatIndex == -1) {
            RecurringTaskFactory.declareMissingField(REPEAT_MARKER);
        }

        return byIndex < repeatIndex
                ? parseNormalMarkerOrder(command, byIndex, repeatIndex)
                : parseReversedMarkerOrder(command, byIndex, repeatIndex);
    }

    private static RecurringTaskDetails parseNormalMarkerOrder(
            String command, int byIndex, int repeatIndex) {
        return new RecurringTaskDetails(
                TaskFactory.extractCommandSection(command, TASK_TYPE.length(), byIndex),
                TaskFactory.extractCommandSection(
                        command, byIndex + BY_MARKER.length(), repeatIndex),
                TaskFactory.extractCommandSection(
                        command, repeatIndex + REPEAT_MARKER.length(), command.length()));
    }

    private static RecurringTaskDetails parseReversedMarkerOrder(
            String command, int byIndex, int repeatIndex) {
        return new RecurringTaskDetails(
                TaskFactory.extractCommandSection(command, TASK_TYPE.length(), repeatIndex),
                TaskFactory.extractCommandSection(
                        command, byIndex + BY_MARKER.length(), command.length()),
                TaskFactory.extractCommandSection(
                        command, repeatIndex + REPEAT_MARKER.length(), byIndex));
    }

    private static void validateDetails(RecurringTaskDetails details)
            throws TaskCreationException {
        if (details.getDescription().isEmpty()) {
            throw TaskCreationException.declareEmptyDescription(TASK_TYPE);
        }
        if (details.getByString().isEmpty()) {
            RecurringTaskFactory.declareMissingField(BY_MARKER);
        }
        if (details.getRepeatString().isEmpty()) {
            RecurringTaskFactory.declareMissingField(REPEAT_MARKER);
        }
    }

    private static void declareMissingField(String fieldName)
            throws TaskCreationException {
        throw TaskCreationException.declareMissingField(
                TASK_TYPE, fieldName, CORRECT_FORMAT, EXAMPLE_COMMAND);
    }

    private static Period parseRecurrence(String recurrence) throws TaskCreationException {
        String[] components = recurrence.trim().split("\\s+");
        boolean hasExpectedComponents = components.length == 2;
        boolean hasDayUnit = hasExpectedComponents
                && (components[1].equals("day") || components[1].equals("days"));
        if (!hasDayUnit) {
            throw TaskCreationException.declareInvalidRecurrenceFormat(
                    recurrence, CORRECT_FORMAT, EXAMPLE_COMMAND);
        }

        int days;
        try {
            days = Integer.parseInt(components[0]);
        } catch (NumberFormatException e) {
            throw TaskCreationException.declareInvalidRecurrenceFormat(
                    recurrence, CORRECT_FORMAT, EXAMPLE_COMMAND);
        }
        if (days <= 0) {
            throw TaskCreationException.declareInvalidRecurrenceFormat(
                    recurrence, CORRECT_FORMAT, EXAMPLE_COMMAND);
        }
        return Period.ofDays(days);
    }

    /** Holds the textual fields extracted from a recurring task command. */
    private static class RecurringTaskDetails {
        private final String description;
        private final String byString;
        private final String repeatString;

        RecurringTaskDetails(String description, String byString, String repeatString) {
            this.description = description;
            this.byString = byString;
            this.repeatString = repeatString;
        }

        String getDescription() {
            return description;
        }

        String getByString() {
            return byString;
        }

        String getRepeatString() {
            return repeatString;
        }
    }
}
