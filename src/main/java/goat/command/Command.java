package goat.command;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * One instruction from the user, already understood and ready to be carried out.
 *
 * Until now {@link goat.Goat} held a switch over a keyword enum, with one
 * handler method per branch, so adding a command meant editing Goat: a new
 * enum constant, a new switch branch, a new private method. The switch was the
 * kind that keeps growing, and it sat in the class that is meant to be about
 * running the program rather than about any particular command.
 *
 * Each command is now its own class. {@link goat.parser.Parser} decides which one a line
 * asks for and builds it; Goat calls {@link #execute} without knowing or
 * caring which subclass it is holding. Adding a command becomes a matter of
 * writing one new class, and no existing class changes except the one line in
 * Parser that names it.
 *
 * Note the division of labour with the keyword enum inside
 * {@link goat.parser.Parser}: that enum answers "which word did the user
 * type?", which is a parsing question, while a Command answers "what should
 * happen?". Keeping them apart is why the enum was renamed in an earlier
 * commit, and why it is not visible outside its own package.
 *
 * This class is abstract because "a command" on its own is not a thing that
 * can be carried out -- only a specific one is -- and because {@link #execute}
 * has no sensible default. {@link #isExit()} does have one, so it is given a
 * body here and overridden only by {@link ExitCommand}.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * All three collaborators are passed in rather than stored as fields,
     * because a command is a short-lived description of one instruction: it is
     * built, run once and discarded. Taking them as parameters also states
     * plainly that a command is not allowed to reach for anything else.
     *
     * @param tasks   the task list to read or change
     * @param ui      used to tell the user what happened
     * @param storage used to write the list back to disk after a change
     * @throws GoatException if the command cannot be carried out, for example
     *                       because it names a task that does not exist
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException;

    /**
     * Reports whether Goat should stop after this command.
     *
     * Only one command ever says yes, so the default is no and
     * {@link ExitCommand} is the single override.
     *
     * @return true if this command ends the program
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Lays out tasks as a numbered list under a heading.
     *
     * Both {@link ListCommand} and {@link FindCommand} show a set of tasks this
     * way and differ only in the heading and in which tasks they pass, so the
     * numbering lives here rather than being written out twice.
     *
     * Numbering restarts at 1 for whatever is passed in. A search result is
     * numbered 1, 2, 3 even when those tasks sit further down the full list,
     * because the numbers name what is on screen.
     *
     * @param heading the line that introduces the list
     * @param tasks   the tasks to lay out
     * @return the heading followed by one line per task
     */
    protected static String[] formatNumbered(String heading, TaskList tasks) {
        // Stream.concat joins the heading to the numbered lines without an
        // array index to keep in step with a counter. rangeClosed rather than
        // streaming the list directly, because TaskList does not hand out what
        // it wraps.
        return Stream.concat(
                Stream.of(heading),
                IntStream.rangeClosed(1, tasks.size())
                        .mapToObj(taskNumber -> taskNumber + "." + describe(tasks, taskNumber)))
                .toArray(String[]::new);
    }

    /**
     * Returns one task as it should read on screen.
     *
     * A small wrapper so the numbering above can be written as a mapping:
     * TaskList.get throws a checked exception, which a lambda cannot pass on,
     * and the numbers used are always in range.
     *
     * @param tasks      the tasks being shown
     * @param taskNumber the task's position, from 1
     * @return that task's text
     */
    private static String describe(TaskList tasks, int taskNumber) {
        try {
            return tasks.get(taskNumber).toString();
        } catch (GoatException e) {
            // Unreachable: rangeClosed(1, size) never leaves the list.
            throw new IllegalStateException("task " + taskNumber + " vanished", e);
        }
    }
}
