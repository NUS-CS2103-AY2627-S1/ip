package piplupbot.task;

import java.util.Arrays;

import piplupbot.PiplupBotException;
import piplupbot.Storage;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the kinds of task the bot stores, one constant for each.
 *
 * <p>Two facts about each kind used to be written down twice. The letter that
 * names it in the save file was returned by the subclass and spelled out again
 * as a {@code case} label in {@link Storage}; and how many fields its lines
 * carry appeared only in {@code Storage}, as the bare numbers 3, 4 and 5.
 * Nothing tied the copies together, so a fourth kind of task would have been
 * saved happily and then refused on the next run as an unknown type -- losing
 * the task in silence, which is the one thing the save file exists to
 * prevent.</p>
 *
 * <p>Holding both facts here makes a kind of task a type rather than a loose
 * letter, which lets {@code Storage} {@code switch} over these constants. A
 * {@code switch} expression over an enum must cover every one of them, so adding
 * a kind of task now stops the compiler until the reading side has been taught
 * about it. That is the same arrangement {@link piplupbot.command.CommandWord
 * CommandWord} already uses for the words a user types.</p>
 */
public enum TaskType {
    /** A task with no date attached, e.g. {@code visit new theme park}. */
    TODO("T", 0),

    /** A task with a due date, written as one field after the shared ones. */
    DEADLINE("D", 1),

    /** A task with a start and an end, written in that order. */
    EVENT("E", 2);

    /** The letter that names this kind of task in the save file. */
    private final String code;

    /**
     * How many fields of its own this kind of task is saved with, beyond the
     * type code, the done flag and the description that every task shares.
     */
    private final int extraFieldCount;

    /**
     * Creates a kind of task from the letter that names it in the save file.
     * An enum's constructor is implicitly private, so the constants declared
     * above are the only instances there will ever be.
     *
     * @param code            The letter, e.g. {@code "D"}.
     * @param extraFieldCount How many fields this kind adds to the shared three.
     */
    TaskType(String code, int extraFieldCount) {
        this.code = code;
        this.extraFieldCount = extraFieldCount;
    }

    /**
     * Returns the letter that names this kind of task in the save file.
     *
     * @return The letter, e.g. {@code "D"}.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns how many fields of its own this kind of task is saved with.
     * {@link Storage} adds this to the number of shared fields to know how long
     * a line of this kind should be, so the count lives beside the letter it
     * belongs to rather than being spelled out again at the reading end.
     *
     * @return The number of extra fields, e.g. 2 for an event.
     */
    public int getExtraFieldCount() {
        return extraFieldCount;
    }

    /**
     * Returns the kind of task the given letter names.
     * The match is exact, so a hand-edited {@code t} is refused rather than
     * taken for a todo: everything this program writes is upper case, and
     * guessing at anything else risks rebuilding a task the user never had.
     *
     * @param code The letter read from a saved line.
     * @return The kind of task it names.
     * @throws PiplupBotException If no kind of task uses that letter, so that
     *                            the line is skipped rather than guessed at.
     */
    public static TaskType fromCode(String code) throws PiplupBotException {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new PiplupBotException("Unknown task type: " + code));
    }
}
