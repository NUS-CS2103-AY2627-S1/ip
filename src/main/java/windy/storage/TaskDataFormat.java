package windy.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the task record format shared by task serialization and storage loading.
 */
public final class TaskDataFormat {
    private static final int COMMON_FIELD_COUNT = 2;
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_PATTERN = "\\s*\\|\\s*";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    private TaskDataFormat() {
    }

    /**
     * Formats a task as one record in the task data file.
     *
     * @param taskType the type of task being stored.
     * @param isDone whether the task is completed.
     * @param taskFields the type-specific task fields.
     * @return the formatted task record.
     * @throws IllegalArgumentException if the number of task fields is incorrect.
     */
    public static String formatRecord(TaskType taskType, boolean isDone, String... taskFields) {
        int expectedTaskFieldCount = taskType.getFieldCount() - COMMON_FIELD_COUNT;
        if (taskFields.length != expectedTaskFieldCount) {
            throw new IllegalArgumentException("Expected " + expectedTaskFieldCount
                    + " task fields for " + taskType + " but found " + taskFields.length);
        }

        List<String> fields = new ArrayList<>();
        fields.add(taskType.getCode());
        fields.add(isDone ? COMPLETE_STATUS : INCOMPLETE_STATUS);
        fields.addAll(List.of(taskFields));
        return String.join(FIELD_SEPARATOR, fields);
    }

    /**
     * Splits one task record into its stored fields.
     *
     * @param record the task record to split.
     * @return all stored fields, including empty trailing fields.
     */
    public static String[] splitRecord(String record) {
        return record.split(FIELD_SEPARATOR_PATTERN, -1);
    }

    /**
     * Parses a stored completion status.
     *
     * @param status the stored status field.
     * @return {@code true} for a completed task or {@code false} for an incomplete task.
     * @throws IllegalArgumentException if the status is not supported.
     */
    public static boolean parseCompletionStatus(String status) {
        return switch (status) {
            case INCOMPLETE_STATUS -> false;
            case COMPLETE_STATUS -> true;
            default -> throw new IllegalArgumentException(
                    "completion status must be 0 or 1 but was '" + status + "'");
        };
    }

    /**
     * Represents a task type in the data file and its required field count.
     */
    public enum TaskType {
        /** A task without a date. */
        TODO("T", 3),
        /** A task with a deadline. */
        DEADLINE("D", 4),
        /** A task spanning a date range. */
        EVENT("E", 5);

        private final String code;
        private final int fieldCount;

        TaskType(String code, int fieldCount) {
            this.code = code;
            this.fieldCount = fieldCount;
        }

        public String getCode() {
            return code;
        }

        public int getFieldCount() {
            return fieldCount;
        }

        /**
         * Finds the task type represented by a stored code.
         *
         * @param code the stored task type code.
         * @return the matching task type.
         * @throws IllegalArgumentException if the code is not supported.
         */
        public static TaskType fromCode(String code) {
            for (TaskType taskType : values()) {
                if (taskType.code.equals(code)) {
                    return taskType;
                }
            }
            throw new IllegalArgumentException("unsupported task type '" + code + "'");
        }
    }
}
