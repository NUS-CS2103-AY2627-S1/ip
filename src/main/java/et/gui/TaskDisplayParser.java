package et.gui;

import et.task.TaskType;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts task details from ET response lines for graphical rendering.
 */
final class TaskDisplayParser {
    /** Matches an optional list number followed by ET's task type, status, and description. */
    private static final Pattern TASK_LINE_PATTERN = Pattern.compile(
            "^\\s*(?:(\\d+)\\.)?\\[([TDE])\\]\\[([ X✓])\\]\\s+(.+)$");

    /** Separates a deadline description from its due date or time. */
    private static final Pattern DEADLINE_DETAILS_PATTERN = Pattern.compile("^(.+) \\(by: (.+)\\)$");

    /** Separates an event description from its start and end dates or times. */
    private static final Pattern EVENT_DETAILS_PATTERN = Pattern.compile("^(.+) \\(from: (.+) to: (.+)\\)$");

    /** Prevents instantiation of this utility class. */
    private TaskDisplayParser() {
    }

    /**
     * Parses one response line when it contains a task.
     *
     * @param line one line from ET's response
     * @return the task display details, or an empty value for ordinary message text
     */
    static Optional<TaskDisplay> parseTaskLine(String line) {
        Matcher matcher = TASK_LINE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String taskNumber = matcher.group(1) == null ? "" : matcher.group(1);
        TaskType taskType = TaskType.fromDisplayCode(matcher.group(2));
        boolean isDone = !matcher.group(3).isBlank();
        TaskDetails taskDetails = parseTaskDetails(taskType, matcher.group(4));
        return Optional.of(new TaskDisplay(taskNumber, taskType, isDone,
                taskDetails.description(), taskDetails.scheduleDetails()));
    }

    /**
     * Separates a task's description from any schedule details added for display.
     *
     * @param taskType the parsed category of task
     * @param displayedDescription the description and optional schedule suffix
     * @return the independently renderable task details
     */
    private static TaskDetails parseTaskDetails(TaskType taskType, String displayedDescription) {
        if (taskType == TaskType.DEADLINE) {
            Matcher deadlineMatcher = DEADLINE_DETAILS_PATTERN.matcher(displayedDescription);
            if (deadlineMatcher.matches()) {
                return new TaskDetails(deadlineMatcher.group(1),
                        List.of(new ScheduleDetail("DUE", deadlineMatcher.group(2))));
            }
        } else if (taskType == TaskType.EVENT) {
            Matcher eventMatcher = EVENT_DETAILS_PATTERN.matcher(displayedDescription);
            if (eventMatcher.matches()) {
                return new TaskDetails(eventMatcher.group(1), List.of(
                        new ScheduleDetail("FROM", eventMatcher.group(2)),
                        new ScheduleDetail("TO", eventMatcher.group(3))));
            }
        }

        return new TaskDetails(displayedDescription, List.of());
    }

    /**
     * Contains the task details needed to build a graphical task card.
     *
     * @param taskNumber the optional one-based list position
     * @param taskType the task's category
     * @param isDone whether the task is complete
     * @param description the task description without date details
     * @param scheduleDetails the task's labelled dates or times
     */
    record TaskDisplay(String taskNumber, TaskType taskType, boolean isDone, String description,
                       List<ScheduleDetail> scheduleDetails) {
    }

    /**
     * Contains one labelled date or time displayed beneath a task description.
     *
     * @param label the role of the date or time
     * @param value the formatted date or time
     */
    record ScheduleDetail(String label, String value) {
    }

    /**
     * Contains the parsed description and schedule details used to create a task display.
     *
     * @param description the task description without date details
     * @param scheduleDetails the task's labelled dates or times
     */
    private record TaskDetails(String description, List<ScheduleDetail> scheduleDetails) {
    }
}
