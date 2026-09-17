package harvey.command;

import java.util.Optional;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.Deadline;
import harvey.task.Event;
import harvey.task.Task;
import harvey.task.TaskList;
import harvey.task.Todo;
import harvey.ui.Ui;
/**
 * Adds a new task to the list.
 * Covers {@code todo}, {@code deadline} and {@code event}, which differ only in how the
 * task is built from what the user typed.
 */
public class AddCommand extends Command {
    /**
     * Character reserved for separating fields in the save file.
     * Task text containing it could not be read back, so it is refused on the way in.
     */
    private static final String RESERVED_CHARACTER = "|";

    /** Separator that introduces the due date of a deadline. */
    private static final String OPTION_BY = "/by";

    /** Separator that introduces the start time of an event. */
    private static final String OPTION_FROM = "/from";

    /** Separator that introduces the end time of an event. */
    private static final String OPTION_TO = "/to";
    /** Which of the three task-creating instructions this is. */
    private final CommandType type;

    /** Everything the user typed after the command word. */
    private final String argument;

    /**
     * Creates a command that will add one task.
     *
     * @param type     the instruction typed, one of {@code TODO}, {@code DEADLINE} or {@code EVENT}.
     * @param argument the description and any dates, as typed.
     */
    public AddCommand(CommandType type, String argument) {
        this.type = type;
        this.argument = argument;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        Task task = createTask(type, argument);
        requireNotDuplicate(task, tasks, ui);
        if (task instanceof Event event) {
            requireNoClash(event, tasks, ui);
        }
        tasks.add(task);

        // Saved before the reply is returned, so that a failure to write the file is
        // reported as an error instead of being hidden behind a cheerful confirmation.
        storage.save(tasks.asList());
        return ui.formatLines("Consider it filed:",
                "  " + task,
                ui.formatTaskCount(tasks.size()));
    }

    /**
     * Checks that a task about to be added is not a copy of one already stored.
     * <p>
     * Checked before the clash check, because an event identical to a stored one also
     * clashes with it, and "you already have this" is the more accurate thing to say.
     *
     * @param task  the task being added.
     * @param tasks the tasks already stored.
     * @param ui    the source of the reply format, used to lay the message out.
     * @throws HarveyException if a task with the same details is already in the list.
     */
    private static void requireNotDuplicate(Task task, TaskList tasks, Ui ui) throws HarveyException {
        Optional<Task> duplicate = tasks.findDuplicate(task);
        if (duplicate.isEmpty()) {
            return;
        }

        int duplicateNumber = tasks.asList().indexOf(duplicate.get()) + 1;
        // A finished copy is the one case where adding again is understandable, so the
        // user is pointed at the command that does what they most likely want.
        String advice = duplicate.get().isDone()
                ? "Nothing was added. It's closed; use unmark " + duplicateNumber + " to reopen it."
                : "Nothing was added.";
        throw new HarveyException(ui.formatLines(
                "You already have that on the docket, as task " + duplicateNumber + ":",
                "  " + duplicate.get(),
                advice));
    }

    /**
     * Checks that an event about to be added does not collide with one already stored.
     * <p>
     * Called before the task is added and before anything is saved, so a refused event
     * leaves both the list and the file exactly as they were. A tutor cannot teach two
     * students at once, so a collision is treated as a mistake to correct rather than a
     * warning to read and ignore.
     *
     * @param event the event being added.
     * @param tasks the tasks already stored.
     * @param ui    the source of the reply format, used to lay the message out.
     * @throws HarveyException if the event overlaps one already in the list.
     */
    private static void requireNoClash(Event event, TaskList tasks, Ui ui) throws HarveyException {
        Optional<Event> clash = tasks.findClash(event);
        if (clash.isEmpty()) {
            return;
        }

        // The task number is what the user needs to delete or inspect the other event,
        // and is the number list shows, so it counts from 1.
        int clashingNumber = tasks.asList().indexOf(clash.get()) + 1;
        throw new HarveyException(ui.formatLines(
                "That clashes with an event you already have:",
                "  " + clash.get(),
                "Nothing was added. Pick a different time, or delete task "
                        + clashingNumber + " first."));
    }

    /**
     * Builds the task described by an {@code todo}, {@code deadline} or {@code event} command.
     * The returned object is a {@link Todo}, {@link Deadline} or {@link Event}, but the
     * declared return type is {@code Task} so that the caller can store any of them in the
     * same array without caring which kind it is. This is polymorphism at work: the caller
     * later calls {@code toString()} on the stored task and each subclass supplies its own
     * version.
     *
     * @param command  the command that was typed, one of {@link CommandType#TODO},
     *                 {@link CommandType#DEADLINE} or {@link CommandType#EVENT}.
     * @param argument everything typed after the command word.
     * @return the new task.
     * @throws HarveyException if the description or any required date is missing.
     */
    private static Task createTask(CommandType command, String argument) throws HarveyException {
        assert argument != null : "Parser.parseArgument() returns an empty string when there is no argument";
        if (argument.isEmpty()) {
            // "event" starts with a vowel, so it needs "An" rather than "A".
            String article = (command == CommandType.EVENT) ? "An " : "A ";
            throw new HarveyException(article + command.getKeyword() + " needs a description. "
                    + "For example: " + command.getExample());
        }

        // Checked once here, before the argument is split up, so it covers the description
        // and every date field of all three task types.
        if (argument.contains(RESERVED_CHARACTER)) {
            throw new HarveyException("Please leave out the \"" + RESERVED_CHARACTER
                    + "\" character. I use it to separate fields when saving your tasks, "
                    + "so a task containing it could not be loaded back.");
        }

        switch (command) {
            case TODO:
                return new Todo(argument);
            case DEADLINE:
                requireAtMostOnce(argument, OPTION_BY, command);
                // "return book /by Sunday" splits into "return book" and "Sunday".
                String[] parts = splitAtOption(argument, OPTION_BY,
                        "A deadline needs a due date after " + OPTION_BY + ". For example: "
                                + command.getExample());
                // parseDate rejects anything that is not a real date, so a Deadline can never
                // be built holding text that only looks like one.
                return new Deadline(parts[0], Deadline.parseDate(parts[1]));
            case EVENT:
                // An event needs two separators, so split at "/from" first and then at "/to".
                String eventHelp = "An event needs a start after " + OPTION_FROM + " and an end after "
                        + OPTION_TO + ". For example: " + command.getExample();
                requireAtMostOnce(argument, OPTION_FROM, command);
                requireAtMostOnce(argument, OPTION_TO, command);
                requireInOrder(argument, OPTION_FROM, OPTION_TO, command);
                String[] fromParts = splitAtOption(argument, OPTION_FROM, eventHelp);
                String[] toParts = splitAtOption(fromParts[1], OPTION_TO, eventHelp);
                return new Event(fromParts[0], Event.parseDateTime(toParts[0]),
                        Event.parseDateTime(toParts[1]));
            default:
                // Parser sends only the three commands named above here, so reaching this
                // means a fourth was routed here without being given a task to build.
                throw new HarveyException(command.getKeyword() + " does not create a task.");
        }
    }

    /**
     * Refuses an option given more than once, such as two {@code /by} dates.
     * <p>
     * Without this, the first occurrence is split at and everything after it, including
     * the second option, is read as one date, which fails with a message about the date
     * rather than about the real mistake.
     *
     * @param text    the text typed after the command word.
     * @param option  the option that may appear at most once.
     * @param command the command typed, whose example is shown.
     * @throws HarveyException if the option appears twice or more.
     */
    private static void requireAtMostOnce(String text, String option, CommandType command)
            throws HarveyException {
        int first = text.indexOf(option);
        if (first >= 0 && text.indexOf(option, first + option.length()) >= 0) {
            throw new HarveyException("You gave " + option + " more than once. Give it exactly once, "
                    + "for example: " + command.getExample());
        }
    }

    /**
     * Refuses two options given in the wrong order, such as {@code /to} before {@code /from}.
     *
     * @param text    the text typed after the command word.
     * @param earlier the option that must come first.
     * @param later   the option that must come second.
     * @param command the command typed, whose example is shown.
     * @throws HarveyException if both options are present and {@code later} comes first.
     */
    private static void requireInOrder(String text, String earlier, String later, CommandType command)
            throws HarveyException {
        int earlierPosition = text.indexOf(earlier);
        int laterPosition = text.indexOf(later);
        if (earlierPosition >= 0 && laterPosition >= 0 && laterPosition < earlierPosition) {
            throw new HarveyException("Put " + earlier + " before " + later + ", for example: "
                    + command.getExample());
        }
    }

    /**
     * Splits text at the first occurrence of an option such as {@code /by}.
     *
     * @param text            the text to split.
     * @param option          the option to split at.
     * @param errorMessage    the explanation to show the user if the split is not possible.
     * @return the text before and after the option.
     * @throws HarveyException if the option is absent or either side of it is empty.
     */
    private static String[] splitAtOption(String text, String option, String errorMessage)
            throws HarveyException {
        int optionPosition = text.indexOf(option);
        if (optionPosition < 0) {
            throw new HarveyException(errorMessage);
        }

        String before = text.substring(0, optionPosition).trim();
        String after = text.substring(optionPosition + option.length()).trim();
        if (before.isEmpty() || after.isEmpty()) {
            throw new HarveyException(errorMessage);
        }
        return new String[] {before, after};
    }
}
