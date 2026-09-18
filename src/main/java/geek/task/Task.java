package geek.task;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;

import geek.exception.GeekException;
import geek.time.DateTimeParser;

/**
 * Represents a task with a description and mutable completion status.
 *
 * Concrete task kinds are created through the factory methods in this class.
 */
public abstract class Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy, h:mm a",
                    Locale.ENGLISH
            );

    private final String description;
    private boolean isDone;

    private Task(String description) {
        this(description, false);
    }

    private Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Creates an incomplete todo task.
     *
     * @param description Description shown to the user.
     * @return New todo task.
     */
    public static Task newTodo(String description) {
        return new Todo(description);
    }

    /**
     * Creates an incomplete deadline task from a supported date or date-time.
     *
     * @param description Description shown to the user.
     * @param deadline User-entered deadline.
     * @return New deadline task.
     * @throws DateTimeParseException If the deadline is not a supported date
     *         or date-time.
     */
    public static Task newDeadline(
            String description,
            String deadline
    ) {
        DateTimeParser.ParsedDateTime parsedDeadline =
                DateTimeParser.parseDeadline(deadline);

        return new Deadline(
                description,
                parsedDeadline.value(),
                parsedDeadline.hasTime()
        );
    }

    /**
     * Creates an incomplete event task from a start and end time.
     *
     * @param description Description shown to the user.
     * @param startTime User-entered event start time.
     * @param endTime User-entered event end time.
     * @return New event task.
     * @throws DateTimeParseException If either time is unsupported.
     * @throws GeekException If the event does not end after it starts.
     */
    public static Task newEvent(
            String description,
            String startTime,
            String endTime
    ) {
        LocalDateTime parsedStartTime =
                DateTimeParser.parseDateTime(startTime);

        LocalDateTime parsedEndTime =
                DateTimeParser.parseDateTime(endTime);

        if (!parsedEndTime.isAfter(parsedStartTime)) {
            throw new GeekException(
                    "The event end time must be "
                            + "after the start time."
            );
        }

        return new Event(
                description,
                parsedStartTime,
                parsedEndTime
        );
    }

    /**
     * Restores a task from a record produced by {@link #toDataString()}.
     *
     * @param data Tab-separated stored task record.
     * @return Task represented by the record.
     * @throws IllegalArgumentException If the record structure, encoded
     *         fields, status, or date-time values are invalid.
     */
    public static Task fromDataString(String data) {
        String[] fields = data.split("\\t", -1);

        if (fields.length < 3) {
            throw new IllegalArgumentException(
                    "Saved task has too few fields."
            );
        }

        boolean isDone;

        if (fields[1].equals("1")) {
            isDone = true;
        } else if (fields[1].equals("0")) {
            isDone = false;
        } else {
            throw new IllegalArgumentException(
                    "Saved task has an invalid status."
            );
        }

        String description = decode(fields[2]);

        if (description.isBlank()) {
            throw new IllegalArgumentException(
                    "Saved task has an empty description."
            );
        }

        return switch (fields[0]) {
            case "T" -> {
                requireFieldCount(fields, 3);
                yield new Todo(description, isDone);
            }
            case "D" -> {
                requireFieldCount(fields, 4);

                String deadlineText = decode(fields[3]);

                if (deadlineText.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved deadline has no date."
                    );
                }

                DateTimeParser.ParsedDateTime deadline;

                try {
                    deadline = DateTimeParser.parseStoredDeadline(
                            deadlineText
                    );
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Saved deadline has an invalid date.",
                            e
                    );
                }

                yield new Deadline(
                        description,
                        deadline.value(),
                        deadline.hasTime(),
                        isDone
                );
            }
            case "E" -> {
                requireFieldCount(fields, 5);

                String startTimeText = decode(fields[3]);
                String endTimeText = decode(fields[4]);

                if (startTimeText.isBlank()
                        || endTimeText.isBlank()) {
                    throw new IllegalArgumentException(
                            "Saved event has an invalid time."
                    );
                }

                LocalDateTime startTime;
                LocalDateTime endTime;

                try {
                    startTime =
                            LocalDateTime.parse(startTimeText);
                    endTime =
                            LocalDateTime.parse(endTimeText);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Saved event has an invalid time.",
                            e
                    );
                }

                if (!endTime.isAfter(startTime)) {
                    throw new IllegalArgumentException(
                            "Saved event ends before it starts."
                    );
                }

                yield new Event(
                        description,
                        startTime,
                        endTime,
                        isDone
                );
            }
            default -> throw new IllegalArgumentException(
                    "Saved task has an unknown type."
            );
        };
    }

    /**
     * Serializes this task as one tab-separated storage record.
     *
     * Text fields are URL-safe Base64 encoded so that their contents cannot be
     * mistaken for field separators.
     *
     * @return Serialized task record.
     */
    public String toDataString() {
        StringBuilder data = new StringBuilder();

        data.append(getTypeCode())
                .append('\t')
                .append(isDone ? "1" : "0")
                .append('\t')
                .append(encode(description));

        appendAdditionalData(data);
        return data.toString();
    }

    /**
     * Returns the storage code identifying the concrete task type.
     *
     * @return Single-character task type code.
     */
    protected abstract String getTypeCode();

    /**
     * Appends fields specific to the concrete task type.
     *
     * The default implementation appends nothing because todo tasks have no
     * additional fields.
     *
     * @param data Builder already containing the common task fields.
     */
    protected void appendAdditionalData(StringBuilder data) {
        // Todo tasks have no additional fields.
    }

    /**
     * Appends one tab-separated, Base64-encoded field.
     *
     * @param data Storage record being built.
     * @param value Field value to append.
     */
    protected static void appendEncodedField(
            StringBuilder data,
            String value
    ) {
        data.append('\t').append(encode(value));
    }

    /**
     * Marks this task as completed.
     */
    public void mark() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns the description for use by concrete task formatters.
     *
     * @return Task description.
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns the status symbol used in task displays.
     *
     * @return {@code X} when completed, or one space when not completed.
     */
    public String getStatus() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task occurs on the specified date.
     *
     * Undated tasks return {@code false}; dated task types override this
     * behavior.
     *
     * @param date Date to test.
     * @return {@code true} if this task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date-time used to place this task in chronological order.
     *
     * Undated tasks return an empty value. A deadline uses its due time, while
     * an event uses its start time.
     *
     * @return Date-time used for sorting, or an empty value if undated.
     */
    public Optional<LocalDateTime> getSortDateTime() {
        return Optional.empty();
    }

    /**
     * Encodes a storage field using URL-safe Base64 without padding.
     *
     * @param value Value to encode.
     * @return Encoded value.
     */
    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }

    /**
     * Decodes a URL-safe Base64 storage field as UTF-8 text.
     *
     * @param value Encoded value.
     * @return Decoded text.
     * @throws IllegalArgumentException If the value is not valid Base64.
     */
    private static String decode(String value) {
        return new String(
                Base64.getUrlDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }

    /**
     * Verifies that a stored record has the required number of fields.
     *
     * @param fields Fields in the stored record.
     * @param expectedCount Required number of fields.
     * @throws IllegalArgumentException If the field count is incorrect.
     */
    private static void requireFieldCount(
            String[] fields,
            int expectedCount
    ) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException(
                    "Saved task has an invalid number of fields."
            );
        }
    }

    /**
     * Returns the human-readable task representation shown by the UI.
     *
     * @return Task status and description.
     */
    @Override
    public String toString() {
        return String.format(
                "[%s] %s",
                getStatus(),
                description
        );
    }

    /**
     * Represents a task without date information.
     */
    private static class Todo extends Task {
        private Todo(String description) {
            this(description, false);
        }

        private Todo(String description, boolean isDone) {
            super(description, isDone);
        }

        @Override
        protected String getTypeCode() {
            return "T";
        }

        @Override
        public String toString() {
            return String.format(
                    "[T][%s] %s",
                    getStatus(),
                    getDescription()
            );
        }
    }

    /**
     * Represents a task due on a date, optionally at a specific time.
     */
    private static class Deadline extends Task {
        private final LocalDateTime deadline;
        /**
         * Indicates whether the original deadline includes a time component.
         */
        private final boolean hasTime;

        private Deadline(
                String description,
                LocalDateTime deadline,
                boolean hasTime
        ) {
            this(description, deadline, hasTime, false);
        }

        private Deadline(
                String description,
                LocalDateTime deadline,
                boolean hasTime,
                boolean isDone
        ) {
            super(description, isDone);
            this.deadline = deadline;
            this.hasTime = hasTime;
        }

        @Override
        protected String getTypeCode() {
            return "D";
        }

        @Override
        protected void appendAdditionalData(
                StringBuilder data
        ) {
            appendEncodedField(
                    data,
                    hasTime
                            ? deadline.toString()
                            : deadline.toLocalDate().toString()
            );
        }

        @Override
        public boolean occursOn(LocalDate date) {
            return deadline.toLocalDate().equals(date);
        }

        @Override
        public Optional<LocalDateTime> getSortDateTime() {
            return Optional.of(deadline);
        }

        @Override
        public String toString() {
            String formattedDeadline = hasTime
                    ? deadline.format(DISPLAY_DATE_TIME_FORMAT)
                    : deadline.toLocalDate()
                            .format(DISPLAY_DATE_FORMAT);

            return String.format(
                    "[D][%s] %s (by: %s)",
                    getStatus(),
                    getDescription(),
                    formattedDeadline
            );
        }
    }

    /**
     * Represents an event spanning a start and end time.
     */
    private static class Event extends Task {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        private Event(
                String description,
                LocalDateTime startTime,
                LocalDateTime endTime
        ) {
            this(description, startTime, endTime, false);
        }

        private Event(
                String description,
                LocalDateTime startTime,
                LocalDateTime endTime,
                boolean isDone
        ) {
            super(description, isDone);
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        protected String getTypeCode() {
            return "E";
        }

        @Override
        protected void appendAdditionalData(
                StringBuilder data
        ) {
            appendEncodedField(
                    data,
                    startTime.toString()
            );
            appendEncodedField(
                    data,
                    endTime.toString()
            );
        }

        @Override
        public boolean occursOn(LocalDate date) {
            LocalDate startDate = startTime.toLocalDate();
            LocalDate endDate = endTime.toLocalDate();

            return !date.isBefore(startDate)
                    && !date.isAfter(endDate);
        }

        @Override
        public Optional<LocalDateTime> getSortDateTime() {
            return Optional.of(startTime);
        }

        @Override
        public String toString() {
            return String.format(
                    "[E][%s] %s (from: %s to: %s)",
                    getStatus(),
                    getDescription(),
                    startTime.format(
                            DISPLAY_DATE_TIME_FORMAT
                    ),
                    endTime.format(
                            DISPLAY_DATE_TIME_FORMAT
                    )
            );
        }
    }
}
