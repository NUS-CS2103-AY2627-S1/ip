package piplupbot.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import piplupbot.PiplupBotException;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the orders the task list can be put into, one constant for each
 * word the user may type after {@code sort}.
 *
 * <p>Each constant carries the word to match and the order it stands for, in the
 * way {@link TaskType} carries a letter and a field count: keeping the two
 * together is what lets {@link #fromKeyword} build its hint from
 * {@link #values()}, so a fifth key would appear in the hint by itself rather
 * than having to be spelled out a second time somewhere else.</p>
 *
 * <p>Every comparator here reads a task by asking it -- for its date, its
 * description, its type or its done status -- rather than by testing which kind
 * of task it is. That is the same polymorphism that lets one list hold all three
 * kinds and print them without a {@code switch}, and it is why a fourth kind of
 * task would sort correctly without a line changing here.</p>
 */
public enum SortKey {
    /**
     * Chronological order: a deadline by when it is due, an event by when it
     * starts, and a todo -- which has no date at all -- after every task that
     * has one.
     */
    DATE("date", Comparator.comparing(task -> task.getSortDateTime().orElse(LocalDateTime.MIN))),

    /**
     * Alphabetical order by description, ignoring the difference between capital
     * and small letters, so "Read Book" sits beside "read book" instead of in a
     * block of its own.
     */
    NAME("name", Comparator.comparing(task -> task.getDescription().toLowerCase(Locale.ROOT))),

    /** Todos, then deadlines, then events -- the order {@link TaskType} declares them in. */
    TYPE("type", Comparator.comparing(Task::getType)),

    /**
     * Unfinished work first, so what is left to do is at the top.
     * Like {@code date}, {@code name} and {@code type}, the keyword names what is
     * compared rather than which tasks come first: the old {@code sort done} read
     * as "done tasks first", the opposite of what it did.
     */
    STATUS("status", Comparator.comparing(Task::isDone));

    /**
     * Sorts every dated task before every task without a date.
     *
     * <p>It is never reversed: {@link #getComparator} applies the direction to
     * the order <em>within</em> each group and leaves the grouping itself alone,
     * so todos stay at the bottom whichever way the dates run. "No date" is read
     * as a task having none, rather than as a date at one end of time.</p>
     *
     * <p>A {@code Boolean} sorts {@code false} before {@code true}, so asking
     * whether the date is <em>missing</em> puts the tasks that have one
     * first.</p>
     */
    private static final Comparator<Task> DATED_BEFORE_DATELESS =
            Comparator.comparing(task -> task.getSortDateTime().isEmpty());

    /** The word the user types for this key. */
    private final String keyword;

    /**
     * This key's own order, smallest first.
     * {@link SortDirection#DESC} turns it round, so each constant states its
     * order once rather than once per direction.
     */
    private final Comparator<Task> ascendingOrder;

    /**
     * Creates a key from the word the user types for it and the order it names.
     * An enum's constructor is implicitly private, so the constants declared
     * above are the only instances there will ever be.
     *
     * @param keyword        The word, e.g. {@code "date"}.
     * @param ascendingOrder The order it stands for, smallest first.
     */
    SortKey(String keyword, Comparator<Task> ascendingOrder) {
        this.keyword = keyword;
        this.ascendingOrder = ascendingOrder;
    }

    /**
     * Returns the word the user types for this key, for use in a reply such as
     * {@code "Here are your tasks, sorted by date:"}.
     *
     * @return The keyword, e.g. {@code "date"}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the order to sort by, ready to be handed to {@code List.sort}.
     *
     * <p>{@link #DATE} is the one key that needs more than its own order,
     * because it is the one key some tasks cannot answer: a todo has no date,
     * while every task has a description, a type and a done status. Grouping the
     * dateless tasks out first is what lets the date comparison never have to
     * wonder what to do about a missing date -- by the time two tasks are
     * compared on their dates, both have one.</p>
     *
     * <p>Nothing here breaks a tie. Two tasks that compare equal keep the order
     * they were already in, because {@code List.sort} is stable, and that is
     * what lets one sort refine another.</p>
     *
     * @param direction Whether this key's order runs forwards or backwards.
     * @return The comparator that puts the tasks in that order.
     */
    public Comparator<Task> getComparator(SortDirection direction) {
        Comparator<Task> ordered = direction.applyTo(ascendingOrder);
        if (this == DATE) {
            return DATED_BEFORE_DATELESS.thenComparing(ordered);
        }
        return ordered;
    }

    /**
     * Returns the key the given word names.
     * The match is exact and lower case, as it is for every command word, so
     * {@code Date} is refused rather than taken for {@code date}.
     *
     * @param keyword The word typed after {@code sort}.
     * @return The key it names.
     * @throws PiplupBotException If no key is spelled that way.
     */
    public static SortKey fromKeyword(String keyword) throws PiplupBotException {
        return Arrays.stream(values())
                .filter(key -> key.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new PiplupBotException(
                        "Pip... I don't know how to sort by \"" + keyword + "\".",
                        getKeywordHint()));
    }

    /**
     * Returns the hint listing every key, e.g.
     * {@code "Try: date, name, type, or status."}.
     * A line naming a key that does not exist and a line naming none at all -- a
     * bare {@code sort} -- are answered with the same hint, so it is built here
     * rather than in either of the two places that show it.
     *
     * @return The line shown to a user whose key could not be read.
     */
    public static String getKeywordHint() {
        return "Try: " + buildKeywordList() + ".";
    }

    /**
     * Returns every keyword, listed the way the hint reads them, e.g.
     * {@code "date, name, type, or status"}.
     * Built from {@link #values()} for the reason
     * {@link piplupbot.command.CommandWord CommandWord} builds its own that way:
     * a newly added key appears in the hint by itself, so the hint cannot fall
     * out of step with the keys it advertises.
     *
     * @return The keywords in declaration order, separated by commas.
     */
    private static String buildKeywordList() {
        SortKey[] keys = values();
        assert keys.length > 1 : "buildKeywordList() assumes at least two keys to list";

        // The last keyword is held back and added with its "or", so everything
        // before it is a plain comma-separated join.
        String exceptLast = Arrays.stream(keys, 0, keys.length - 1)
                .map(key -> key.keyword)
                .collect(Collectors.joining(", "));
        return exceptLast + ", or " + keys[keys.length - 1].keyword;
    }
}
