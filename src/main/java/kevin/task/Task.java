package kevin.task;

import kevin.Kevin;
import kevin.KevinException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Task is the parent class of all tasks.
 */
public class Task {
    private static final Pattern PIPE_SPLITTER = compile("\\s*\\|\\s*");

    protected String description;
    protected boolean isDone;

    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Creates new Task with default value of false for isDone.
     */
    public Task(String description) {
        this(description,false);
    }

    /**
     * Marks Task as done and returns it.
     */
    public Task mark() {
        isDone = true;
        return this;
    }
    /**
     * Unmarks Task and returns it.
     */
    public Task unmark() {
        isDone = false;
        return this;
    }

    /**
     * Checks if Task description contains keyword.
     *
     * @param keyword Keyword to check for in Task description.
     * @return boolean Whether Task contains keyword.
     */
    public boolean contains(String keyword) {
        return this.description.contains(keyword);
    }

    /**
     * Adds Task to list of Tasks as a String.
     *
     * @param listString List of Tasks.
     * @param number Task number.
     * @return String Updated list of Tasks.
     */
    public String addToList(String listString, int number) {
        return listString + number + ". " + this + '\n';
    }

    /**
     * Formats LocalDateTime object into String
     * Outputs as d MMM yyyy hmma e.g 12 Apr 2026 1130am.
     * If minutes is 0, exclude the minutes eg 12 Apr 2026 12pm.
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        String pattern = "d MMM yyyy ";

        if (dateTime.getMinute() == 0) {
            pattern += "ha";
        } else {
            pattern += "hmma";
        }

        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    /**
     * Checks datetime is later than now.
     * s
     * @throws KevinException If datetime is earlier than now.
     */
    protected static void checkDateTimeLaterThanNow(LocalDateTime dateTime) throws KevinException {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new KevinException("Datetime cannot be earlier than now.");
        }
    }

    /**
     * Parses DateTimeString from tasks.txt into LocalDateTime.
     * Accepts d MMM yyyy hmma e.g 12 Apr 2026 1230pm and without the minutes e.g 12 Apr 2026 1130am.
     */
    public static LocalDateTime parseSavedDateTimeString(String dateTimeString) throws KevinException {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().
                    appendPattern("d MMM yyyy ").
                    optionalStart().appendPattern("hmm").optionalEnd().
                    optionalStart().appendPattern("h").optionalEnd().
                    appendPattern("a").
                    toFormatter(Locale.ENGLISH);
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new KevinException("DateTime is corrupted.");
        }

    }

    /**
     * Parses Task from line when loading tasks.txt.
     *
     * @param line Line containing task.
     * @return Saved Task.
     * @throws KevinException If task is corrupted in tasks.txt.
     */
    public static Task parseLine(String line) throws KevinException {
        try {
            //Each task must contain 1 | if done or 0 | if not done
            boolean containsDoneMarker = Pattern.compile("\\|\\s*1\\s*\\|")
                    .matcher(line)
                    .find();
            boolean containsNotDoneMarker = Pattern.compile("\\|\\s*0\\s*\\|")
                    .matcher(line)
                    .find();

            assert containsDoneMarker || containsNotDoneMarker: "Task has no done or not done marker";

            String[] parts = PIPE_SPLITTER.split(line);
            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            //Each task must be of type ToDo, Deadline or Event
            assert Set.of("T", "D", "E").contains(type) : "Task is not a ToDo, Deadline or Event";

            return switch (type) {
                case "T" -> new ToDo(description, isDone);
                case "D" -> new Deadline(description, isDone, parseSavedDateTimeString(parts[3]));
                case "E" -> new Event(description, isDone, parseSavedDateTimeString(parts[3]),
                        parseSavedDateTimeString(parts[4]));
                default -> new Task("Invalid task, can ignore");
            };
        } catch (AssertionError | KevinException e) {
            throw new KevinException(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new KevinException("Task is missing the DateTimes.");
        }
    }

    /**
     * Formats Task into String for saving into tasks.txt.
     * Output is 1/0 | description where 1 is done and 0 is not done.
     */
    public String formatSaveString() {
        if (isDone) {
            return "1 | " + description;
        } else {
            return "0 | " + description;
        }
    }

    @Override
    public String toString() {
        if (isDone) {
            return "[X] " + description;
        } else {
            return "[ ] " + description;
        }
    }
}
