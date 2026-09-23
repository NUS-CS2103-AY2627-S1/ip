package sallman.command;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import sallman.SallmanException;
import sallman.task.Task;

/**
 * The orders the task list can be sorted into, each paired with the keyword
 * the user types for it.
 * <p>
 * Written as an enum for the same reasons as {@link CommandType}: the compiler
 * can check that an order is one of the known ones, and {@link #keywords()}
 * builds the help text from the constants, so a new order cannot be added
 * without the help text following it.
 */
public enum SortOrder {

    /**
     * Soonest first. Tasks that carry no date go last, since a todo has no
     * place on a timeline but should not disappear from the list either.
     */
    DATE("date", Comparator.comparing(task -> task.getSortDate().orElse(LocalDate.MAX))),

    /** Alphabetical by description, ignoring case. */
    NAME("name", Comparator.comparing(task -> task.getDescription().toLowerCase(Locale.ROOT))),

    /** Tasks still to do first, finished ones after them. */
    STATUS("status", Comparator.comparing(Task::isDone));

    /** What the user types to sort by this order. */
    private final String keyword;

    /** How this order compares one task with another. */
    private final Comparator<Task> comparator;

    SortOrder(String keyword, Comparator<Task> comparator) {
        this.keyword = keyword;
        this.comparator = comparator;
    }

    /**
     * Returns the keyword the user types for this order.
     *
     * @return the keyword, for use in replies that name the order back
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns how this order compares two tasks.
     *
     * @return the comparator to sort the list with
     */
    public Comparator<Task> getComparator() {
        return comparator;
    }

    /**
     * Finds the order matching a keyword typed by the user.
     *
     * @param keyword what the user typed after {@code sort}
     * @return the matching order
     * @throws SallmanException if no order uses that keyword
     */
    public static SortOrder fromKeyword(String keyword) throws SallmanException {
        return Arrays.stream(values())
                .filter(order -> order.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new SallmanException(
                        "I'm sorry, but I don't know how to sort by \"" + keyword + "\".",
                        "I can sort by: " + keywords() + "."));
    }

    /**
     * Lists every order's keyword, for use in help and error text.
     *
     * @return the keywords separated by commas, e.g. {@code date, name, ...}
     */
    public static String keywords() {
        return Arrays.stream(values())
                .map(SortOrder::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
