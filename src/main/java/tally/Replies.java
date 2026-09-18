package tally;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import tally.parser.FreeQuery;
import tally.task.Task;
import tally.task.TaskList;

/**
 * What Tally says about the tally itself.
 *
 * <p>Every reply that reads the tasks and turns them into lines is worked out here:
 * the listing, the search, the free-day answer, and the count that follows a change.
 * The replies that only restate a task the caller already has in hand stay with the
 * command that made the change.
 *
 * <p>Kept apart from Tally because the two answer different questions. Tally decides
 * what happens and when it is said; this decides how it reads.
 */
final class Replies {
    private final TaskList tasks;

    /**
     * Creates the replies for one tally.
     *
     * @param tasks the tally to answer about.
     */
    Replies(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns when the user is next free for as long as they asked, or says there is
     * no such stretch.
     *
     * @param query the run of days wanted, and the day to start looking from.
     * @return the lines to tell the user.
     */
    String[] describeFreeDays(FreeQuery query) {
        int days = query.days();
        Optional<LocalDate> found = tasks.findFreeRun(days, query.earliestDate());
        String answer = found.isPresent()
                ? describeRunFound(found.get(), days)
                : describeNoRun(query.earliestDate(), days);
        return new String[] {answer};
    }

    /**
     * Returns the reply naming when the user is next free for as long as they asked.
     *
     * @param start the first day of the run found.
     * @param days how many days in a row were wanted.
     * @return a sentence naming the day, reading for one day or for several.
     */
    private static String describeRunFound(LocalDate start, int days) {
        if (days == 1) {
            return String.format("Next free day: %s.", Task.formatDate(start));
        }
        return String.format("Next %d free days in a row begin %s.", days,
                Task.formatDate(start));
    }

    /**
     * Returns the reply for when no such run of days exists within the days searched.
     *
     * <p>The span is named rather than called a year, because the search also stops at
     * the last day a date can be written as, and from close enough to that day it covers
     * less than a year.
     *
     * @param earliestDate the day the search started from.
     * @param days how many days in a row were wanted.
     * @return a sentence saying so, reading for one day or for several.
     */
    private static String describeNoRun(LocalDate earliestDate, int days) {
        String span = String.format("%s to %s", Task.formatDate(earliestDate),
                Task.formatDate(TaskList.findLastDaySearched(earliestDate)));
        if (days == 1) {
            return String.format("No free day from %s. You did this to yourself.", span);
        }
        return String.format("No run of %d free days from %s. Ambitious.", days, span);
    }

    /** Returns the whole tally, or says so when there is nothing on it. */
    String[] describeTally() {
        if (tasks.isEmpty()) {
            return new String[] {"Nothing on record. Enjoy it while it lasts."};
        }
        List<Integer> allPositions = IntStream.range(0, tasks.size()).boxed().toList();
        return formatNumberedTasks("On record:", allPositions);
    }

    /**
     * Returns the tasks whose description contains the given text.
     *
     * <p>Each is shown against its place on the whole tally rather than its place
     * among the matches, so the number beside it still names that task if the user
     * goes on to mark or delete it.
     *
     * @param searchText the text to look for.
     * @return the lines to tell the user.
     */
    String[] describeMatchingTasks(String searchText) {
        List<Integer> positions = tasks.findPositions(searchText);
        if (positions.isEmpty()) {
            return new String[] {"No match. Nothing you wrote down, at least."};
        }
        return formatNumberedTasks("Matching:", positions);
    }

    /**
     * Returns a heading followed by one line per task, each numbered by its place on
     * the tally counting from 1.
     *
     * @param heading the line introducing the list.
     * @param positions the places of the tasks to show, counting from 0.
     * @return the lines to show, ready to hand to the user interface.
     */
    private String[] formatNumberedTasks(String heading, List<Integer> positions) {
        assert positions.stream().allMatch(position -> position >= 0 && position < tasks.size())
                : "positions come from findPositions or from a walk over the whole tally, and"
                + " both yield only places that hold a task, unlike one of: " + positions;
        // AI suggested String.format instead of concatenating strings manually.
        Stream<String> numberedTasks = positions.stream()
                .map(position -> String.format("%d.%s", position + 1, tasks.get(position)));
        return Stream.concat(Stream.of(heading), numberedTasks).toArray(String[]::new);
    }

    /**
     * Returns the sentence reporting how many tasks the tally now holds.
     *
     * @return for example "3 tasks on record."
     */
    String formatCountSentence() {
        // AI identified grammatical error, manual fix.
        return String.format("%d %s on record.",
                tasks.size(), tasks.size() == 1 ? "task" : "tasks");
    }
}
