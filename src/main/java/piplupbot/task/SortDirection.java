package piplupbot.task;

import java.util.Arrays;
import java.util.Comparator;

import piplupbot.PiplupBotException;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents which way round a sort runs: {@code asc} for smallest first,
 * {@code desc} for largest first.
 *
 * <p>A direction is a word the user types, so it needs the same three things
 * {@link piplupbot.command.CommandWord CommandWord} needs -- the spelling to
 * match, a way to refuse anything else, and a hint naming what would have
 * worked. That is why it is an enum rather than the {@code boolean} a caller
 * could pass just as easily: {@link piplupbot.command.MarkCommand MarkCommand}
 * carries a boolean because nobody types it, while this is read off the line.</p>
 *
 * <p>What each direction <em>means</em> is left to {@link SortKey}, which owns
 * the order being turned round. This class knows only how to turn one round.</p>
 */
public enum SortDirection {
    /**
     * Smallest first, which is what each key calls its natural order: the
     * earliest date, {@code a} before {@code z}, a todo before an event, and
     * unfinished work before finished.
     */
    ASC("asc"),

    /** The same order read backwards. */
    DESC("desc");

    /** The word the user types for this direction. */
    private final String keyword;

    /**
     * Creates a direction from the word the user types for it.
     * An enum's constructor is implicitly private, so the two constants above
     * are the only instances there will ever be.
     *
     * @param keyword The word, e.g. {@code "desc"}.
     */
    SortDirection(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types for this direction.
     *
     * @return The keyword, e.g. {@code "desc"}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the given order as this direction asks for it.
     *
     * <p>Only the comparator handed in is turned round. {@link SortKey#DATE}
     * depends on that: the rule that puts dateless tasks last is applied outside
     * this method, so it survives a {@code desc} that reverses everything
     * else.</p>
     *
     * @param ascendingOrder The key's own order, smallest first.
     * @return That order, reversed if this direction is {@link #DESC}.
     */
    Comparator<Task> applyTo(Comparator<Task> ascendingOrder) {
        return this == ASC ? ascendingOrder : ascendingOrder.reversed();
    }

    /**
     * Returns the direction the given word names.
     * The match is exact and lower case, as it is for every other word the bot
     * reads: {@code DESC} and {@code descending} are refused rather than guessed
     * at.
     *
     * @param keyword The word typed after the sort key.
     * @return The direction it names.
     * @throws PiplupBotException If no direction is spelled that way.
     */
    public static SortDirection fromKeyword(String keyword) throws PiplupBotException {
        return Arrays.stream(values())
                .filter(direction -> direction.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new PiplupBotException(
                        "Pip... I don't know the sort direction \"" + keyword + "\".",
                        getKeywordHint()));
    }

    /**
     * Returns the hint that names both directions, e.g. {@code "Try: asc or desc."}.
     *
     * <p>The two are named here rather than gathered from {@link #values()} the
     * way {@link SortKey}'s hint is, because a sort has exactly two directions
     * and always will. Building a list from two constants would also read
     * "asc, or desc", with a comma a two-item list does not want.</p>
     *
     * @return The line shown to a user whose direction could not be read.
     */
    public static String getKeywordHint() {
        return "Try: " + ASC.keyword + " or " + DESC.keyword + ".";
    }
}
