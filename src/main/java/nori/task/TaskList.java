package nori.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import nori.NoriException;

/**
 * Stores tasks and performs operations on their task numbers and date queries.
 */
public class TaskList {
    /** Renders a scheduled event's start time, as in {@code 09:30}. */
    private static final DateTimeFormatter SCHEDULE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    /** Stands in for a missing start time, so untimed rows line up under the timed ones. */
    private static final String SCHEDULE_TIME_PADDING = "      ";

    /** The tasks, in the order the user added them. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in argument order.
     *
     * @param tasks the tasks to place in the list.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the tasks with which to initialize the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the specified zero-based index.
     *
     * @param index the index at which to insert the task.
     * @param task the task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index the index of the task to remove.
     * @return the removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the index of the task to return.
     * @return the task at the specified index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns a valid zero-based index for a user-supplied task number.
     *
     * @param taskNumber the user-supplied task number.
     * @param commandKeyword the command using the task number.
     * @return the matching zero-based index.
     * @throws NoriException if the task number is unusable.
     */
    public int getTaskIndex(String taskNumber, String commandKeyword) throws NoriException {
        try {
            int taskIndex = Integer.parseInt(taskNumber) - 1;
            if (taskIndex >= 0 && taskIndex < size()) {
                return taskIndex;
            }
            throw new NoriException(getTaskNumberError(taskNumber, commandKeyword));
        } catch (NumberFormatException exception) {
            throw new NoriException(getTaskNumberError(taskNumber, commandKeyword));
        }
    }

    /**
     * Returns the task list as display lines.
     *
     * @return the task-list response lines.
     */
    public String[] getDisplayLines() {
        if (isEmpty()) {
            return new String[] {"The iceberg is empty. Try \"todo borrow book\". Noot noot!"};
        }

        List<String> taskLines = IntStream.range(0, size()).mapToObj(this::getNumberedTask).toList();
        return prependHeading("Noot noot! Tasks currently chilling on the iceberg:", taskLines);
    }

    /**
     * Returns deadline and event task lines that occur on a date.
     *
     * @param date the date to search.
     * @return the date-query response lines.
     */
    public String[] getTasksOnDateDisplayLines(LocalDate date) {
        List<String> matchingTasks = getNumberedTasks(task -> occursOn(task, date));
        if (matchingTasks.isEmpty()) {
            return new String[] {"Nothing is hatching on " + date + ". The ice is quiet."};
        }
        return prependHeading("Noot noot! Things hatching on " + date + ":", matchingTasks);
    }

    /**
     * Returns deadline and event task lines that occur within a date range.
     *
     * @param dateRange the inclusive date range to search.
     * @return the date-range response lines.
     */
    public String[] getTasksInDateRangeDisplayLines(DateRange dateRange) {
        assert dateRange != null : "The parser builds the range before a range listing is requested.";

        List<String> matchingTasks = getNumberedTasks(task -> occursInDateRange(task, dateRange));
        if (matchingTasks.isEmpty()) {
            return new String[] {"Nothing is hatching from " + dateRange.getFrom()
                    + " to " + dateRange.getTo() + ". The ice is quiet."};
        }
        return prependHeading("Noot noot! Things hatching from " + dateRange.getFrom()
                + " to " + dateRange.getTo() + ":", matchingTasks);
    }

    /**
     * Returns task lines whose descriptions contain a keyword.
     *
     * The search ignores case and matches anywhere in the description, so
     * {@code book} finds both "read book" and "Bookshop trip". Only the
     * description is searched; a deadline's date and an event's start and end
     * details are not. Matching lines keep their numbers from the full list, so
     * a number shown here can be used directly with mark, unmark or delete.
     *
     * @param keyword the text to search for in task descriptions.
     * @return the keyword-search response lines.
     */
    public String[] getTasksMatchingKeywordDisplayLines(String keyword) {
        String foldedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<String> matchingTasks = getNumberedTasks(task -> containsKeyword(task, foldedKeyword));
        if (matchingTasks.isEmpty()) {
            return new String[] {"No matching fish in this sea. Try another keyword!"};
        }
        return prependHeading("Noot noot! I found these fish:", matchingTasks);
    }

    /**
     * Returns one day's tasks laid out as a schedule.
     *
     * The events the day names a start time for come first, in the order they
     * start. Below them come the events that run over the day without a time
     * of their own, then the deadlines falling due, then every to-do, which
     * belongs to no particular day. A section with nothing in it is left out.
     *
     * Task numbers are the ones the full list uses, so a number read here can
     * be given straight to mark, unmark or delete.
     *
     * @param date the date to lay out.
     * @return the schedule response lines.
     */
    public String[] getScheduleDisplayLines(LocalDate date) {
        assert date != null : "A schedule is asked for either today or a date the parser read.";

        List<String> scheduleLines = new ArrayList<>(getTimedEventLines(date));
        addSection(scheduleLines, "All day:", getUntimedEventLines(date));
        addSection(scheduleLines, "Due:", getDeadlineLines(date));
        addSection(scheduleLines, "Anytime:", getTodoLines());
        if (scheduleLines.isEmpty()) {
            return new String[] {"Nothing on the schedule for " + date + ". The ice is quiet."};
        }
        return prependHeading("Noot noot! Schedule for " + date + ":", scheduleLines);
    }

    /**
     * Finds a stored task that records the same thing as the given one.
     *
     * @param task the task to look for.
     * @return the stored task it repeats, or empty when the list holds no such task.
     */
    public Optional<Task> findSameTask(Task task) {
        assert task != null : "A duplicate check is made for a task a command has just built.";

        return tasks.stream().filter(task::isSameTask).findFirst();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list contains no tasks.
     *
     * @return {@code true} if no tasks are stored.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a read-only view of the stored tasks.
     *
     * @return an unmodifiable task list view.
     */
    public List<Task> asUnmodifiableList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Explains why a command's task number cannot be used.
     *
     * @param taskNumber the user-supplied task number.
     * @param commandKeyword the command using the task number.
     * @return a corrective error message.
     */
    private String getTaskNumberError(String taskNumber, String commandKeyword) {
        if (taskNumber.isEmpty()) {
            return "NOOT?! \"" + commandKeyword + "\" needs a task number. Try \""
                    + commandKeyword + " 1\".";
        }
        try {
            int taskIndex = Integer.parseInt(taskNumber) - 1;
            if (taskIndex < 0) {
                return "NOOT?! Task numbers start from 1, not " + taskNumber + ". Penguins can count!";
            }
            if (isEmpty()) {
                return "NOOT?! The iceberg is empty, so there is nothing to " + commandKeyword + ".";
            }
            return "NOOT?! The iceberg has only " + size() + " task(s), so \"" + commandKeyword + " "
                    + taskNumber + "\" points straight into the sea.";
        } catch (NumberFormatException exception) {
            if (isIntegerLiteral(taskNumber)) {
                return "GIANT NOOT! \"" + taskNumber + "\" is far too large for a task number."
                        + " Use a whole number from 1 to " + Math.max(size(), 1) + ".";
            }
            return "NOOT?! \"" + taskNumber + "\" is not a task number. Use \""
                    + commandKeyword + " 1\"; penguins count with digits.";
        }
    }

    /**
     * Returns whether text is an optionally signed whole-number literal.
     *
     * @param text the text to inspect.
     * @return {@code true} if the text contains only an optional sign and digits.
     */
    private boolean isIntegerLiteral(String text) {
        int firstDigitIndex = text.startsWith("-") || text.startsWith("+") ? 1 : 0;
        if (firstDigitIndex == text.length()) {
            return false;
        }
        return text.substring(firstDigitIndex).chars().allMatch(Character::isDigit);
    }

    /**
     * Finds the display lines of the tasks a search accepts, numbered as in the full list.
     *
     * @param isMatch the search each task is put through.
     * @return the matching numbered task lines.
     */
    private List<String> getNumberedTasks(Predicate<Task> isMatch) {
        return IntStream.range(0, size())
                .filter(index -> isMatch.test(get(index)))
                .mapToObj(this::getNumberedTask)
                .toList();
    }

    /**
     * Finds the rows for the events that start at a known time on a date.
     *
     * @param date the date being laid out.
     * @return the rows, each behind its start time, earliest first.
     */
    private List<String> getTimedEventLines(LocalDate date) {
        return IntStream.range(0, size())
                .mapToObj(index -> toScheduledEvent(index, date))
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(ScheduledEvent::startTime))
                .map(scheduledEvent -> scheduledEvent.startTime().format(SCHEDULE_TIME_FORMATTER)
                        + " " + scheduledEvent.displayLine())
                .toList();
    }

    /**
     * Places one task on a schedule, if it is an event starting at a known time on the date.
     *
     * @param index the zero-based index of the task.
     * @param date the date being laid out.
     * @return the scheduled event, or empty when the task is not one.
     */
    private Optional<ScheduledEvent> toScheduledEvent(int index, LocalDate date) {
        Task task = get(index);
        if (!(task instanceof Event)) {
            return Optional.empty();
        }
        return ((Event) task).findStartTimeOn(date)
                .map(startTime -> new ScheduledEvent(startTime, getNumberedTask(index)));
    }

    /**
     * Finds the rows for the events running over a date without starting at a time on it.
     *
     * @param date the date being laid out.
     * @return the rows for the date's all-day events.
     */
    private List<String> getUntimedEventLines(LocalDate date) {
        return getPaddedLines(task -> task instanceof Event && occursOn(task, date)
                && ((Event) task).findStartTimeOn(date).isEmpty());
    }

    /**
     * Finds the rows for the deadlines falling due on a date.
     *
     * @param date the date being laid out.
     * @return the rows for the date's deadlines.
     */
    private List<String> getDeadlineLines(LocalDate date) {
        return getPaddedLines(task -> task instanceof Deadline && occursOn(task, date));
    }

    /**
     * Finds the rows for every to-do, which has no date and so suits any day.
     *
     * @return the rows for the stored to-dos.
     */
    private List<String> getTodoLines() {
        return getPaddedLines(task -> task instanceof Todo);
    }

    /**
     * Finds numbered rows indented to the width of the schedule's time column.
     *
     * @param isMatch the test each task is put through.
     * @return the matching rows, each padded into line.
     */
    private List<String> getPaddedLines(Predicate<Task> isMatch) {
        return getNumberedTasks(isMatch).stream().map(line -> SCHEDULE_TIME_PADDING + line).toList();
    }

    /**
     * Adds a headed section to a schedule, unless the section has nothing in it.
     *
     * @param scheduleLines the schedule being built.
     * @param heading the section heading.
     * @param sectionLines the section rows.
     */
    private static void addSection(List<String> scheduleLines, String heading, List<String> sectionLines) {
        if (sectionLines.isEmpty()) {
            return;
        }
        scheduleLines.add(heading);
        scheduleLines.addAll(sectionLines);
    }

    /**
     * Returns whether a task's description contains an already folded keyword.
     *
     * Case folding uses {@link Locale#ROOT} so a search behaves the same way
     * whatever locale the machine running Nori is set to. The caller folds the
     * keyword once rather than once per task.
     *
     * @param task the task to inspect.
     * @param foldedKeyword the search keyword, already in lower case.
     * @return {@code true} if the description contains the keyword.
     */
    private static boolean containsKeyword(Task task, String foldedKeyword) {
        return task.getDescription().toLowerCase(Locale.ROOT).contains(foldedKeyword);
    }

    /**
     * Returns one task's display line, numbered as it is in the full list.
     *
     * @param index the zero-based index of the task.
     * @return the numbered task line.
     */
    private String getNumberedTask(int index) {
        return (index + 1) + "." + get(index);
    }

    /**
     * Adds a heading before task lines.
     *
     * @param heading the response heading.
     * @param taskLines the numbered task lines.
     * @return the combined response lines.
     */
    private String[] prependHeading(String heading, List<String> taskLines) {
        assert !taskLines.isEmpty() : "A heading is added only when at least one task matched.";

        return Stream.concat(Stream.of(heading), taskLines.stream()).toArray(String[]::new);
    }

    /**
     * Returns whether a task is a deadline or event occurring on the given date.
     *
     * @param task the task to inspect.
     * @param date the date to match.
     * @return {@code true} if the task occurs on {@code date}.
     */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return ((Deadline) task).occursOn(date);
        }
        if (task instanceof Event) {
            return ((Event) task).occursOn(date);
        }
        return false;
    }

    /**
     * Returns whether a task is a deadline in, or an event overlapping, a date range.
     *
     * @param task the task to inspect.
     * @param dateRange the inclusive date range to match.
     * @return {@code true} if the task occurs within the date range.
     */
    private boolean occursInDateRange(Task task, DateRange dateRange) {
        if (task instanceof Deadline) {
            return ((Deadline) task).occursInDateRange(dateRange.getFrom(), dateRange.getTo());
        }
        if (task instanceof Event) {
            return ((Event) task).occursInDateRange(dateRange.getFrom(), dateRange.getTo());
        }
        return false;
    }

    /**
     * Pairs an event's display row with the time it starts, so a day can be put in order.
     *
     * @param startTime the time the event starts on the day being laid out.
     * @param displayLine the event's numbered display row.
     */
    private record ScheduledEvent(LocalTime startTime, String displayLine) {
    }
}
