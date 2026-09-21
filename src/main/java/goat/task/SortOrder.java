package goat.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import goat.GoatException;

/**
 * The ways a task list can be put in order.
 *
 * Each constant pairs the word the user types with the comparison that word
 * means, so the two cannot drift apart and adding an order is one new constant
 * rather than an edit in three places.
 *
 * The pattern matches {@link goat.parser.CommandType}: a keyword, a lookup that
 * is the single place an unrecognized word is rejected, and no behavior beyond
 * naming one choice.
 */
public enum SortOrder {
    /**
     * Earliest first, with undated tasks last.
     *
     * A todo has no time at all, so it cannot be placed among the dated ones by
     * date. Putting those last keeps the answer to "what is coming up?" at the
     * top, which is the question this order is asked.
     */
    DATE("date", Comparator
            .<Task, LocalDateTime>comparing(
                    task -> task.getScheduledTime().orElse(LocalDateTime.MAX))
            .thenComparing(task -> task.getDescription().toLowerCase(Locale.ROOT))),

    /**
     * Alphabetical by description, ignoring case.
     *
     * Ignoring case because "read book" and "Read book" differing in position
     * by capitalization would look like a bug rather than a rule. Folded under
     * Locale.ROOT so the order does not depend on the machine's language.
     */
    NAME("name", Comparator.comparing(task -> task.getDescription().toLowerCase(Locale.ROOT)));

    /** The word the user types to ask for this order. */
    private final String keyword;

    /** How two tasks are compared under this order. */
    private final Comparator<Task> comparator;

    SortOrder(String keyword, Comparator<Task> comparator) {
        this.keyword = keyword;
        this.comparator = comparator;
    }

    /**
     * Finds the order a typed word asks for.
     *
     * @param keyword the word the user typed after "sort"
     * @return the matching order
     * @throws GoatException if no order uses that word
     */
    public static SortOrder fromKeyword(String keyword) throws GoatException {
        return Arrays.stream(values())
                .filter(order -> order.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new GoatException(
                        "Sort by what? Try: sort date, or sort name"));
    }

    /**
     * Returns how two tasks compare under this order.
     *
     * @return the comparator this order stands for
     */
    public Comparator<Task> getComparator() {
        return comparator;
    }

    /**
     * Returns the word the user types to ask for this order.
     *
     * @return the keyword, for showing back in a confirmation
     */
    public String getKeyword() {
        return keyword;
    }
}
