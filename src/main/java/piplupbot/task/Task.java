package piplupbot.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import piplupbot.Storage;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents a single task in the user's list: what the task is, and whether
 * it is done.
 * Keeping the two together means they can no longer fall out of step,
 * which was a risk while they lived in two separate arrays.
 *
 * <p>This is also the base class for the three kinds of task the bot supports:
 * {@link Todo}, {@link Deadline} and {@link Event}. Everything they have in
 * common lives here, so the bot can hold them all in one list and call
 * {@code toString()} without knowing which kind it is holding.</p>
 *
 * <p>It is {@code abstract} because "a task" on its own is not something the
 * user can add: every task the bot stores is a todo, a deadline or an event.
 * Declaring it so lets the compiler say the same thing, and lets this class
 * call {@link #getTypeLabel()} knowing some subclass must have answered it.</p>
 */
public abstract class Task {
    /**
     * What the user typed, e.g. {@code read book}.
     *
     * <p>It is {@code private}, not {@code protected}: no subclass reads it.
     * {@link Todo}, {@link Deadline} and {@link Event} hand their description to
     * this class's constructor and then leave it alone, so opening the field up
     * to them would buy nothing and cost the guarantee below.</p>
     *
     * <p>It is {@code final} because the constructor is the one place that checks
     * a description is not blank. A field a subclass could reassign afterwards
     * would make that check a hope rather than a promise -- and a blank
     * description is one this bot would save happily and then refuse to load
     * back, losing the task.</p>
     */
    private final String description;

    /**
     * Whether the task has been completed.
     * {@code private} for the same reason as the description, but not
     * {@code final}: {@link #markAsDone} and {@link #markAsNotDone} are the two
     * ways it is meant to change.
     */
    private boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description What the task is.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "A task was created with no description";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Reports whether this task's description contains the given text, ignoring
     * the difference between capital and small letters.
     *
     * <p>The question is asked of the task rather than answered by a caller
     * reading a description out of it, so what counts as a match is decided in
     * one place, beside the field it is decided about. That matters because the
     * rule is not the obvious one: the search is deliberately more forgiving
     * than the rest of the bot, matching part of a word and ignoring capitals,
     * so that {@code find boo} and {@code find BOOK} both find "read book".
     * Someone hunting for a task they half remember should not have to type it
     * exactly.</p>
     *
     * <p>Only the description is searched, never the dates a deadline or an
     * event adds, so {@code find oct} does not match a task merely due in
     * October. Searching the whole rendered line would be one character's
     * change here and would quietly make every date, type label and status box
     * searchable too.</p>
     *
     * <p>{@code Locale.ROOT} rather than the machine's own language, for the
     * reason {@link DateTimes} pins {@code Locale.ENGLISH}: a Turkish machine
     * lower-cases "I" to a dotless letter, which would make a keyword stop
     * matching a description that plainly contains it.</p>
     *
     * @param text What to look for, as the user typed it.
     * @return {@code true} if the description contains it.
     */
    public boolean descriptionContains(String text) {
        return description.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns what the user typed, e.g. {@code read book}.
     *
     * <p>Package-private rather than public: it exists so that
     * {@link SortKey#NAME} can order tasks by their descriptions, not so that
     * the rest of the program can read a description out of a task. A task
     * shows its own description through {@link #toString()} and hands it over
     * for saving through {@link #toFileFields()}, which is how everything
     * outside this package gets at it.</p>
     *
     * @return The description, never blank.
     */
    String getDescription() {
        return description;
    }

    /**
     * Reports whether the task has been completed.
     * {@link #getStatusIcon()} answers the same question for the screen, but in
     * the screen's own words; ordering tasks by {@code " "} before {@code "X"}
     * would tie {@link SortKey#STATUS}'s order to that wording, so that a change
     * to the status box would quietly change the sort.
     *
     * <p>It is public because {@code mark} and {@code unmark} ask it too, to
     * notice a task that already has the status they were asked to set.</p>
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date this task is sorted by, or nothing if it has none.
     *
     * <p>A todo has no date at all, so the answer here is
     * {@link Optional#empty()} and every {@link Todo} inherits it;
     * {@link Deadline} answers with its due date and {@link Event} with its
     * start. Asking the task is the whole of the rule {@link SortKey#DATE}
     * needs: the comparator never has to test which kind of task it is holding,
     * any more than printing one does.</p>
     *
     * <p>An {@link Optional} rather than {@code null}, so that "this task has no
     * date" is something the type says out loud and a caller cannot forget to
     * check for.</p>
     *
     * @return The date to sort by, or empty for a task that has none.
     */
    Optional<LocalDateTime> getSortDateTime() {
        return Optional.empty();
    }

    /**
     * Returns every date this task carries, in the order it shows them.
     *
     * <p>A todo has none, so the answer here is an empty list, which every
     * {@link Todo} inherits. This is not the question
     * {@link #getSortDateTime()} answers: an event is sorted by its start alone,
     * but two events are the same event only if they also end together.</p>
     *
     * @return The dates, possibly none.
     */
    List<LocalDateTime> getDateTimes() {
        return List.of();
    }

    /**
     * Reports whether this task and another stand for the same piece of work:
     * the same kind of task, the same description and the same dates.
     *
     * <p>Two differences are ignored on purpose. Capitals are, as they are by
     * {@code find} and {@code sort name}, because "Read Book" and "read book"
     * are one task typed two ways. And so is whether either task is done:
     * finishing a task does not make it a different one, and {@code unmark} is
     * the way to take up a finished task again.</p>
     *
     * <p>This is a method of its own rather than an override of
     * {@code equals}, which every list and test uses to compare tasks. There,
     * "equal, although only one of them is done" would be a surprise.</p>
     *
     * @param other The task to compare this one with.
     * @return {@code true} if the two tasks have the same details.
     */
    boolean isDuplicateOf(Task other) {
        return getType() == other.getType()
                && description.equalsIgnoreCase(other.description)
                && getDateTimes().equals(other.getDateTimes());
    }

    /**
     * Returns the character shown inside the status box in the task list.
     *
     * @return {@code "X"} if the task is done, or a single space if it is not.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Records that the task has been completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Records that the task has not been completed after all. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns which kind of task this is.
     * Each subclass answers for itself, which is why this class does not need
     * to store the kind or ask what it is: calling this method on a
     * {@code Task} reaches the right subclass's answer on its own.
     *
     * <p>The kind is the one piece of type information a subclass has to supply,
     * because both the label shown on screen and the code written to the save
     * file are built from it. Keeping one source for the two means they cannot
     * drift apart.</p>
     *
     * @return The kind of task, e.g. {@link TaskType#TODO}.
     */
    protected abstract TaskType getType();

    /**
     * Returns the label that says which kind of task this is, as the task list
     * displays it.
     *
     * @return The type code in square brackets, e.g. {@code "[T]"}.
     */
    protected String getTypeLabel() {
        return "[" + getType().getCode() + "]";
    }

    /**
     * Returns the fields that describe this task in the save file.
     *
     * <p>The task hands over its parts as plain text and leaves {@link Storage}
     * to join them into a line. That division matters: a description may contain
     * any character at all, including the one the file uses to separate fields,
     * and protecting it is the file format's problem rather than the task's. A
     * task that built its own line would have to know about separators and
     * escaping, and every subclass would have to get that right again.</p>
     *
     * <p>The done status is given as {@code 1} or {@code 0} rather than
     * {@code true}/{@code false}, following the format in the requirements.</p>
     *
     * @return The type code, the done status and the description.
     */
    public String[] toFileFields() {
        return buildFileFields();
    }

    /**
     * Returns the fields every task begins with, followed by whatever the
     * subclass adds. Having the shared three in one place means a subclass
     * cannot accidentally write them in a different order.
     *
     * @param extras The subclass's own fields, in the order they are written.
     * @return The shared fields followed by {@code extras}.
     */
    protected String[] buildFileFields(String... extras) {
        // The parts are added in order rather than assigned to numbered slots,
        // so this method says which shared fields there are and which comes
        // first without also having to state how many -- a count only the
        // reading end needs, and which Storage now keeps on its own.
        List<String> fields = new ArrayList<>();
        fields.add(getType().getCode());
        fields.add(isDone ? "1" : "0");
        fields.add(description);
        fields.addAll(List.of(extras));
        return fields.toArray(new String[0]);
    }

    /**
     * Returns the task the way the task list displays it, e.g. {@code [T][X] read book}.
     * Java calls this automatically whenever a Task is used where text is expected,
     * such as in string concatenation.
     *
     * <p>Deciding the order -- label, status box, description -- here rather
     * than in each subclass means the three kinds of task cannot drift into
     * three different shapes; a subclass supplies only its own label, plus
     * anything peculiar to it such as a deadline's due date.</p>
     *
     * @return The type label, the status box and the description.
     */
    @Override
    public String toString() {
        return getTypeLabel() + "[" + getStatusIcon() + "] " + description;
    }
}
