package bob.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import bob.BobException;

/**
 * Represents a single task the chatbot remembers: what the user wants to do,
 * and whether it has been done yet.
 *
 * <p>This class holds only what every kind of task has in common. The kinds the
 * chatbot supports — {@link Todo}, {@link Deadline} and {@link Event} — extend it
 * and add whatever is particular to them, so the shared parts are written once
 * here instead of being repeated three times.
 *
 * <p>It is {@code abstract} because "a task" on its own is not something the user
 * can add: every task the chatbot stores is one of the three kinds. Declaring it
 * abstract has the compiler enforce that, rather than leaving it to be remembered.
 *
 * <p>What a task is made of is private to this class, and the subclasses reach it
 * only through the methods below, which is all they need: {@link Deadline} and
 * {@link Event} add dates of their own, and no kind of task sets the description
 * or the done status itself. Keeping the fields private is what lets this class
 * promise that a task's description is the one it was made with, and that its
 * status only ever changes through {@link #markAsDone()} and
 * {@link #markAsNotDone()}.
 */
public abstract class Task {

    /** Status field written to the save file for a task that has been done. */
    public static final String DONE_FLAG = "1";

    /** Status field written to the save file for a task that has not been done yet. */
    public static final String NOT_DONE_FLAG = "0";

    /**
     * What the user typed when adding the task, or when they last changed its description.
     *
     * <p>{@code final}, because a task is never reworded in place: an edit builds a
     * new task holding the new description, through {@link #withEdit}. Saying so here
     * means the compiler refuses the assignment rather than a reader having to check
     * every method for one.
     */
    private final String description;

    /** Whether the task has been marked as done. */
    private boolean isDone;

    /**
     * Creates a task that is not done yet, since a task the user has just
     * mentioned is something still to do.
     *
     * @param description what the user typed when adding the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single character shown inside the first box of a listing,
     * identifying which kind of task this is: {@code T}, {@code D} or {@code E}.
     *
     * <p>Each subclass answers for itself, and {@link #toString()} calls this
     * without knowing which subclass it is talking to. That is polymorphism doing
     * the work: the listing code stays the same however many kinds of task exist.
     */
    public abstract String getTypeIcon();

    /**
     * Returns the date this task is pinned to, or an empty {@link Optional} for a
     * task that is not pinned to any date.
     *
     * <p>This is what lets {@link bob.command.CommandWord#ON CommandWord.ON},
     * {@link bob.command.CommandWord#BEFORE CommandWord.BEFORE},
     * {@link bob.command.CommandWord#AFTER CommandWord.AFTER} and
     * {@link bob.command.CommandWord#NEXT CommandWord.NEXT} work through one list holding all three
     * kinds of task without asking what kind each one is: a
     * {@link Deadline} answers with its due date, an {@link Event} with its start,
     * and a {@link Todo} accepts this default and answers that it has none.
     *
     * <p>An {@code Optional} is returned rather than {@code null} so that a caller
     * cannot forget the dateless case, exactly as in
     * {@link bob.command.CommandWord#parse CommandWord.parse}.
     */
    public Optional<TaskDateTime> getScheduledDate() {
        return Optional.empty();
    }

    /**
     * Returns whether this task falls on {@code day}.
     *
     * <p>A task with no date never does. A task with one does when its date is on
     * that day; {@link Event} widens this to the whole stretch it runs for.
     */
    public boolean occursOn(LocalDate day) {
        return getScheduledDate().filter(date -> date.isOn(day)).isPresent();
    }

    /**
     * Returns whether this task falls on a day earlier than {@code day}.
     *
     * <p>A task with no date never does, so a todo is left out of a listing of
     * what is coming up rather than being counted as overdue since the beginning
     * of time.
     */
    public boolean isBefore(LocalDate day) {
        return getScheduledDate().filter(date -> date.isBefore(day)).isPresent();
    }

    /**
     * Returns whether this task falls on a day later than {@code day}.
     *
     * <p>The mirror image of {@link #isBefore}, and dateless tasks are left out of
     * both for the same reason. A task is placed by the one date it is pinned to,
     * so an event is placed by its start: an event already running on {@code day}
     * is not still to come, and {@link #occursOn} is what finds that one.
     */
    public boolean isAfter(LocalDate day) {
        return getScheduledDate().filter(date -> date.isAfter(day)).isPresent();
    }

    /**
     * Returns whether this task's description contains {@code keyword}.
     *
     * <p>Case and the spaces between words are ignored, so a user searching for
     * {@code Book} still finds a task they wrote as {@code read book}, and
     * {@code find sports club} finds one typed as {@code sports  club}. Comparing
     * the text exactly as typed would be marginally simpler, but it would make a
     * search fail for the two reasons a user is least likely to think of. Extra
     * spaces are ignored by {@link #isSameTaskAs} as well, so a search cannot miss a
     * task that adding it a second time would be refused for matching.
     *
     * <p>The keyword is looked for anywhere in the description rather than as a
     * whole word of its own, so {@code book} finds {@code bookshop} too. That is
     * what makes a partial word worth typing: a whole-word search would need the
     * description split into words first, and would then refuse to find the very
     * tasks a user typing half a word is reaching for.
     *
     * <p>Unlike the date questions above, every kind of task answers this the same
     * way, because every task has a description. There is nothing here for a
     * subclass to override.
     *
     * @param keyword the text to look for, as the user typed it.
     */
    public boolean matchesKeyword(String keyword) {
        return normalizeForMatching(description).contains(normalizeForMatching(keyword));
    }

    /**
     * Returns whether {@code other} is the same task as this one, written again.
     *
     * <p>Two tasks are the same when they are the same kind, carry the same dates, and
     * have descriptions that differ at most in capital letters and in the spaces
     * between words: {@code Read  Book} is {@code read book} typed a second time, and
     * the user gains nothing from holding both. Whether either has been done is
     * ignored, since marking a task done does not make it a different task.
     *
     * <p>Only the comparison is loose. Each task keeps its description as it was typed.
     *
     * <p>A method with a name of its own rather than an override of {@code equals}.
     * An equality that ignored capitals and the done status is not what {@code equals}
     * normally means, and overriding it would bring {@code hashCode} along with it.
     *
     * @param other the task to compare this one with.
     */
    public boolean isSameTaskAs(Task other) {
        return getClass() == other.getClass()
                && normalizeForMatching(description).equals(normalizeForMatching(other.description))
                && hasSameDatesAs(other);
    }

    /**
     * Returns whether {@code other}, a task of the same kind as this one, carries the
     * same dates.
     *
     * <p>A task with no dates has none to differ in, so this default answers
     * {@code true}. {@link Deadline} and {@link Event} override it to compare the dates
     * they add, which keeps {@link #isSameTaskAs} from having to ask which kind of task
     * it holds.
     *
     * @param other a task of the same class as this one.
     */
    protected boolean hasSameDatesAs(Task other) {
        return true;
    }

    /**
     * Returns text in the form descriptions and keywords are compared in: with no
     * spaces around it, one space between words, and no capital letters.
     *
     * <p>Used by {@link #isSameTaskAs} and {@link #matchesKeyword} alike, so that the
     * two cannot disagree about which differences between two pieces of text matter.
     */
    private static String normalizeForMatching(String text) {
        // Locale.ROOT rather than the machine's own locale, so that lowercasing
        // means the same thing wherever the chatbot is run.
        return text.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the single character shown inside the status box of a listing:
     * {@code X} for a task that is done, a space for one that is not.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Returns whether the task has been marked as done. */
    public boolean isDone() {
        return isDone;
    }

    /** Records that the task has been done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Records that the task is not done after all. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns a copy of this task with the changes in {@code edit} made to it,
     * leaving this task as it is.
     *
     * <p>A new task is built rather than this one being changed, so that the fields
     * of every kind of task can stay {@code final}. That matters most for an
     * {@link Event}, whose start and end are only meaningful as a pair: a pair that
     * is replaced whole can be checked whole.
     *
     * <p>What every kind of task shares is dealt with here, once: the copy has the
     * new description if the edit gives one and the old one if not, and it is done
     * if this task was done. What differs between the kinds — which dates they have,
     * and which changes make no sense for them — is left to {@link #buildEdited}.
     *
     * @param edit the changes to make.
     * @return the edited copy, of the same kind as this task and with the same done status.
     * @throws BobException if the edit changes a date this kind of task does not
     *                      have, or would leave an event ending before it starts.
     */
    public final Task withEdit(TaskEdit edit) throws BobException {
        Task edited = buildEdited(Objects.requireNonNullElse(edit.description(), description), edit);
        if (isDone) {
            edited.markAsDone();
        }
        return edited;
    }

    /**
     * Returns a new task of this kind holding {@code description}, with the dates
     * {@code edit} changes and this task's own dates for the rest.
     *
     * <p>The copy is returned not done; {@link #withEdit} carries the done status over.
     *
     * @param description the description the copy is to have, already settled.
     * @param edit        the changes asked for, whose description has already been dealt with.
     * @return the copy.
     * @throws BobException if {@code edit} changes a date this kind of task does not
     *                      have, or leaves the copy's dates impossible.
     */
    protected abstract Task buildEdited(String description, TaskEdit edit) throws BobException;

    /**
     * Returns the task as the list of fields that {@link bob.storage.Storage Storage} writes to the
     * save file: the kind of task, whether it has been done, and what it is.
     *
     * <p>Subclasses that remember more than that — a deadline's due date, an
     * event's start and end — override this and append to
     * {@code super.toSaveFields()}, exactly as they do for {@link #toString()},
     * so the three shared fields are listed in one place only.
     *
     * <p>The fields are returned separately rather than as one finished line of
     * text. Joining them, and protecting a field that itself contains the
     * separator, is then left to {@link bob.storage.Storage Storage}, which is the one class that
     * knows the layout of the file.
     */
    public List<String> toSaveFields() {
        List<String> fields = new ArrayList<>();
        fields.add(getTypeIcon());
        fields.add(isDone ? DONE_FLAG : NOT_DONE_FLAG);
        fields.add(description);
        return fields;
    }

    /**
     * Returns the part of the task's display form that every kind of task shares,
     * for example {@code [T][X] read book}.
     *
     * <p>Subclasses that have something to add, such as a deadline's due date,
     * override this and append to {@code super.toString()}, so the type box,
     * status box and description are formatted in one place only.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
