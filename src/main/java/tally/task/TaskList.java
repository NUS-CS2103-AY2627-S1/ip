package tally.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/** The tasks on the user's tally, and the things that can be done to them. */
public class TaskList {
    /** How far ahead a free-day search looks before giving up, in days. */
    private static final int SEARCH_HORIZON_DAYS = 366;

    private final List<Task> tasks;

    /** Creates an empty tally. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a tally holding the given tasks.
     *
     * @param tasks the tasks to start with, in the order they should be listed.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds tasks to the end of the tally, in the order given.
     *
     * <p>Takes any number of them so that adding one reads no differently from
     * adding several.
     *
     * @param tasksToAdd the tasks to add.
     */
    public void add(Task... tasksToAdd) {
        for (Task task : tasksToAdd) {
            assert task != null
                    : "Storage.load throws rather than yield a null task, and the parser builds"
                    + " every other one, so a null would surface only later as a failed save";
        }
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Returns the first day of the earliest run of free days of the length asked for.
     *
     * <p>A day is free when no task on the tally takes it up. The search runs a year
     * ahead and no further, so that a tally with something on every day answers
     * rather than running on. It also stops at the last day a date can be written as,
     * since a day beyond that could be shown but not typed back in.
     *
     * @param days how many free days in a row are wanted, at least one.
     * @param earliestDate the first day that may be offered.
     * @return the first day of the earliest such run, or empty if there is none.
     */
    public Optional<LocalDate> findFreeRun(int days, LocalDate earliestDate) {
        assert days >= 1 : "Parser.parseFreeQuery refuses a run shorter than a day: " + days;
        LocalDate lastDay = findLastDaySearched(earliestDate);
        int freeDaysInARow = 0;
        for (LocalDate day = earliestDate; !day.isAfter(lastDay); day = day.plusDays(1)) {
            LocalDate candidate = day;
            boolean isTaken = tasks.stream().anyMatch(task -> task.occupies(candidate));
            freeDaysInARow = isTaken ? 0 : freeDaysInARow + 1;
            if (freeDaysInARow == days) {
                return Optional.of(day.minusDays(days - 1L));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns where the tally already holds the same task, if it does.
     *
     * @param task the task being considered.
     * @return its place on the tally counting from 0, or empty if it is not there.
     */
    public OptionalInt findPositionOf(Task task) {
        return IntStream.range(0, tasks.size())
                .filter(position -> tasks.get(position).isSameAs(task))
                .findFirst();
    }

    /**
     * Returns the last day a search starting from the given day looks at.
     *
     * <p>The reply naming no free run has to say which days were looked at, so where
     * the search stops is worked out here rather than in two places that could disagree.
     *
     * @param earliestDate the first day that may be offered.
     * @return the last day the search reaches, which is the sooner of a year ahead and
     *     the last day a date can be written as.
     */
    public static LocalDate findLastDaySearched(LocalDate earliestDate) {
        LocalDate lastDay = earliestDate.plusDays(SEARCH_HORIZON_DAYS - 1L);
        return lastDay.isAfter(Task.LAST_DATE) ? Task.LAST_DATE : lastDay;
    }

    /**
     * Puts the given tasks on the tally in place of whatever it holds.
     *
     * <p>This is how the tally is brought back into step with the data file after a save
     * that failed, so that what the user is shown is what a restart would give them.
     *
     * @param replacements the tasks to hold instead, in the order they should be listed.
     */
    public void replaceAll(List<Task> replacements) {
        tasks.clear();
        tasks.addAll(replacements);
    }

    /**
     * Removes a task from the tally.
     *
     * @param task the task to remove, which must be one this tally holds.
     */
    public void remove(Task task) {
        tasks.remove(task);
    }

    /**
     * Returns the task at the given position, counting from 0.
     *
     * @param index the position of the task.
     * @return the task there.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "Parser.parseTaskIndex turns what the user typed into a position only after"
                + " rejecting out-of-range numbers, so an invalid index here means a caller"
                + " reached past it: " + index;
        return tasks.get(index);
    }

    /**
     * Returns how many tasks the tally holds.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the tally holds no tasks at all.
     *
     * @return true when there is nothing on the tally.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the positions of the tasks whose description contains the given text.
     *
     * <p>Positions count from 0, and are the tasks' places on the whole tally
     * rather than places among the matches, so a number shown to the user still
     * names the same task in a later command.
     *
     * @param searchText the text to look for, matched without regard to case.
     * @return the positions of the matching tasks, in the order they were added.
     */
    public List<Integer> findPositions(String searchText) {
        String lowercaseSearchText = searchText.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(i -> tasks.get(i).getDescription().toLowerCase(Locale.ROOT).contains(lowercaseSearchText))
                .boxed()
                .toList();
    }

    /**
     * Returns a copy of the tasks as a plain list, for code that only reads them.
     *
     * <p>The list is a copy, so adding or removing through it does not change the tally.
     * The tasks in it are the same objects the tally holds, though, so marking one done
     * through this list marks it done on the tally as well.
     *
     * @return the tasks, in the order they were added.
     */
    public List<Task> copyAsList() {
        return new ArrayList<>(tasks);
    }
}
