package duchess.tasks.factories;

import java.time.LocalDate;

import duchess.tasks.Deadline;
import duchess.tasks.exceptions.TaskCreationException;

/** Creates {@link Deadline} tasks from user commands. */
class DeadlineFactory {
    private static final String TASK_TYPE = "deadline";
    private static final String BY_MARKER = "/by";
    private static final String CORRECT_FORMAT =
            "deadline DESCRIPTION /by yyyy-MM-dd";
    private static final String EXAMPLE_COMMAND =
            "deadline clean the house /by 2026-12-10";

    private DeadlineFactory() {
        // Utility class.
    }

    /**
     * Creates a deadline task from a command.
     *
     * @throws TaskCreationException if required information or a valid date is missing
     */
    static Deadline create(String command) throws TaskCreationException {
        DeadlineDetails details = parseDetails(command);
        validateDetails(details);

        LocalDate byDate = TaskFactory.parseDate(details.getByString());
        return new Deadline(details.getDescription(), byDate);
    }

    private static DeadlineDetails parseDetails(String command)
            throws TaskCreationException {
        int byIndex = TaskFactory.findMarker(command, BY_MARKER);
        if (byIndex == -1) {
            DeadlineFactory.declareMissingField(BY_MARKER);
        }

        return new DeadlineDetails(
                TaskFactory.extractCommandSection(command, TASK_TYPE.length(), byIndex),
                TaskFactory.extractCommandSection(
                        command, byIndex + BY_MARKER.length(), command.length()));
    }

    private static void validateDetails(DeadlineDetails details)
            throws TaskCreationException {
        if (details.getDescription().isEmpty()) {
            throw TaskCreationException.declareEmptyDescription(TASK_TYPE);
        }
        if (details.getByString().isEmpty()) {
            DeadlineFactory.declareMissingField(BY_MARKER);
        }
    }

    private static void declareMissingField(String fieldName)
            throws TaskCreationException {
        throw TaskCreationException.declareMissingField(
                TASK_TYPE, fieldName, CORRECT_FORMAT, EXAMPLE_COMMAND);
    }

    /** Holds the textual fields extracted from a deadline command. */
    private static class DeadlineDetails {
        private final String description;
        private final String byString;

        DeadlineDetails(String description, String byString) {
            this.description = description;
            this.byString = byString;
        }

        String getDescription() {
            return description;
        }

        String getByString() {
            return byString;
        }
    }
}
