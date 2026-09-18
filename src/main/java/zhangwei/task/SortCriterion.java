package zhangwei.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import zhangwei.ZhangWeiException;

/**
 * The orders the task list can be sorted into, each paired with the keyword
 * the user types for it.
 *
 * <p>Keeping the keyword and the ordering together means the parser, the
 * confirmation message and the "I can't sort by that" complaint can never
 * disagree about which orders exist, the same way {@link
 * zhangwei.command.CommandType} does for commands.
 *
 * <p>Every ordering here is used with a stable sort, so tasks that compare
 * equal keep the order they were already in. Sorting twice by the same
 * criterion therefore changes nothing, which is what a user expects.
 */
public enum SortCriterion {

    /**
     * Earliest first. A todo carries no date and so has nothing to be placed
     * by; those go last, after every task that does have one.
     */
    DATE("date", Comparator
            .comparing((Task task) -> task.getDate().isEmpty())
            .thenComparing(task -> task.getDate().orElse(LocalDate.MIN))),

    /** A to Z, ignoring case, so "apple" and "Apple" sit together. */
    DESCRIPTION("description",
            Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER)),

    /** Still to do first, then the ones already done. */
    STATUS("status", Comparator.comparing(Task::isDone));

    private final String keyword;
    private final Comparator<Task> comparator;

    /**
     * Creates a criterion invoked by the given keyword and ordering tasks with
     * the given comparator.
     *
     * @param keyword the word the user types to sort this way.
     * @param comparator the order this criterion puts tasks into.
     */
    SortCriterion(String keyword, Comparator<Task> comparator) {
        this.keyword = keyword;
        this.comparator = comparator;
    }

    /**
     * Returns the word the user types to sort this way.
     *
     * @return the keyword of this criterion, e.g. "date".
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the order this criterion puts tasks into.
     *
     * @return the comparator for this criterion.
     */
    public Comparator<Task> getComparator() {
        return comparator;
    }

    /**
     * Returns the criterion the given keyword asks for.
     *
     * @param keyword the word typed after "sort".
     * @return the criterion using that keyword.
     * @throws ZhangWeiException if no criterion uses that keyword, listing the
     *     keywords that would have worked.
     */
    public static SortCriterion fromKeyword(String keyword) throws ZhangWeiException {
        return Arrays.stream(values())
                .filter(criterion -> criterion.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new ZhangWeiException("I can't sort by \"" + keyword
                        + "\". I can sort by: " + listKeywords() + "."));
    }

    /**
     * Returns every keyword, comma separated, in the order declared above.
     *
     * @return the keywords of all criteria, e.g. "date, description, status".
     */
    public static String listKeywords() {
        return Arrays.stream(values())
                .map(SortCriterion::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
